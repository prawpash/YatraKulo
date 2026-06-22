package com.ekapasha.auth_service.user.presentation.controller;

import com.ekapasha.auth_service.role.infrastructure.persistence.repository.JPARoleRepository;
import com.ekapasha.auth_service.support.PostgresTestSupport;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.JPAUserRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.JPAWorkspaceMemberRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.JPAWorkspaceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
class AuthenticationFlowIntegrationTest extends PostgresTestSupport {
  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private JPAUserRepository userRepository;
  @Autowired private JPAWorkspaceRepository workspaceRepository;
  @Autowired private JPARoleRepository roleRepository;
  @Autowired private JPAWorkspaceMemberRepository workspaceMemberRepository;
  @Autowired private EntityManager entityManager;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private MockMvc mockMvc;

  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  void shouldRegisterUserAndDriveWorkspaceRoleMemberFlow() throws Exception {
    mockMvc
        .perform(
            post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Jane Doe")
                .param("username", "jane")
                .param("email", "jane@example.com")
                .param("password", "password123")
                .param("profilePictureURL", "https://example.com/avatar.png"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?registered=true"));

    UUID memberUserId = userRepository.findByUsername("jane").orElseThrow().getId();
    UUID ownerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    userRepository.saveAndFlush(
        new com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity(
            ownerId,
            "Owner User",
            "owner",
            "owner@example.com",
            "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
            null,
            Instant.parse("2026-05-01T00:00:00Z"),
            Instant.parse("2026-05-01T00:00:00Z")));
    JwtRequestPostProcessor ownerJwt = jwt().jwt(jwt -> jwt.subject(ownerId.toString()));

    MvcResult workspaceResult =
        mockMvc
            .perform(
                post("/api/v1/workspaces")
                    .with(ownerJwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "name": "Support Workspace",
                          "description": "Integration test workspace",
                          "isDefault": false
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn();

    JsonNode workspaceJson = objectMapper.readTree(workspaceResult.getResponse().getContentAsString());
    UUID workspaceId = UUID.fromString(workspaceJson.get("id").asText());

    mockMvc
        .perform(
            put("/api/v1/workspaces/{id}/default", workspaceId)
                .with(ownerJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isDefault\":true}"))
        .andExpect(status().isNoContent());

    entityManager.flush();
    entityManager.clear();

    assertThat(workspaceRepository.findByOwnerIdAndIsDefaultTrue(ownerId))
        .hasValueSatisfying(workspace -> assertThat(workspace.getId()).isEqualTo(workspaceId));

    MvcResult roleResult =
        mockMvc
            .perform(
                post("/api/v1/roles")
                    .with(ownerJwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "workspaceId": "%s",
                          "name": "Admin",
                          "description": "Workspace admin role"
                        }
                        """
                            .formatted(workspaceId)))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode roleJson = objectMapper.readTree(roleResult.getResponse().getContentAsString());
    UUID roleId = UUID.fromString(roleJson.get("id").asText());

    mockMvc
        .perform(
            post("/api/v1/workspaces/{id}/members", workspaceId)
                .with(ownerJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "userId": "%s",
                      "roleId": "%s"
                    }
                    """
                        .formatted(memberUserId, roleId)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/v1/workspaces/" + workspaceId + "/members/" + memberUserId));

    assertThat(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, memberUserId))
        .isPresent();
    assertThat(roleRepository.findById(roleId)).isPresent();
  }
}
