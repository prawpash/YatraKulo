package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class RemoveWorkspaceMemberCommandHandler
    implements VoidCommandHandler<RemoveWorkspaceMemberCommand> {

  private final WorkspaceMemberService workspaceMemberService;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final AppLogger logger = new AppLogger(RemoveWorkspaceMemberCommandHandler.class);

  @Override
  @Transactional
  public void handler(RemoveWorkspaceMemberCommand command) {
    try {
      // Find the workspace
      Workspace workspace =
          workspaceReadRepository
              .findById(command.workspaceId())
              .orElseThrow(() -> new NotFoundException("Workspace not found."));

      boolean isOwner = workspace.getOwnerId().equals(command.invokedBy());
      boolean isSelfRemoval = command.userId().equals(command.invokedBy());

      // Only the workspace owner can remove members, or members can leave on their own
      if (!isOwner && !isSelfRemoval) {
        throw new UnauthorizedAccessException(
            "Only the workspace owner can remove members, or members can leave on their own.");
      }

      workspaceMemberService.removeMember(command.workspaceId(), command.userId());

      this.logger.info(
          LogEvent.builder("Workspace member removed successfully: " + command.userId())
              .eventName(AuthLogEvent.WORKSPACE_MEMBER_REMOVED)
              .metadata("workspaceId", command.workspaceId().toString())
              .metadata("userId", command.userId().toString())
              .build());
    } catch (Exception e) {
      String workspaceId =
          command.workspaceId() != null ? command.workspaceId().toString() : "null";
      String userId = command.userId() != null ? command.userId().toString() : "null";
      if (e instanceof NotFoundException || e instanceof UnauthorizedAccessException) {
        this.logger.warn(
            LogEvent.builder("Removing workspace member failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_MEMBER_OPERATION_FAILED)
                .metadata("workspaceId", workspaceId)
                .metadata("userId", userId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder(
                    "Removing workspace member failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_MEMBER_OPERATION_FAILED)
                .metadata("workspaceId", workspaceId)
                .metadata("userId", userId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
