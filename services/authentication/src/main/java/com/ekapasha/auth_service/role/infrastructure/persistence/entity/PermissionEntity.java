package com.ekapasha.auth_service.role.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "permission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PermissionEntity {
  @Id
  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private String description;

  public PermissionEntity(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    PermissionEntity that = (PermissionEntity) o;
    return getCode() != null && Objects.equals(getCode(), that.getCode());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}