package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
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
class GetWorkspaceByIdQueryHandlerTest {

  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private GetWorkspaceByIdQueryHandler handler;

  @Test
  void shouldReturnWorkspaceWhenFound() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    Workspace result = handler.handler(new GetWorkspaceByIdQuery(WORKSPACE_ID));

    assertThat(result).isSameAs(workspace);
  }

  @Test
  void shouldThrowWhenMissing() {
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new GetWorkspaceByIdQuery(WORKSPACE_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found");
  }
}
