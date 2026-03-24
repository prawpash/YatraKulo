package com.ekapasha.auth_service.workspace.domain.entity;

import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkspaceMember {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final UUID workspaceId;
  private final UUID userId;
  private UUID roleId;

  private final Instant addedAt;
  private Instant updatedAt;

  @Getter(AccessLevel.NONE)
  private final UUID addedBy;
  
  @Getter(AccessLevel.NONE)
  private UUID updatedBy;

  @Builder
  private WorkspaceMember(
      @NonNull UUID id,
      @NonNull UUID workspaceId,
      @NonNull UUID userId,
      @NonNull UUID roleId,
      @NonNull Instant addedAt,
      @NonNull Instant updatedAt,
      UUID addedBy,
      UUID updatedBy
  ) {
    this.id = Objects.requireNonNull(id, "id");
    this.workspaceId = Objects.requireNonNull(workspaceId, "workspaceId");
    this.userId = Objects.requireNonNull(userId, "userId");
    this.roleId = Objects.requireNonNull(roleId, "roleId");

    this.addedAt = Objects.requireNonNull(addedAt, "addedAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");

    this.addedBy = addedBy;
    this.updatedBy = updatedBy;
  }

  public void changeRole(UUID roleId, Instant updatedAt, UUID updatedBy) {
    // not able to change the role of an owner
    if (this.addedBy == null) {
      throw new IllegalArgumentException("You cannot change the role of an owner.");
    }

    if(!updatedBy.equals(this.addedBy)){
      throw new IllegalArgumentException("You do not have permission to change the role of this user.");
    }

    this.roleId = Objects.requireNonNull(roleId, "roleId");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    this.updatedBy = Objects.requireNonNull(updatedBy, "updatedBy");
  }

  public Optional<UUID> getAddedBy(){
    return Optional.ofNullable(addedBy);
  }

  public Optional<UUID> getUpdatedBy(){
    return Optional.ofNullable(updatedBy);
  }

}
