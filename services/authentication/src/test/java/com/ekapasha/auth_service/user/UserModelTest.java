package com.ekapasha.auth_service.user;

import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommand;
import com.ekapasha.auth_service.user.application.command.user.UpdateUserProfileCommand;
import com.ekapasha.auth_service.user.application.query.GetUserByEmailQuery;
import com.ekapasha.auth_service.user.application.query.GetUserByIdQuery;
import com.ekapasha.auth_service.user.application.query.GetUserByUsernameQuery;
import com.ekapasha.auth_service.user.presentation.dto.RegisterUserRequest;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.user.UserTestFixtures.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {

  @Test
  void shouldExposeRecordFieldsForCommandsAndQueries() {
    RegisterUserCommand registerCommand =
        new RegisterUserCommand("Jane Doe", "jane", "jane@example.com", "password123", null);
    UpdateUserProfileCommand updateCommand =
        new UpdateUserProfileCommand(USER_ID, "Jane Doe", "jane", "jane@example.com", "https://example.com/avatar.png", USER_ID);
    GetUserByEmailQuery byEmailQuery = new GetUserByEmailQuery("jane@example.com");
    GetUserByIdQuery byIdQuery = new GetUserByIdQuery(USER_ID);
    GetUserByUsernameQuery byUsernameQuery = new GetUserByUsernameQuery("jane");
    RegisterUserRequest request =
        new RegisterUserRequest("Jane Doe", "jane", "jane@example.com", "password123", "https://example.com/avatar.png");

    assertThat(registerCommand.name()).isEqualTo("Jane Doe");
    assertThat(registerCommand.username()).isEqualTo("jane");
    assertThat(registerCommand.email()).isEqualTo("jane@example.com");
    assertThat(registerCommand.password()).isEqualTo("password123");

    assertThat(updateCommand.id()).isEqualTo(USER_ID);
    assertThat(updateCommand.profilePictureURL()).contains("https://example.com/avatar.png");
    assertThat(updateCommand.invokedBy()).isEqualTo(USER_ID);

    assertThat(byEmailQuery.email()).isEqualTo("jane@example.com");
    assertThat(byIdQuery.id()).isEqualTo(USER_ID);
    assertThat(byUsernameQuery.username()).isEqualTo("jane");

    assertThat(request.name()).isEqualTo("Jane Doe");
    assertThat(request.profilePictureURL()).isEqualTo("https://example.com/avatar.png");
  }
}
