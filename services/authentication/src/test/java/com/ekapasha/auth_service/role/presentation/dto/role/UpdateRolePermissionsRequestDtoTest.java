package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.ekapasha.auth_service.role.RoleTestFixtures.OTHER_USER_ID;
import static com.ekapasha.auth_service.role.RoleTestFixtures.ROLE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateRolePermissionsRequestDtoTest {

  @Test
  void shouldMapPermissionCodesToCommand() {
    UpdateRolePermissionsRequestDto dto =
        new UpdateRolePermissionsRequestDto(Set.of("workspace.update", "account.read"), Set.of("workspace.delete"));

    var command = dto.toCommand(ROLE_ID, OTHER_USER_ID);

    assertThat(command.roleId()).isEqualTo(ROLE_ID);
    assertThat(command.invokedBy()).isEqualTo(OTHER_USER_ID);
    assertThat(command.assign()).containsExactlyInAnyOrder(Permission.WORKSPACE_UPDATE, Permission.ACCOUNT_READ);
    assertThat(command.revoke()).containsExactly(Permission.WORKSPACE_DELETE);
  }

  @Test
  void shouldTreatNullSetsAsEmptySets() {
    UpdateRolePermissionsRequestDto dto = new UpdateRolePermissionsRequestDto(null, null);

    var command = dto.toCommand(ROLE_ID, OTHER_USER_ID);

    assertThat(command.assign()).isEmpty();
    assertThat(command.revoke()).isEmpty();
  }

  @Test
  void shouldRejectInvalidPermissionCode() {
    UpdateRolePermissionsRequestDto dto = new UpdateRolePermissionsRequestDto(Set.of("invalid"), Set.of());

    assertThatThrownBy(() -> dto.toCommand(ROLE_ID, OTHER_USER_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid permission code: invalid");
  }
}
