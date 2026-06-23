package com.ekapasha.auth_service.workspace.infrastructure.persistence;

import com.ekapasha.auth_service.workspace.WorkspaceTestFixtures;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper.WorkspaceMapper;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper.WorkspaceMemberMapper;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.JPAWorkspaceMemberRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.JPAWorkspaceRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.WorkspaceMemberReadRepositoryImpl;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.WorkspaceMemberWriteRepositoryImpl;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.WorkspaceReadRepositoryImpl;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.WorkspaceWriteRepositoryImpl;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceInfrastructureTest {

  @Mock private JPAWorkspaceRepository workspaceRepository;
  @Mock private JPAWorkspaceMemberRepository workspaceMemberRepository;

  @Test
  void shouldMapWorkspaceEntityAndRepositoryOperations() {
    WorkspaceEntity entity =
        new WorkspaceEntity(WORKSPACE_ID, "Workspace", "Description", OWNER_ID, false, CREATED_AT, UPDATED_AT);
    Workspace domain = WorkspaceMapper.toDomain(entity);
    assertThat(domain.getId()).isEqualTo(WORKSPACE_ID);
    assertThat(domain.getDescription()).contains("Description");

    WorkspaceEntity mappedBack = WorkspaceMapper.toEntity(domain);
    assertThat(mappedBack).isEqualTo(entity);

    WorkspaceReadRepositoryImpl readRepository = new WorkspaceReadRepositoryImpl(workspaceRepository);
    WorkspaceWriteRepositoryImpl writeRepository = new WorkspaceWriteRepositoryImpl(workspaceRepository);

    when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(entity));
    when(workspaceRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID)).thenReturn(Optional.of(entity));
    when(workspaceRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of(entity));
    when(workspaceRepository.findByOwnerId(OWNER_ID, PageRequest.of(0, 10)))
        .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
    when(workspaceRepository.findByOwnerIdAndNameContainingIgnoreCase(OWNER_ID, "search", PageRequest.of(0, 10)))
        .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
    when(workspaceRepository.findByOwnerIdAndIsDefaultTrue(OWNER_ID)).thenReturn(Optional.of(entity));
    when(workspaceRepository.existsByIdAndOwnerId(WORKSPACE_ID, OWNER_ID)).thenReturn(true);
    when(workspaceRepository.count()).thenReturn(7L);

    assertThat(readRepository.findById(WORKSPACE_ID)).hasValueSatisfying(workspace -> assertThat(workspace.getName()).isEqualTo("Workspace"));
    assertThat(readRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID)).isPresent();
    assertThat(readRepository.findByOwnerId(OWNER_ID)).hasSize(1);
    DomainPage<Workspace> page = readRepository.findByOwnerId(OWNER_ID, new DomainPageRequest(0, 10));
    assertThat(page.content()).singleElement().satisfies(workspace -> assertThat(workspace.getId()).isEqualTo(WORKSPACE_ID));
    DomainPage<Workspace> searchPage =
        readRepository.findByOwnerIdAndSearch(OWNER_ID, "search", new DomainPageRequest(0, 10));
    assertThat(searchPage.content()).hasSize(1);
    assertThat(readRepository.findDefaultByOwnerId(OWNER_ID)).isPresent();
    assertThat(readRepository.existsByIdAndOwnerId(WORKSPACE_ID, OWNER_ID)).isTrue();
    assertThat(readRepository.count()).isEqualTo(7L);

    when(workspaceRepository.save(any(WorkspaceEntity.class))).thenReturn(entity);
    Workspace saved = writeRepository.save(WorkspaceTestFixtures.workspace());
    assertThat(saved.getId()).isEqualTo(WORKSPACE_ID);
    writeRepository.deleteById(WORKSPACE_ID);
    writeRepository.setDefault(WORKSPACE_ID, OWNER_ID);
    verify(workspaceRepository).save(any(WorkspaceEntity.class));
    verify(workspaceRepository).deleteById(WORKSPACE_ID);
    verify(workspaceRepository).setDefault(WORKSPACE_ID, OWNER_ID);
  }

  @Test
  void shouldMapWorkspaceMemberEntityAndRepositoryOperations() {
    WorkspaceMemberEntity entity =
        new WorkspaceMemberEntity(MEMBER_ID, WORKSPACE_ID, OTHER_USER_ID, ROLE_ID, CREATED_AT, UPDATED_AT, OWNER_ID, OWNER_ID);
    WorkspaceMember domain = WorkspaceMemberMapper.toDomain(entity);
    assertThat(domain.getId()).isEqualTo(MEMBER_ID);
    assertThat(domain.getAddedBy()).contains(OWNER_ID);

    WorkspaceMemberEntity mappedBack = WorkspaceMemberMapper.toEntity(domain);
    assertThat(mappedBack).isEqualTo(entity);

    WorkspaceMemberReadRepositoryImpl readRepository = new WorkspaceMemberReadRepositoryImpl(workspaceMemberRepository);
    WorkspaceMemberWriteRepositoryImpl writeRepository = new WorkspaceMemberWriteRepositoryImpl(workspaceMemberRepository);

    when(workspaceMemberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(entity));
    when(workspaceMemberRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID)).thenReturn(Optional.of(entity));
    when(workspaceMemberRepository.findByWorkspaceId(WORKSPACE_ID, PageRequest.of(0, 10)))
        .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
    when(workspaceMemberRepository.findByWorkspaceIdAndUserSearch(WORKSPACE_ID, "jane", PageRequest.of(0, 10)))
        .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
    when(workspaceMemberRepository.findByUserId(OWNER_ID, PageRequest.of(0, 10)))
        .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
    when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID)).thenReturn(true);
    when(workspaceMemberRepository.countByWorkspaceId(WORKSPACE_ID)).thenReturn(4L);

    assertThat(readRepository.findById(MEMBER_ID)).isPresent();
    assertThat(readRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID)).isPresent();
    assertThat(readRepository.findByWorkspaceId(WORKSPACE_ID, new DomainPageRequest(0, 10))).isNotNull();
    DomainPage<WorkspaceMember> searchMemberPage =
        readRepository.findByWorkspaceIdAndSearch(WORKSPACE_ID, "jane", new DomainPageRequest(0, 10));
    assertThat(searchMemberPage.content()).hasSize(1);
    assertThat(readRepository.findByUserId(OWNER_ID, new DomainPageRequest(0, 10))).isNotNull();
    assertThat(readRepository.existsByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID)).isTrue();
    assertThat(readRepository.countByWorkspaceId(WORKSPACE_ID)).isEqualTo(4L);

    when(workspaceMemberRepository.save(any(WorkspaceMemberEntity.class))).thenReturn(entity);
    WorkspaceMember saved = writeRepository.save(WorkspaceTestFixtures.workspaceMember());
    assertThat(saved.getId()).isEqualTo(MEMBER_ID);
    writeRepository.deleteById(MEMBER_ID);
    writeRepository.deleteByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID);
    verify(workspaceMemberRepository).save(any(WorkspaceMemberEntity.class));
    verify(workspaceMemberRepository).deleteById(MEMBER_ID);
    verify(workspaceMemberRepository).deleteByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID);
  }
}
