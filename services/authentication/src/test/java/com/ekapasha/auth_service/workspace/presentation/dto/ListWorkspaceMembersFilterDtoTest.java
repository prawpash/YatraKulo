package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListWorkspaceMembersFilterDtoTest {

  @Test
  void shouldMapToPageRequest() {
    ListWorkspaceMembersFilterDto dto = new ListWorkspaceMembersFilterDto(1, 20, "search");

    assertThat(dto.toPageRequest()).isEqualTo(new DomainPageRequest(1, 20));
    assertThat(dto.search()).isEqualTo("search");
  }
}
