package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JPARoleRepository extends JpaRepository<RoleEntity, UUID> {
  Page<RoleEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String nameSearchTerm,
      String descriptionSearchTerm,
      Pageable pageable
  );

  long countByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String nameSearchTerm,
      String descriptionSearchTerms
  );
}