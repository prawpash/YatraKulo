package com.ekapasha.auth_service.user.infrastructure.persistence.entity;

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
@Table(name = "\"user\"")
@NoArgsConstructor
@Getter
@ToString
public class UserEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false, length = 100)
  private String hashedPassword;

  @Column(name = "profile_picture_url")
  private String profilePictureURL;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public UserEntity(
      UUID id,
      String name,
      String username,
      String email,
      String hashedPassword,
      String profilePictureURL,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.email = email;
    this.hashedPassword = hashedPassword;
    this.profilePictureURL = profilePictureURL;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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
    UserEntity that = (UserEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
