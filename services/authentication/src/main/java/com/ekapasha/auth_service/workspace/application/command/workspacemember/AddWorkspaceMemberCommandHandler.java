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

import java.time.Instant;

@RequiredArgsConstructor
public class AddWorkspaceMemberCommandHandler
    implements VoidCommandHandler<AddWorkspaceMemberCommand> {

  private final WorkspaceMemberService workspaceMemberService;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final AppLogger logger = new AppLogger(AddWorkspaceMemberCommandHandler.class);

  @Override
  @Transactional
  public void handler(AddWorkspaceMemberCommand command) {
    try {
      // Find the workspace
      Workspace workspace =
          workspaceReadRepository
              .findById(command.workspaceId())
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
          command.invokedBy());

      this.logger.info(
          LogEvent.builder("Workspace member added successfully: " + command.userId())
              .eventName(AuthLogEvent.WORKSPACE_MEMBER_ADDED)
              .metadata("workspace.id", command.workspaceId().toString())
              .metadata("user.id", command.userId().toString())
              .metadata("role.id", command.roleId().toString())
              .build());
    } catch (Exception e) {
      String workspaceId =
          command.workspaceId() != null ? command.workspaceId().toString() : "null";
      String userId = command.userId() != null ? command.userId().toString() : "null";
      if (e instanceof NotFoundException || e instanceof UnauthorizedAccessException) {
        this.logger.warn(
            LogEvent.builder("Adding workspace member failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_MEMBER_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .metadata("user.id", userId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("Adding workspace member failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_MEMBER_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .metadata("user.id", userId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
