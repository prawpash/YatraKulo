package com.ekapasha.shared.validation;

public final class Preconditions {
  private Preconditions() {}

  public static String requireNonBlank(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(name + " must not be blank");
    }
    return value;
  }
}
