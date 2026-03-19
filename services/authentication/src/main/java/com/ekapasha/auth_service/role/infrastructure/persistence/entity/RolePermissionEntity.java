package com.ekapasha.auth_service.role.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "role_permissions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class RolePermissionEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "role_id", nullable = false)
  private UUID roleId;

  @Column(name = "permission_code", nullable = false)
  private String permissionCode;

  @Column(name = "added_at", nullable = false)
  private Instant addedAt;

  public RolePermissionEntity(UUID id, UUID roleId, String permissionCode, Instant addedAt) {
    this.id = id;
    this.roleId = roleId;
    this.permissionCode = permissionCode;
    this.addedAt = addedAt;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                                                                                 .getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                 .getPersistentClass()
        : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    RolePermissionEntity that = (RolePermissionEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                   .getPersistentClass()
                                                                   .hashCode() : getClass().hashCode();
  }
}
