package com.ekapasha.auth_service.workspace.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceTest {

  @Test
  void shouldRenameWorkspace() {
    Workspace workspace = workspace();

    workspace.rename("New name", LATER);

    assertThat(workspace.getName()).isEqualTo("New name");
    assertThat(workspace.getUpdatedAt()).isEqualTo(LATER);
  }

  @Test
  void shouldUpdateDescription() {
    Workspace workspace = workspace();

    workspace.updateDescription("New description", LATER);

    assertThat(workspace.getDescription()).contains("New description");
    assertThat(workspace.getUpdatedAt()).isEqualTo(LATER);
  }

  @Test
  void shouldMarkAndUnsetDefault() {
    Workspace workspace = workspace();

    workspace.markAsDefault(LATER);
    assertThat(workspace.isDefault()).isTrue();
    assertThat(workspace.getUpdatedAt()).isEqualTo(LATER);

    workspace.unsetDefault(CREATED_AT);
    assertThat(workspace.isDefault()).isFalse();
    assertThat(workspace.getUpdatedAt()).isEqualTo(CREATED_AT);
  }

  @Test
  void shouldRejectBlankRename() {
    Workspace workspace = workspace();

    assertThatThrownBy(() -> workspace.rename(" ", Instant.now()))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
