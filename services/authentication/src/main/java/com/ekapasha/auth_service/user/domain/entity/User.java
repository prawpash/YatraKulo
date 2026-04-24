package com.ekapasha.auth_service.user.domain.entity;

import com.ekapasha.shared.Preconditions;

import java.time.Instant;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
  @EqualsAndHashCode.Include
  private final UUID id;
  private String name;
  private String username;
  private String email;
  private String hashedPassword;

  @Getter(AccessLevel.NONE)
  private String profilePictureURL;

  private final Instant createdAt;
  private Instant updatedAt;

  @Builder
  private User(
      @NonNull UUID id,
      @NonNull String name,
      @NonNull String username,
      @NonNull String email,
      @NonNull String hashedPassword,
      String profilePictureURL,
      @NonNull Instant createdAt,
      @NonNull Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.name = Preconditions.requireNonBlank(name, "name");
    this.username = Preconditions.requireNonBlank(username, "username");
    this.email = Preconditions.requireNonBlank(email, "email");
    this.hashedPassword = Preconditions.requireNonBlank(hashedPassword, "hashedPassword");

    this.profilePictureURL = profilePictureURL;

    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public void rename(String name,Instant updatedAt, UUID updatedBy) {
    this.name = Preconditions.requireNonBlank(name, "name");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public void changeEmail(String email, Instant updatedAt, UUID updatedBy) {
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.email = Preconditions.requireNonBlank(email, "email");
  }

  public void changeUsername(String username, Instant updatedAt, UUID updatedBy) {
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.username = Preconditions.requireNonBlank(username, "username");
  }

  public void changePassword(String hashedPassword, Instant updatedAt, UUID updatedBy) {
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.hashedPassword = Preconditions.requireNonBlank(hashedPassword, "hashedPassword");
  }

  public void changeProfilePictureURL(
      String profilePictureURL, Instant updatedAt, UUID updatedBy) {
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.profilePictureURL = profilePictureURL;
  }

  public Optional<String> getProfilePictureURL() {
    return Optional.ofNullable(profilePictureURL);
  }
}
