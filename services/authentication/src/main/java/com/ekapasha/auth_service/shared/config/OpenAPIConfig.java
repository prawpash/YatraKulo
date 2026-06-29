package com.ekapasha.auth_service.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Authentication Service",
            version = "1.0",
            description = "Authentication Service"))
@SecuritySchemes({
  @SecurityScheme(
      name = "BearerAuth",
      type = SecuritySchemeType.HTTP,
      scheme = "bearer",
      bearerFormat = "JWT"),
  @SecurityScheme(
      name = "Oauth2",
      type = SecuritySchemeType.OAUTH2,
      flows =
          @OAuthFlows(
              authorizationCode =
                  @OAuthFlow(
                      authorizationUrl = "http://localhost:5000/oauth2/authorize",
                      tokenUrl = "http://localhost:5000/oauth2/token",
                      scopes = {
                        @OAuthScope(name = "openid", description = "OpenID Connect Authentication")
                      })))
})
@Configuration
public class OpenAPIConfig {}
