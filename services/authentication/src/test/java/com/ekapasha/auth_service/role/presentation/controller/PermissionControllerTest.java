package com.ekapasha.auth_service.role.presentation.controller;

import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQueryHandler;
import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQuery;
import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQueryHandler;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.presentation.dto.permission.ListPermissionsFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.permission.PermissionResponseDto;
import com.ekapasha.shared.pagination.DomainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

  @Mock private ListPermissionsQueryHandler listPermissionsQueryHandler;
  @Mock private GetPermissionByCodeQueryHandler getPermissionByCodeQueryHandler;
  @InjectMocks private PermissionController controller;

  @Test
  void shouldListPermissions() {
    when(listPermissionsQueryHandler.handler(any()))
        .thenReturn(new DomainPage<>(List.of(Permission.WORKSPACE_DELETE), 1, 1, 0, 1));

    var response = controller.listPermissions(new ListPermissionsFilterDto(0, 10, "workspace"));

    ArgumentCaptor<ListPermissionsQuery> captor = ArgumentCaptor.forClass(ListPermissionsQuery.class);
    verify(listPermissionsQueryHandler).handler(captor.capture());
    assertThat(captor.getValue().search()).isEqualTo("workspace");
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).singleElement()
        .satisfies(dto -> assertThat(dto).isEqualTo(PermissionResponseDto.from(Permission.WORKSPACE_DELETE)));
  }

  @Test
  void shouldGetPermissionByCode() {
    when(getPermissionByCodeQueryHandler.handler(any())).thenReturn(Permission.ACCOUNT_READ);

    var response = controller.getPermissionByCode("account.read");

    assertThat(response.getBody()).isEqualTo(PermissionResponseDto.from(Permission.ACCOUNT_READ));
  }
}
