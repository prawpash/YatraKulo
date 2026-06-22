package com.ekapasha.auth_service.user.infrastructure.config;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {

    http.oauth2AuthorizationServer(
            (authorizationServer) -> {
              http.securityMatcher(authorizationServer.getEndpointsMatcher());
              authorizationServer.oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
            })
        .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
        // Redirect to the login page when not authenticated from the
        // authorization endpoint
        .exceptionHandling(
            (exceptions) ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) {
    http.securityMatcher("/api/**")
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> corsConfigurationSource())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
        .exceptionHandling(
            (exceptions) ->
                exceptions.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

    return http.build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
    http.authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers("/login", "/register", "/error", "/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        // Form login handles the redirect to the login page from the
        // authorization server filter chain
        .formLogin(form -> form.loginPage("/login").permitAll());

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource(RsaProperties rsaProperties) {
    RSAPublicKey publicKey = this.parseRSAPublicKey(rsaProperties.getPublicKey());
    RSAPrivateKey privateKey = this.parseRSAPrivateKey(rsaProperties.getPrivateKey());

    RSAKey rsaKey =
        new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(rsaProperties.getKeyId())
            .build();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
  }

  /**
   * Parses the public key from the PEM format.
   *
   * @param publicKey the public key in PEM format
   * @return the public key
   */
  private RSAPublicKey parseRSAPublicKey(String publicKey) {
    try {
      String publicKeyContent =
          publicKey
              .replace("-----BEGIN PUBLIC KEY-----", "")
              .replace("-----END PUBLIC KEY-----", "")
              .replaceAll("\\s", "");

      byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse public key", e);
    }
  }

  /**
   * Parses the private key from the PEM format.
   *
   * @param privateKey the private key in PEM format
   * @return the private key
   */
  private RSAPrivateKey parseRSAPrivateKey(String privateKey) {
    try {
      String privateKeyContent =
          privateKey
              .replace("-----BEGIN PRIVATE KEY-----", "")
              .replace("-----END PRIVATE KEY-----", "")
              .replaceAll("\\s", "");

      byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse private key", e);
    }
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(
      UserReadRepository userReadRepository) {
    return context -> {
      User user =
          userReadRepository
              .findByUsername(context.getPrincipal().getName())
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));

      JwtClaimsSet.Builder claims = context.getClaims();

      claims.claim("sub", user.getId().toString());

      //      if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
      //
      //
      //      } else
      if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
        claims.claim("name", user.getName());
        claims.claim("username", user.getUsername());
        claims.claim("email", user.getEmail());
      }
    };
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }
}
