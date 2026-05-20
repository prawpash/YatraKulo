package com.ekapasha.auth_service.workspace.domain.service;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberWriteRepository;
import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceMemberServiceTest {

  @Mock private WorkspaceMemberReadRepository workspaceMemberReadRepository;
  @Mock private WorkspaceMemberWriteRepository workspaceMemberWriteRepository;
  @InjectMocks private WorkspaceMemberService service;

  @Test
  void shouldAddMemberWhenMissing() {
    when(workspaceMemberReadRepository.existsByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(false);

    service.addMember(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID, CREATED_AT, OWNER_ID);

    ArgumentCaptor<WorkspaceMember> captor = ArgumentCaptor.forClass(WorkspaceMember.class);
    verify(workspaceMemberWriteRepository).save(captor.capture());
    WorkspaceMember saved = captor.getValue();
    assertThat(saved.getWorkspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(saved.getUserId()).isEqualTo(OTHER_USER_ID);
    assertThat(saved.getRoleId()).isEqualTo(ROLE_ID);
    assertThat(saved.getAddedBy()).contains(OWNER_ID);
    assertThat(saved.getUpdatedBy()).contains(OWNER_ID);
  }

  @Test
  void shouldSkipAddWhenMemberAlreadyExists() {
    when(workspaceMemberReadRepository.existsByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(true);

    service.addMember(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID, CREATED_AT, OWNER_ID);

    verify(workspaceMemberWriteRepository, never()).save(any());
  }

  @Test
  void shouldRemoveMemberWhenPresentAndNotOwner() {
    WorkspaceMember member = workspaceMember();
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(Optional.of(member));

    service.removeMember(WORKSPACE_ID, OTHER_USER_ID);

    verify(workspaceMemberWriteRepository).deleteByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID);
  }

  @Test
  void shouldRejectRemovingMissingMember() {
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.removeMember(WORKSPACE_ID, OTHER_USER_ID))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("User is not a member of this workspace.");
  }

  @Test
  void shouldRejectRemovingOwner() {
    WorkspaceMember owner = ownerMember();
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(Optional.of(owner));

    assertThatThrownBy(() -> service.removeMember(WORKSPACE_ID, OWNER_ID))
        .isInstanceOf(DomainRuleViolationException.class)
        .hasMessage("Cannot remove the owner of the workspace.");
  }

  @Test
  void shouldUpdateMemberRole() {
    WorkspaceMember member = workspaceMember();
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(Optional.of(member));

    service.updateMemberRole(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID_2, LATER, OWNER_ID);

    verify(workspaceMemberWriteRepository).save(member);
    assertThat(member.getRoleId()).isEqualTo(ROLE_ID_2);
    assertThat(member.getUpdatedAt()).isEqualTo(LATER);
    assertThat(member.getUpdatedBy()).contains(OWNER_ID);
  }

  @Test
  void shouldRejectUpdatingMissingMember() {
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateMemberRole(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID_2, LATER, OWNER_ID))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("User is not a member of this workspace.");
  }
}
