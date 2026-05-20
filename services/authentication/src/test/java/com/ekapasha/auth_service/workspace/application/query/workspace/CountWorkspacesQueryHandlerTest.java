package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountWorkspacesQueryHandlerTest {

  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private CountWorkspacesQueryHandler handler;

  @Test
  void shouldReturnRepositoryCount() {
    when(workspaceReadRepository.count()).thenReturn(7L);

    assertThat(handler.handler(new CountWorkspacesQuery())).isEqualTo(7L);
  }
}
