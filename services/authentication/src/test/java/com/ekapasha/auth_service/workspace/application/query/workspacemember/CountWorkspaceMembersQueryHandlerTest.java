package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountWorkspaceMembersQueryHandlerTest {

  @Mock private WorkspaceMemberReadRepository workspaceMemberReadRepository;
  @InjectMocks private CountWorkspaceMembersQueryHandler handler;

  @Test
  void shouldReturnRepositoryCount() {
    when(workspaceMemberReadRepository.countByWorkspaceId(WORKSPACE_ID)).thenReturn(3L);

    assertThat(handler.handler(new CountWorkspaceMembersQuery(WORKSPACE_ID))).isEqualTo(3L);
  }
}
