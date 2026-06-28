package com.ekapasha.ledger.infrastructure.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkspacePermissionJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${app.auth.service-url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Cache<String, List<String>> permissionsCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("No request context available");
            return new JwtAuthenticationToken(jwt, List.of());
        }
        
        HttpServletRequest request = attributes.getRequest();
        String workspaceId = request.getHeader("x-workspace-id");
        String authorizationHeader = request.getHeader("Authorization");
        String userId = jwt.getSubject();

        if (workspaceId == null || workspaceId.isEmpty()) {
            log.warn("Missing x-workspace-id header");
            return new JwtAuthenticationToken(jwt, List.of());
        }

        String cacheKey = userId + ":" + workspaceId;
        List<String> permissions = permissionsCache.get(cacheKey, key -> fetchPermissions(userId, workspaceId, authorizationHeader));

        Collection<GrantedAuthority> authorities = permissions != null ? permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()) : List.of();

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private List<String> fetchPermissions(String userId, String workspaceId, String authHeader) {
        try {
            log.debug("Fetching permissions for user {} in workspace {}", userId, workspaceId);
            String url = String.format("%s/api/v1/workspaces/%s/members/%s/permissions", authServiceUrl, workspaceId, userId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch permissions for user {} in workspace {}", userId, workspaceId, e);
            return List.of();
        }
    }
}
