package com.ekapasha.auth_service.role.domain.entity;

import com.ekapasha.shared.validation.Preconditions;
import com.ekapasha.shared.exception.DomainRuleViolationException;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {
  @EqualsAndHashCode.Include
  private final UUID id;
  @Getter(AccessLevel.NONE)
  private final UUID workspaceId;
  private String name;
  private String description;

  private final Instant createdAt;
  private Instant updatedAt;
  @Getter(AccessLevel.NONE)
  private Instant deletedAt;

  @Getter(AccessLevel.NONE)
  private final UUID createdBy;
  @Getter(AccessLevel.NONE)
  private UUID updatedBy;
  @Getter(AccessLevel.NONE)
  private UUID deletedBy;

  @Builder()
  private Role(
      @NonNull UUID id,
      UUID workspaceId,
      @NonNull String name,
      String description,
      @NonNull Instant createdAt,
      @NonNull Instant updatedAt,
      Instant deletedAt,
      UUID createdBy,
      UUID updatedBy,
      UUID deletedBy
  ) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.name = Preconditions.requireNonBlank(name, "name");
    this.description = description;

    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.deletedAt = deletedAt;

    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    this.deletedBy = deletedBy;
  }

  public void rename(String name, Instant updatedAt, UUID updatedBy) {
    if (this.deletedAt != null) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    this.name = Preconditions.requireNonBlank(name, "name");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");

    if (updatedBy != null) {
      this.updatedBy = updatedBy;
    }
  }

  public void changeDescription(String description, Instant updatedAt, UUID updatedBy) {
    if (this.deletedAt != null) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.description = description;

    if (updatedBy != null) {
      this.updatedBy = updatedBy;
    }
  }

  public void delete(Instant deletedAt, UUID deletedBy) {
    if (this.deletedAt != null) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    if (deletedBy == null) {
      throw new IllegalArgumentException("deletedBy must not be null");
    }

    this.updatedAt = Objects.requireNonNull(deletedAt, "deletedAt");
    this.deletedAt = Objects.requireNonNull(deletedAt, "deletedAt");

    this.updatedBy = deletedBy;
    this.deletedBy = deletedBy;
  }

  public Optional<Instant> getDeletedAt() {
    return Optional.ofNullable(deletedAt);
  }

  public Optional<UUID> getWorkspaceId() {
    return Optional.ofNullable(workspaceId);
  }

  public Optional<UUID> getCreatedBy() {
    return Optional.ofNullable(createdBy);
  }

  public Optional<UUID> getUpdatedBy() {
    return Optional.ofNullable(updatedBy);
  }

  public Optional<UUID> getDeletedBy() {
    return Optional.ofNullable(deletedBy);
  }
}
