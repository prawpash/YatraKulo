package com.ekapasha.auth_service.workspace.application.query.workspacepermission;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionReadRepository;
import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListWorkspacePermissionsQueryHandler
    implements QueryHandler<ListWorkspacePermissionsQuery, List<String>> {

  private final WorkspaceMemberReadRepository workspaceMemberReadRepository;
  private final RolePermissionReadRepository rolePermissionReadRepository;

  @Override
  public List<String> handler(ListWorkspacePermissionsQuery query) {
    WorkspaceMember member = workspaceMemberReadRepository
        .findByWorkspaceIdAndUserId(query.workspaceId(), query.userId())
        .orElseThrow(() -> new NotFoundException("User is not a member of this workspace"));

    List<RolePermission> rolePermissions = rolePermissionReadRepository.findByRoleId(member.getRoleId());

    return rolePermissions.stream()
        .map(rp -> rp.getPermission().getCode())
        .toList();
  }
}