package com.ekapasha.auth_service.logging;

import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationLoggingListener {
  private final MeterRegistry meterRegistry;
  private final AppLogger logger = new AppLogger(AuthenticationLoggingListener.class);

  @EventListener
  public void handleSuccess(AuthenticationSuccessEvent event) {
    Authentication auth = event.getAuthentication();
    String className = auth.getClass().getName();

    if (className.contains("UsernamePasswordAuthenticationToken")) {
      this.meterRegistry.counter("auth.login.count", "status", "success").increment();
      this.logger.info(
          LogEvent.builder("User logged in successfully: " + auth.getName())
              .eventName(AuthLogEvent.USER_LOGIN_SUCCESS)
              .metadata("user.name", auth.getName())
              .build());
    } else {
      this.logger.info(
          LogEvent.builder("Token issued successfully for principal: " + auth.getName())
              .eventName(AuthLogEvent.TOKEN_GENERATION_SUCCESS)
              .metadata("principal", auth.getName())
              .metadata("authType", auth.getClass().getSimpleName())
              .build());
    }
  }

  @EventListener
  public void handleFailure(AbstractAuthenticationFailureEvent event) {
    Authentication auth = event.getAuthentication();
    String className = auth.getClass().getName();

    if (className.contains("UsernamePasswordAuthenticationToken")) {
      this.meterRegistry.counter("auth.login.count", "status", "failed").increment();
      this.logger.warn(
          LogEvent.builder("User login failed: " + auth.getName())
              .eventName(AuthLogEvent.USER_LOGIN_FAILED)
              .metadata("user.name", auth.getName())
              .error(event.getException())
              .build());
    } else {
      this.logger.error(
          LogEvent.builder("Token generation/auth failed for principal: " + auth.getName())
              .eventName(AuthLogEvent.TOKEN_GENERATION_FAILED)
              .metadata("principal", auth.getName())
              .metadata("authType", auth.getClass().getSimpleName())
              .error(event.getException())
              .build());
    }
  }
}
