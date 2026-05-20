package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.PermissionEntity;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionReadRepositoryImplTest {

  @Mock private JPAPermissionRepository jpaPermissionRepository;

  @Test
  void shouldReturnAllPermissionsWhenSearchIsBlank() {
    PermissionReadRepositoryImpl repository = new PermissionReadRepositoryImpl(jpaPermissionRepository);
    PageImpl<PermissionEntity> page =
        new PageImpl<>(List.of(new PermissionEntity("workspace.update", "Update workspace")), PageRequest.of(0, 10), 1);
    when(jpaPermissionRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

    DomainPage<Permission> result = repository.getAll(" ", new DomainPageRequest(0, 10));

    assertThat(result.content()).containsExactly(Permission.WORKSPACE_UPDATE);
    assertThat(result.totalElements()).isEqualTo(1);
    verify(jpaPermissionRepository).findAll(PageRequest.of(0, 10));
    verify(jpaPermissionRepository, never()).findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(any(), any(), any());
  }

  @Test
  void shouldSearchPermissionsWhenSearchProvided() {
    PermissionReadRepositoryImpl repository = new PermissionReadRepositoryImpl(jpaPermissionRepository);
    PageImpl<PermissionEntity> page =
        new PageImpl<>(List.of(new PermissionEntity("account.read", "Read account")), PageRequest.of(1, 5), 2);
    when(jpaPermissionRepository.findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase("account", "account", PageRequest.of(1, 5)))
        .thenReturn(page);

    DomainPage<Permission> result = repository.getAll("account", new DomainPageRequest(1, 5));

    assertThat(result.content()).containsExactly(Permission.ACCOUNT_READ);
    assertThat(result.currentPage()).isEqualTo(1);
  }

  @Test
  void shouldMapFindByCode() {
    PermissionReadRepositoryImpl repository = new PermissionReadRepositoryImpl(jpaPermissionRepository);
    when(jpaPermissionRepository.findByCode("workspace.delete"))
        .thenReturn(Optional.of(new PermissionEntity("workspace.delete", "Delete workspace")));

    assertThat(repository.findByCode("workspace.delete")).contains(Permission.WORKSPACE_DELETE);
  }
}
