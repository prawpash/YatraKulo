package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
public class AddWorkspaceMemberCommandHandler implements VoidCommandHandler<AddWorkspaceMemberCommand> {

  private final WorkspaceMemberService workspaceMemberService;
  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  @Transactional
  public void handler(AddWorkspaceMemberCommand command) {
    // Find the workspace
    Workspace workspace = workspaceReadRepository.findById(command.workspaceId())
        .orElseThrow(() -> new NotFoundException("Workspace not found."));

    // Only the workspace owner can add new members
    if (!workspace.getOwnerId().equals(command.invokedBy())) {
      throw new UnauthorizedAccessException("Only the workspace owner can add new members.");
    }
    workspaceMemberService.addMember(
        command.workspaceId(),
        command.userId(),
        command.roleId(),
        Instant.now(),
        command.invokedBy()
    );
  }
}
