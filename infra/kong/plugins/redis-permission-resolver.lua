local redis = require "resty.redis"
local http = require "resty.http"
local cjson = require "cjson"

local _M = {}

-- Configuration variables
local AUTH_SERVICE_BASE_URL = "http://host.docker.internal:5000"
local CACHE_TTL = 3600 -- 1 hour in seconds
local REDIS_HOST = os.getenv("KONG_REDIS_HOST") or "redis"
local REDIS_PORT = tonumber(os.getenv("KONG_REDIS_PORT")) or 6379

function _M.resolve()
    -- 1. Extract Workspace ID from the incoming request header
    local workspace_id = kong.request.get_header("X-Workspace-Id")
    if not workspace_id then
        kong.log.debug("No X-Workspace-Id header found. Skipping permission check.")
        return
    end

    -- 2. Extract User ID from the Kong JWT payload (injected by the Kong JWT plugin)
    local jwt_payload = kong.request.get_header("X-Kong-JWT-Claim-sub")
    if not jwt_payload then
        kong.log.err("No JWT claim found. Ensure JWT plugin is running before this script.")
        return
    end
    local user_id = jwt_payload

    -- 3. Connect to Redis
    local red = redis:new()
    red:set_timeout(1000) -- 1 second timeout

    local ok, err = red:connect(REDIS_HOST, REDIS_PORT)
    if not ok then
        kong.log.err("Failed to connect to Redis: ", err)
        return kong.response.exit(500, { message = "Internal Server Error - Cache unavailable" })
    end

    -- 4. Two-Tier Lookup
    local role_key = "user:" .. user_id .. ":workspace:" .. workspace_id .. ":role"
    local role_id, err = red:get(role_key)
    
    local permissions
    if role_id and role_id ~= ngx.null then
        local perm_key = "role:" .. role_id .. ":permissions"
        permissions = red:get(perm_key)
        if permissions == ngx.null then
            permissions = nil
        end
    end

    -- 5. Cache-Aside: Fetch from Auth Service on Cache Miss
    if not role_id or role_id == ngx.null or not permissions then
        kong.log.debug("Cache miss for user: ", user_id, " in workspace: ", workspace_id, ". Fetching from Auth service.")
        
        local auth_header = kong.request.get_header("Authorization")
        if not auth_header then
            kong.log.err("No Authorization header found for Auth Service call.")
            return kong.response.exit(401, { message = "Unauthorized - Missing credentials" })
        end

        local httpc = http.new()
        httpc:set_timeout(2000) -- 2 seconds timeout

        local auth_service_url = AUTH_SERVICE_BASE_URL .. "/api/v1/workspaces/" .. workspace_id .. "/members/" .. user_id .. "/role-permissions"
        local res, http_err = httpc:request_uri(auth_service_url, {
            method = "GET",
            headers = {
                ["Authorization"] = auth_header,
                ["Accept"] = "application/json",
            }
        })

        if not res then
            kong.log.err("HTTP request failed: ", http_err)
            return kong.response.exit(500, { message = "Internal Server Error - Auth Service unreachable" })
        end

        if res.status ~= 200 then
            kong.log.err("Auth service returned status: ", res.status, " body: ", res.body)
            if res.status == 404 then
                return kong.response.exit(403, { message = "Forbidden - Not a member of this workspace" })
            else
                return kong.response.exit(res.status, { message = "Failed to resolve permissions" })
            end
        end

        -- Parse Response DTO: { "roleId": "...", "permissions": ["read", "write"] }
        local success, val = pcall(cjson.decode, res.body)
        if not success or not val.roleId or not val.permissions then
            kong.log.err("Failed to parse JSON response: ", res.body)
            return kong.response.exit(500, { message = "Internal Server Error - Invalid Auth response" })
        end

        role_id = val.roleId
        local perm_list = val.permissions
        permissions = table.concat(perm_list, ",")

        -- Cache both back in Redis
        red:setex(role_key, CACHE_TTL, role_id)
        red:setex("role:" .. role_id .. ":permissions", CACHE_TTL, permissions)
    end

    -- 6. Inject the permissions and user ID into downstream request headers
    kong.service.request.set_header("X-User-Permissions", permissions)
    kong.service.request.set_header("X-User-Id", user_id)

    -- Inject shared secret for zero-trust
    local shared_secret = os.getenv("KONG_SHARED_SECRET")
    if shared_secret then
        kong.service.request.set_header("X-Kong-Secret", shared_secret)
    end

    -- Put Redis connection back into the pool
    red:set_keepalive(10000, 100)
end

return _M
