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
class CheckWorkspaceMemberExistsQueryHandlerTest {

  @Mock private WorkspaceMemberReadRepository workspaceMemberReadRepository;
  @InjectMocks private CheckWorkspaceMemberExistsQueryHandler handler;

  @Test
  void shouldReturnRepositoryResult() {
    when(workspaceMemberReadRepository.existsByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(true);

    assertThat(handler.handler(new CheckWorkspaceMemberExistsQuery(WORKSPACE_ID, OTHER_USER_ID))).isTrue();
  }
}
