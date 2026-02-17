package com.ekapasha.auth_service.user.domain.service;

public interface PasswordService {
  public String hashPassword(String password);

  public boolean verifyPassword(String password, String hashedPassword);
}
