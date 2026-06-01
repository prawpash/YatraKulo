package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.DomainRuleViolationException;
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
public class UpdateWorkspaceMemberRoleCommandHandler implements VoidCommandHandler<UpdateWorkspaceMemberRoleCommand> {

  private final WorkspaceMemberService workspaceMemberService;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final AppLogger logger = new AppLogger(UpdateWorkspaceMemberRoleCommandHandler.class);

  @Override
  @Transactional
  public void handler(UpdateWorkspaceMemberRoleCommand command) {
    try {
      // Find the workspace
      Workspace workspace = workspaceReadRepository.findById(command.workspaceId())
          .orElseThrow(() -> new NotFoundException("Workspace not found."));

      // Only the workspace owner can change member roles
      if (!workspace.getOwnerId().equals(command.invokedBy())) {
        throw new UnauthorizedAccessException("Only the workspace owner can change member roles.");
      }

      // Owner cannot change their own role
      if (command.userId().equals(command.invokedBy())) {
        throw new DomainRuleViolationException("Workspace owner cannot change their own role.");
      }

      workspaceMemberService.updateMemberRole(
          command.workspaceId(),
          command.userId(),
          command.roleId(),
          Instant.now(),
          command.invokedBy()
      );

      this.logger.info(
          LogEvent.builder("Workspace member role updated successfully: " + command.userId())
              .eventName(AuthLogEvent.WORKSPACE_MEMBER_ROLE_UPDATED)
              .metadata("workspaceId", command.workspaceId().toString())
              .metadata("userId", command.userId().toString())
              .metadata("roleId", command.roleId().toString())
              .build()
      );
    } catch (Exception e) {
      String workspaceId = command.workspaceId() != null ? command.workspaceId()
                                                                  .toString() : "null";
      String userId = command.userId() != null ? command.userId()
                                                              .toString() : "null";
      if (e instanceof NotFoundException || e instanceof UnauthorizedAccessException || e instanceof DomainRuleViolationException) {
        this.logger.warn(
            LogEvent.builder("Updating workspace member role failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_MEMBER_OPERATION_FAILED)
                .metadata("workspaceId", workspaceId)
                .metadata("userId", userId)
                .error(e)
                .build()
        );
      } else {
        this.logger.error(
            LogEvent.builder("Updating workspace member role failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_MEMBER_OPERATION_FAILED)
                .metadata("workspaceId", workspaceId)
                .metadata("userId", userId)
                .error(e)
                .build()
        );
      }
      throw e;
    }
  }
}
