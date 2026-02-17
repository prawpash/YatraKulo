package com.ekapasha.auth_service.user.infrastructure.security;

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
    return this.passwordEncoder.encode(password);
  }

  @Override
  public boolean verifyPassword(
      String password,
      String hashedPassword
  ) {
    return this.passwordEncoder.matches(password, hashedPassword);
  }
}
