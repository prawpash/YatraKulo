package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRoleCommandHandlerTest {

  @Mock private RoleWriteRepository roleWriteRepository;
  @Mock private RoleReadRepository roleReadRepository;
  @InjectMocks private UpdateRoleCommandHandler handler;

  @Test
  void shouldUpdateRoleNameAndDescriptionWhenCallerIsCreator() {
    Role role = activeWorkspaceRole("Old name", "Old description");
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(role));

    handler.handler(new UpdateRoleCommand(ROLE_ID, "New name", "New description", CREATED_BY));

    ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
    verify(roleWriteRepository).save(captor.capture());
    Role saved = captor.getValue();
    assertThat(saved.getName()).isEqualTo("New name");
    assertThat(saved.getDescription()).isEqualTo("New description");
    assertThat(saved.getUpdatedBy()).contains(CREATED_BY);
    assertThat(saved.getUpdatedAt()).isNotEqualTo(UPDATED_AT);
  }

  @Test
  void shouldNotChangeFieldsWhenIncomingValuesAreBlankOrSameAsCurrent() {
    Role role = activeWorkspaceRole("Same name", "Same description");
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(role));

    handler.handler(new UpdateRoleCommand(ROLE_ID, " ", null, CREATED_BY));

    verify(roleWriteRepository).save(role);
    assertThat(role.getName()).isEqualTo("Same name");
    assertThat(role.getDescription()).isEqualTo("Same description");
    assertThat(role.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(role.getUpdatedBy()).contains(UPDATED_BY);
  }

  @Test
  void shouldThrowWhenRoleMissing() {
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new UpdateRoleCommand(ROLE_ID, "New name", null, CREATED_BY)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Role not found.");
  }

  @Test
  void shouldThrowWhenRoleIsGlobal() {
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(globalRole()));

    assertThatThrownBy(() -> handler.handler(new UpdateRoleCommand(ROLE_ID, "New name", null, CREATED_BY)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Role not found.");
  }

  @Test
  void shouldThrowWhenCallerIsNotCreator() {
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(activeWorkspaceRole()));

    assertThatThrownBy(() -> handler.handler(new UpdateRoleCommand(ROLE_ID, "New name", null, OTHER_USER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Role not found.");
  }
}
