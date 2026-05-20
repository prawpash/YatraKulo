package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListWorkspacesByOwnerQueryHandlerTest {

  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private ListWorkspacesByOwnerQueryHandler handler;

  @Test
  void shouldDelegateToRepository() {
    DomainPage<Workspace> expected = page(List.of(workspace()));
    DomainPageRequest request = new DomainPageRequest(0, 10);
    when(workspaceReadRepository.findByOwnerIdAndSearch(OWNER_ID, "search", request)).thenReturn(expected);

    DomainPage<Workspace> result =
        handler.handler(new ListWorkspacesByOwnerQuery(OWNER_ID, request, "search"));

    assertThat(result).isSameAs(expected);
  }
}
