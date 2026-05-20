package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleWriteRepositoryImplTest {

  @Mock private JPARoleRepository jpaRoleRepository;

  @Test
  void shouldSaveMappedEntityAndReturnDomainObject() {
    RoleWriteRepositoryImpl repository = new RoleWriteRepositoryImpl(jpaRoleRepository);
    Role role = activeWorkspaceRole();
    when(jpaRoleRepository.save(any(RoleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Role result = repository.save(role);

    ArgumentCaptor<RoleEntity> captor = ArgumentCaptor.forClass(RoleEntity.class);
    verify(jpaRoleRepository).save(captor.capture());
    RoleEntity saved = captor.getValue();
    assertThat(saved.getId()).isEqualTo(ROLE_ID);
    assertThat(saved.getWorkspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(result.getName()).isEqualTo("Admin");
    assertThat(result.getDescription()).isEqualTo("Admin role");
  }
}
