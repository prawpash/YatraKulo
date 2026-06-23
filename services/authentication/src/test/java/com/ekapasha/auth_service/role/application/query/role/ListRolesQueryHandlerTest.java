package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRolesQueryHandlerTest {

  @Mock private RoleReadRepository roleReadRepository;
  @InjectMocks private ListRolesQueryHandler handler;

  @Test
  void shouldDelegateWithFalseForNullFlags() {
    DomainPage<Role> expected = page(List.of(activeWorkspaceRole()));
    when(roleReadRepository.getRoles(null, false, "admin", new DomainPageRequest(0, 10), false))
        .thenReturn(expected);

    DomainPage<Role> result =
        handler.handler(new ListRolesQuery(null, null, "admin", new DomainPageRequest(0, 10), null));

    assertThat(result).isSameAs(expected);
  }

  @Test
  void shouldDelegateWithProvidedFlags() {
    DomainPage<Role> expected = page(List.of(activeWorkspaceRole()));
    DomainPageRequest request = new DomainPageRequest(2, 25);
    when(roleReadRepository.getRoles(WORKSPACE_ID, true, "search", request, true)).thenReturn(expected);

    DomainPage<Role> result =
        handler.handler(new ListRolesQuery(WORKSPACE_ID, true, "search", request, true));

    assertThat(result).isSameAs(expected);
  }
}
