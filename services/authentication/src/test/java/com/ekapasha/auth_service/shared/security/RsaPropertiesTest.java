package com.ekapasha.auth_service.shared.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RsaPropertiesTest {

  @Test
  void shouldExposeGettersAndSetters() {
    RsaProperties properties = new RsaProperties();
    properties.setPrivateKey("private");
    properties.setPublicKey("public");
    properties.setKeyId("kid-1");

    assertThat(properties.getPrivateKey()).isEqualTo("private");
    assertThat(properties.getPublicKey()).isEqualTo("public");
    assertThat(properties.getKeyId()).isEqualTo("kid-1");
  }
}
