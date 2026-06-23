package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class ListRolesFilterDtoTest {

  @Test
  void shouldMapNullFlagsToFalse() {
    ListRolesFilterDto dto = new ListRolesFilterDto(WORKSPACE_ID, 0, 10, null, null, "search");

    var query = dto.toListRolesQuery();

    assertThat(query.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(query.includeGlobal()).isFalse();
    assertThat(query.includeDeleted()).isFalse();
    assertThat(query.searchTerm()).isEqualTo("search");
    assertThat(query.pageRequest()).isEqualTo(new DomainPageRequest(0, 10));
  }

  @Test
  void shouldMapExplicitBooleanFlags() {
    ListRolesFilterDto dto = new ListRolesFilterDto(null, 2, 25, true, true, null);

    var query = dto.toListRolesQuery();

    assertThat(query.includeGlobal()).isTrue();
    assertThat(query.includeDeleted()).isTrue();
    assertThat(query.pageRequest()).isEqualTo(new DomainPageRequest(2, 25));
  }
}
