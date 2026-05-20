package com.ekapasha.auth_service.role.application.query.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPermissionByCodeQueryHandlerTest {

  @Mock private PermissionReadRepository permissionReadRepository;
  @InjectMocks private GetPermissionByCodeQueryHandler handler;

  @Test
  void shouldReturnPermissionWhenFound() {
    when(permissionReadRepository.findByCode("account.read"))
        .thenReturn(java.util.Optional.of(Permission.ACCOUNT_READ));

    Permission result = handler.handler(new GetPermissionByCodeQuery("account.read"));

    assertThat(result).isEqualTo(Permission.ACCOUNT_READ);
  }

  @Test
  void shouldThrowWhenMissing() {
    when(permissionReadRepository.findByCode("missing")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new GetPermissionByCodeQuery("missing")))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Permission not found");
  }
}
