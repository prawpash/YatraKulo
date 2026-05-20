package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleReadRepositoryImplTest {

  @Mock private JPARoleRepository jpaRoleRepository;

  @Test
  void shouldDelegateFindByIdAndMapResult() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    when(jpaRoleRepository.findById(ROLE_ID))
        .thenReturn(Optional.of(new RoleEntity(ROLE_ID, WORKSPACE_ID, "Admin", "Admin role", CREATED_AT, UPDATED_AT, null, CREATED_BY, UPDATED_BY, null)));

    assertThat(repository.findById(ROLE_ID)).hasValueSatisfying(role -> assertThat(role.getName()).isEqualTo("Admin"));
  }

  @Test
  void shouldReturnCountAndShortCircuitBlankSearches() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    when(jpaRoleRepository.count()).thenReturn(9L);

    assertThat(repository.count()).isEqualTo(9L);
    assertThat(repository.count(null)).isEqualTo(0L);
    assertThat(repository.count("  ")).isEqualTo(0L);

    verify(jpaRoleRepository).count();
    verify(jpaRoleRepository, never()).countByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(any(), any());
  }

  @Test
  void shouldDelegateSearchCountWhenSearchIsProvided() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    when(jpaRoleRepository.countByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(" admin ", " admin "))
        .thenReturn(3L);

    assertThat(repository.count(" admin ")).isEqualTo(3L);
  }

  @Test
  void shouldDelegateWorkspaceCounts() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    when(jpaRoleRepository.countByWorkspaceId(WORKSPACE_ID)).thenReturn(4L);
    when(jpaRoleRepository.countByWorkspaceIdAndSearchTerm(WORKSPACE_ID, "admin")).thenReturn(2L);

    assertThat(repository.countByWorkspaceId(WORKSPACE_ID)).isEqualTo(4L);
    assertThat(repository.countByWorkspaceId(WORKSPACE_ID, null)).isEqualTo(0L);
    assertThat(repository.countByWorkspaceId(WORKSPACE_ID, " ")).isEqualTo(0L);
    assertThat(repository.countByWorkspaceId(WORKSPACE_ID, "admin")).isEqualTo(2L);
  }

  @Test
  void shouldDelegateGlobalCounts() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    when(jpaRoleRepository.countByWorkspaceIdIsNull()).thenReturn(5L);
    when(jpaRoleRepository.countGlobalRolesBySearchTerm("global")).thenReturn(1L);

    assertThat(repository.countGlobalRoles()).isEqualTo(5L);
    assertThat(repository.countGlobalRoles(null)).isEqualTo(0L);
    assertThat(repository.countGlobalRoles(" ")).isEqualTo(0L);
    assertThat(repository.countGlobalRoles("global")).isEqualTo(1L);
  }

  @Test
  void shouldMapPagedResultsForWorkspaceAndGlobalSearch() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    RoleEntity roleEntity = new RoleEntity(ROLE_ID, WORKSPACE_ID, "Admin", "Admin role", CREATED_AT, UPDATED_AT, null, CREATED_BY, UPDATED_BY, null);
    PageImpl<RoleEntity> page = new PageImpl<>(List.of(roleEntity), PageRequest.of(1, 10), 11);
    when(jpaRoleRepository.searchWorkspaceRolesWithGlobal(WORKSPACE_ID, true, "admin", true, PageRequest.of(1, 10)))
        .thenReturn(page);

    DomainPage<Role> result =
        repository.getRoles(WORKSPACE_ID, true, " admin ", new DomainPageRequest(1, 10), true);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).getName()).isEqualTo("Admin");
    assertThat(result.totalElements()).isEqualTo(11);
    assertThat(result.currentPage()).isEqualTo(1);
    assertThat(result.pageSize()).isEqualTo(10);
  }

  @Test
  void shouldMapGlobalPagedResultsWhenWorkspaceIsNull() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    RoleEntity roleEntity = new RoleEntity(ROLE_ID, null, "Global Admin", "Global role", CREATED_AT, UPDATED_AT, null, CREATED_BY, UPDATED_BY, null);
    PageImpl<RoleEntity> page = new PageImpl<>(List.of(roleEntity), PageRequest.of(0, 20), 1);
    when(jpaRoleRepository.searchGlobalRolesBySearchTerm("", false, PageRequest.of(0, 20)))
        .thenReturn(page);

    DomainPage<Role> result =
        repository.getRoles(null, false, null, new DomainPageRequest(0, 20), false);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).getWorkspaceId()).isEmpty();
    assertThat(result.totalPages()).isEqualTo(1);
  }

  @Test
  void shouldDelegateFindByIdAndWorkspaceId() {
    RoleReadRepositoryImpl repository = new RoleReadRepositoryImpl(jpaRoleRepository);
    RoleEntity entity =
        new RoleEntity(
            ROLE_ID,
            WORKSPACE_ID,
            "Admin",
            "Admin role",
            CREATED_AT,
            UPDATED_AT,
            null,
            CREATED_BY,
            UPDATED_BY,
            null);
    when(jpaRoleRepository.findByIdAndWorkspaceId(ROLE_ID, WORKSPACE_ID))
        .thenReturn(Optional.of(entity));

    assertThat(repository.findByIdAndWorkspaceId(ROLE_ID, WORKSPACE_ID))
        .hasValueSatisfying(role -> assertThat(role.getId()).isEqualTo(ROLE_ID));
  }
}
