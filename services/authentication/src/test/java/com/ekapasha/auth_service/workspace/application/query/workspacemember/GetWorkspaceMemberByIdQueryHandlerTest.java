package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWorkspaceMemberByIdQueryHandlerTest {

  @Mock private WorkspaceMemberReadRepository workspaceMemberReadRepository;
  @InjectMocks private GetWorkspaceMemberByIdQueryHandler handler;

  @Test
  void shouldReturnMemberWhenFound() {
    WorkspaceMember member = workspaceMember();
    when(workspaceMemberReadRepository.findById(MEMBER_ID)).thenReturn(java.util.Optional.of(member));

    WorkspaceMember result = handler.handler(new GetWorkspaceMemberByIdQuery(MEMBER_ID));

    assertThat(result).isSameAs(member);
  }

  @Test
  void shouldThrowWhenMissing() {
    when(workspaceMemberReadRepository.findById(MEMBER_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new GetWorkspaceMemberByIdQuery(MEMBER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace member not found");
  }
}
