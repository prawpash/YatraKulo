package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
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
class ListWorkspaceMembersByWorkspaceQueryHandlerTest {

  @Mock private WorkspaceMemberReadRepository workspaceMemberReadRepository;
  @InjectMocks private ListWorkspaceMembersByWorkspaceQueryHandler handler;

  @Test
  void shouldDelegateToRepository() {
    DomainPage<WorkspaceMember> expected = page(List.of(workspaceMember()));
    DomainPageRequest request = new DomainPageRequest(0, 10);
    when(workspaceMemberReadRepository.findByWorkspaceIdAndSearch(WORKSPACE_ID, "search", request))
        .thenReturn(expected);

    DomainPage<WorkspaceMember> result =
        handler.handler(new ListWorkspaceMembersByWorkspaceQuery(WORKSPACE_ID, request, "search"));

    assertThat(result).isSameAs(expected);
  }
}
