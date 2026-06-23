package com.ekapasha.auth_service.role.domain.entity;

import com.ekapasha.shared.exception.DomainRuleViolationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

  @Test
  void shouldRenameRoleAndUpdateAuditFields() {
    Role role = activeWorkspaceRole();

    role.rename("New name", DELETED_AT, OTHER_USER_ID);

    assertThat(role.getName()).isEqualTo("New name");
    assertThat(role.getUpdatedAt()).isEqualTo(DELETED_AT);
    assertThat(role.getUpdatedBy()).contains(OTHER_USER_ID);
  }

  @Test
  void shouldChangeDescriptionAndUpdateAuditFields() {
    Role role = activeWorkspaceRole();

    role.changeDescription("New description", DELETED_AT, OTHER_USER_ID);

    assertThat(role.getDescription()).isEqualTo("New description");
    assertThat(role.getUpdatedAt()).isEqualTo(DELETED_AT);
    assertThat(role.getUpdatedBy()).contains(OTHER_USER_ID);
  }

  @Test
  void shouldDeleteRoleAndMarkAuditFields() {
    Role role = activeWorkspaceRole();

    role.delete(DELETED_AT, OTHER_USER_ID);

    assertThat(role.getDeletedAt()).contains(DELETED_AT);
    assertThat(role.getDeletedBy()).contains(OTHER_USER_ID);
    assertThat(role.getUpdatedAt()).isEqualTo(DELETED_AT);
    assertThat(role.getUpdatedBy()).contains(OTHER_USER_ID);
  }

  @Test
  void shouldRejectMutationsAfterDelete() {
    Role role = deletedWorkspaceRole();

    assertThatThrownBy(() -> role.rename("New name", Instant.now(), OTHER_USER_ID))
        .isInstanceOf(DomainRuleViolationException.class);
    assertThatThrownBy(() -> role.changeDescription("New description", Instant.now(), OTHER_USER_ID))
        .isInstanceOf(DomainRuleViolationException.class);
  }

  @Test
  void shouldRejectDeleteWithoutDeletedBy() {
    Role role = activeWorkspaceRole();

    assertThatThrownBy(() -> role.delete(DELETED_AT, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("deletedBy must not be null");
  }
}
