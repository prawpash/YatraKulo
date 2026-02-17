package com.ekapasha.auth_service.user.infrastructure.security;

import com.ekapasha.auth_service.user.domain.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PasswordServiceAdapter implements PasswordEncoder {
  private final PasswordService passwordService;

  /**
   * @param rawPassword a password that has not been encoded. The value can be null in the event
   *     that the user has no password; in which case the result must be null.
   * @return
   */
  @Override
  public @Nullable String encode(@Nullable CharSequence rawPassword) {
    Objects.requireNonNull(rawPassword, "rawPassword cannot be null");

    return this.passwordService.hashPassword(rawPassword.toString());
  }

  /**
   * @param rawPassword the raw password to encode and match.
   * @param encodedPassword the encoded password from storage to compare with.
   * @return
   */
  @Override
  public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
    Objects.requireNonNull(rawPassword, "rawPassword cannot be null");

    return this.passwordService.verifyPassword(
        rawPassword.toString(), encodedPassword);
  }
}
