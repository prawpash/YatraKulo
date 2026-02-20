package com.ekapasha.auth_service.workspace.domain.entity;

import com.ekapasha.Preconditions;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Workspace {
  @EqualsAndHashCode.Include
  private final UUID id;
  private String name;
  private String description;
  private final UUID ownerId;
  private boolean isDefault;

  private final Instant createdAt;
  private Instant updatedAt;

  @Builder
  private Workspace(
      @NonNull UUID id,
      @NonNull String name,
      String description,
      @NonNull UUID ownerId,
      boolean isDefault,
      @NonNull Instant createdAt,
      @NonNull Instant updatedAt
  ) {
    this.id = Objects.requireNonNull(id, "id");
    this.name = Preconditions.requireNonBlank(name, "name");
    this.description = description;
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
    this.isDefault = isDefault;

    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public void rename(String name, Instant updatedAt) {
    this.name = Preconditions.requireNonBlank(name, "name");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public void updateDescription(String description, Instant updatedAt) {
    this.description = description;
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public void markAsDefault(Instant updatedAt) {
    this.isDefault = true;
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public void unsetDefault(Instant updatedAt) {
    this.isDefault = false;
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }
}
