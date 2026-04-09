package com.ekapasha.auth_service.role.domain.enums;

public enum Permission {
//  Workspace
  WORKSPACE_UPDATE("workspace.update", "Update workspace"),
  WORKSPACE_DELETE("workspace.delete", "Delete workspace"),
  WORKSPACE_INVITE("workspace.invite", "Invite user to workspace"),

//  Account
  ACCOUNT_READ("account.read", "Read account"),
  ACCOUNT_UPDATE("account.update", "Update account"),
  ACCOUNT_CREATE("account.create", "Create account"),
  ACCOUNT_DELETE("account.delete", "Delete account");

  private final String code;
  private final String description;

  Permission(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public static Permission fromCode(String code){
    for (Permission permission : Permission.values()) {
      if (permission.getCode().equals(code)) {
        return permission;
      }
    }

    throw new IllegalArgumentException("Invalid permission code: " + code);
  }
}
