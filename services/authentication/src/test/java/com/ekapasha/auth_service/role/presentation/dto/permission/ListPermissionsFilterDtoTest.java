package com.ekapasha.auth_service.role.presentation.dto.permission;

import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListPermissionsFilterDtoTest {

  @Test
  void shouldMapToListPermissionsQuery() {
    ListPermissionsFilterDto dto = new ListPermissionsFilterDto(1, 20, "account");

    var query = dto.toListPermissionsQuery();

    assertThat(query.search()).isEqualTo("account");
    assertThat(query.pageRequest()).isEqualTo(new DomainPageRequest(1, 20));
  }
}
