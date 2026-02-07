package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface JPAPermissionRepository extends ListPagingAndSortingRepository<PermissionEntity, String> {
  Optional<PermissionEntity> findByCode(String code);

  Page<PermissionEntity> findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String codeSearchTerm,
      String descriptionSearchTerm,
      Pageable pageable
  );
}