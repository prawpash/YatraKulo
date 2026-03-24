package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.auth_service.shared.application.command.VoidCommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.shared.domain.exception.UnauthorizedAccessException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class RemoveWorkspaceMemberCommandHandler implements VoidCommandHandler<RemoveWorkspaceMemberCommand> {

  private final WorkspaceMemberService workspaceMemberService;
  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  @Transactional
  public void handler(RemoveWorkspaceMemberCommand command) {
    // Find the workspace
    Workspace workspace = workspaceReadRepository.findById(command.workspaceId())
        .orElseThrow(() -> new NotFoundException("Workspace not found."));

    boolean isOwner = workspace.getOwnerId().equals(command.invokedBy());
    boolean isSelfRemoval = command.userId().equals(command.invokedBy());

    // Only the workspace owner can remove members, or members can leave on their own
    if (!isOwner && !isSelfRemoval) {
      throw new UnauthorizedAccessException("Only the workspace owner can remove members, or members can leave on their own.");
    }

    workspaceMemberService.removeMember(
        command.workspaceId(),
        command.userId()
    );
  }
}
