package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRoleByIdQueryHandlerTest {

  @Mock private RoleReadRepository roleReadRepository;
  @InjectMocks private GetRoleByIdQueryHandler handler;

  @Test
  void shouldReturnRoleWhenFound() {
    Role role = activeWorkspaceRole();
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(role));

    Role result = handler.handler(new GetRoleByIdQuery(ROLE_ID));

    assertThat(result).isSameAs(role);
  }

  @Test
  void shouldThrowWhenMissing() {
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new GetRoleByIdQuery(ROLE_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Role not found");
  }
}
