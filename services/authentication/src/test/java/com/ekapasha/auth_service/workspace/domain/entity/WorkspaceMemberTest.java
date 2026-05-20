package com.ekapasha.auth_service.workspace.domain.entity;

import com.ekapasha.shared.exception.DomainRuleViolationException;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceMemberTest {

  @Test
  void shouldChangeRoleWhenCallerMatchesAddedBy() {
    WorkspaceMember member = workspaceMember();

    member.changeRole(ROLE_ID_2, LATER, OWNER_ID);

    assertThat(member.getRoleId()).isEqualTo(ROLE_ID_2);
    assertThat(member.getUpdatedAt()).isEqualTo(LATER);
    assertThat(member.getUpdatedBy()).contains(OWNER_ID);
  }

  @Test
  void shouldRejectRoleChangeForOwner() {
    WorkspaceMember member = ownerMember();

    assertThatThrownBy(() -> member.changeRole(ROLE_ID_2, LATER, OWNER_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("You cannot change the role of an owner.");
  }

  @Test
  void shouldRejectRoleChangeWhenCallerDoesNotMatchAddedBy() {
    WorkspaceMember member = workspaceMember();

    assertThatThrownBy(() -> member.changeRole(ROLE_ID_2, LATER, OTHER_USER_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("You do not have permission to change the role of this user.");
  }
}
