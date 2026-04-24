package com.ekapasha.auth_service.user.infrastructure.security;

import com.ekapasha.shared.Preconditions;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {
  private final BCryptPasswordEncoder passwordEncoder;

  public PasswordServiceImpl() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public String hashPassword(String password) {
    // BCrypt has a 72-byte limit, validate before encoding
    if (password == null) {
      throw new IllegalArgumentException("password must not be null");
    }
    if (password.isBlank()) {
      throw new IllegalArgumentException("password must not be blank");
    }
    // BCrypt will throw IllegalArgumentException for passwords > 72 bytes
    return this.passwordEncoder.encode(password);
  }

  @Override
  public boolean verifyPassword(
      String password,
      String hashedPassword
  ) {
    // Validate inputs - return false for invalid inputs (safe default)
    if (password == null || password.isBlank()) {
      return false;
    }
    if (hashedPassword == null || hashedPassword.isBlank()) {
      return false;
    }

    return this.passwordEncoder.matches(password, hashedPassword);
  }
}
