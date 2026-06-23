package com.ekapasha.auth_service.support;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public final class TestRsaKeys {
  private final String keyId;
  private final String publicKey;
  private final String privateKey;

  private TestRsaKeys(String keyId, String publicKey, String privateKey) {
    this.keyId = keyId;
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public static TestRsaKeys generate() {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      return new TestRsaKeys("test-key", toPublicPem(keyPair.getPublic()), toPrivatePem(keyPair.getPrivate()));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("RSA must be available in the test runtime", e);
    }
  }

  public String keyId() {
    return keyId;
  }

  public String publicKey() {
    return publicKey;
  }

  public String privateKey() {
    return privateKey;
  }

  private static String toPublicPem(PublicKey publicKey) {
    return wrapPem("PUBLIC KEY", publicKey.getEncoded());
  }

  private static String toPrivatePem(PrivateKey privateKey) {
    return wrapPem("PRIVATE KEY", privateKey.getEncoded());
  }

  private static String wrapPem(String type, byte[] encoded) {
    String body = Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(encoded);
    return "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----";
  }
}
