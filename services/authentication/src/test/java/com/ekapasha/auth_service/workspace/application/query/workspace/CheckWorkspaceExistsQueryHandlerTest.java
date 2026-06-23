package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckWorkspaceExistsQueryHandlerTest {

  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private CheckWorkspaceExistsQueryHandler handler;

  @Test
  void shouldReturnRepositoryResult() {
    when(workspaceReadRepository.existsByIdAndOwnerId(WORKSPACE_ID, OWNER_ID)).thenReturn(true);

    assertThat(handler.handler(new CheckWorkspaceExistsQuery(WORKSPACE_ID, OWNER_ID))).isTrue();
  }
}
