package com.ekapasha.auth_service.shared.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rsa")
@Component
public class RsaProperties {
  private String privateKey; // PEM format
  private String publicKey; // PEM format
  private String keyId;
}
