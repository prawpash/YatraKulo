package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.auth_service.shared.application.command.VoidCommandHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
public class AddWorkspaceMemberCommandHandler implements VoidCommandHandler<AddWorkspaceMemberCommand> {

  private final WorkspaceMemberService workspaceMemberService;

  @Override
  @Transactional
  public void handler(AddWorkspaceMemberCommand command) {
    workspaceMemberService.addMember(
        command.workspaceId(),
        command.userId(),
        command.roleId(),
        Instant.now(),
        command.invokedBy()
    );
  }
}
