package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListWorkspacesFilterDtoTest {

  @Test
  void shouldMapToPageRequest() {
    ListWorkspacesFilterDto dto = new ListWorkspacesFilterDto(2, 25, "search");

    assertThat(dto.toPageRequest()).isEqualTo(new DomainPageRequest(2, 25));
    assertThat(dto.search()).isEqualTo("search");
  }
}
