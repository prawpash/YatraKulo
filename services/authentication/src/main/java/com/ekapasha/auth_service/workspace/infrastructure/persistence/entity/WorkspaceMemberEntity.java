package com.ekapasha.auth_service.workspace.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "workspace_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class WorkspaceMemberEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "workspace_id", nullable = false)
  private UUID workspaceId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "role_id", nullable = false)
  private UUID roleId;

  @Column(name = "added_at", nullable = false)
  private Instant addedAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "added_by")
  private UUID addedBy;

  @Column(name = "updated_by")
  private UUID updatedBy;

  public WorkspaceMemberEntity(
      UUID id,
      UUID workspaceId,
      UUID userId,
      UUID roleId,
      Instant addedAt,
      Instant updatedAt,
      UUID addedBy,
      UUID updatedBy) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.userId = userId;
    this.roleId = roleId;
    this.addedAt = addedAt;
    this.updatedAt = updatedAt;
    this.addedBy = addedBy;
    this.updatedBy = updatedBy;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    WorkspaceMemberEntity that = (WorkspaceMemberEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
