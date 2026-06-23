package com.ekapasha.auth_service.role.application.query.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ekapasha.auth_service.role.RoleTestFixtures.page;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPermissionsQueryHandlerTest {

  @Mock private PermissionReadRepository permissionReadRepository;
  @InjectMocks private ListPermissionsQueryHandler handler;

  @Test
  void shouldDelegateToRepository() {
    DomainPage<Permission> expected = page(List.of(Permission.ACCOUNT_READ));
    DomainPageRequest request = new DomainPageRequest(1, 20);
    when(permissionReadRepository.getAll("account", request)).thenReturn(expected);

    DomainPage<Permission> result = handler.handler(new ListPermissionsQuery("account", request));

    assertThat(result).isSameAs(expected);
  }
}
