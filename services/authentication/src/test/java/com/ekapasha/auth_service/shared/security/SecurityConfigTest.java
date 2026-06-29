package com.ekapasha.auth_service.shared.security;

import com.ekapasha.auth_service.user.UserTestFixtures;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;

import com.ekapasha.auth_service.shared.security.SecurityConfig;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;

import static com.ekapasha.auth_service.user.UserTestFixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

  @Test
  void shouldConfigureCorsAndJwtCustomizer() {
    SecurityConfig config = new SecurityConfig();
    CorsConfigurationSource source = config.corsConfigurationSource();
    CorsConfiguration configuration = java.util.Objects.requireNonNull(
        source.getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest()));

    assertThat(configuration.getAllowedOrigins()).contains("http://localhost:3000");
    assertThat(configuration.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "PATCH");
    assertThat(configuration.getAllowedHeaders()).contains("*");
    assertThat(configuration.getAllowCredentials()).isTrue();

    UserReadRepository userReadRepository = Mockito.mock(UserReadRepository.class);
    JwtEncodingContext context = Mockito.mock(JwtEncodingContext.class);
    Authentication principal = Mockito.mock(Authentication.class);
    JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
    when(principal.getName()).thenReturn("jane");
    when(context.getPrincipal()).thenReturn(principal);
    when(context.getClaims()).thenReturn(claims);
    when(context.getTokenType()).thenReturn(new OAuth2TokenType(OidcParameterNames.ID_TOKEN));
    when(userReadRepository.findByUsername("jane")).thenReturn(java.util.Optional.of(user()));

    config.jwtCustomizer(userReadRepository).customize(context);

    JwtClaimsSet built = claims.build();
    assertThat(built.getSubject()).isEqualTo(UserTestFixtures.USER_ID.toString());
    assertThat(built.getClaimAsString("name")).isEqualTo("Jane Doe");
    assertThat(built.getClaimAsString("username")).isEqualTo("jane");
    assertThat(built.getClaimAsString("email")).isEqualTo("jane@example.com");
  }

  @Test
  void shouldRejectMissingUserWhenCustomizingJwt() {
    SecurityConfig config = new SecurityConfig();
    UserReadRepository userReadRepository = Mockito.mock(UserReadRepository.class);
    JwtEncodingContext context = Mockito.mock(JwtEncodingContext.class);
    Authentication principal = Mockito.mock(Authentication.class);
    when(principal.getName()).thenReturn("missing");
    when(context.getPrincipal()).thenReturn(principal);
    when(context.getClaims()).thenReturn(JwtClaimsSet.builder());
    when(context.getTokenType()).thenReturn(new OAuth2TokenType(OidcParameterNames.ID_TOKEN));
    when(userReadRepository.findByUsername("missing")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> config.jwtCustomizer(userReadRepository).customize(context))
        .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
        .hasMessage("User not found");
  }

  @Test
  void shouldBuildJwkSourceAndDecoderFromRsaKeys() throws Exception {
    SecurityConfig config = new SecurityConfig();
    RsaProperties properties = new RsaProperties();
    KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
    properties.setPrivateKey(toPemPrivate((RSAPrivateKey) keyPair.getPrivate()));
    properties.setPublicKey(toPemPublic((RSAPublicKey) keyPair.getPublic()));
    properties.setKeyId("kid-1");

    JWKSource<SecurityContext> source = config.jwkSource(properties);
    List<JWK> jwks = source.get(new JWKSelector(new JWKMatcher.Builder().keyID("kid-1").build()), null);
    assertThat(jwks).singleElement().satisfies(jwk -> assertThat(jwk.getKeyID()).isEqualTo("kid-1"));

    assertThat(config.jwtDecoder(source)).isNotNull();
  }

  private static String toPemPublic(RSAPublicKey publicKey) {
    return "-----BEGIN PUBLIC KEY-----\n"
        + Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(publicKey.getEncoded())
        + "\n-----END PUBLIC KEY-----";
  }

  private static String toPemPrivate(RSAPrivateKey privateKey) {
    return "-----BEGIN PRIVATE KEY-----\n"
        + Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(privateKey.getEncoded())
        + "\n-----END PRIVATE KEY-----";
  }
}
