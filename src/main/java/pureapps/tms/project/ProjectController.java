package pureapps.tms.project;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pureapps.tms.exception.ResourceNotFoundException;
import pureapps.tms.project.dto.ProjectCreateDTO;
import pureapps.tms.project.dto.ProjectDTO;
import pureapps.tms.project.dto.ProjectFilterDTO;
import pureapps.tms.project.dto.ProjectUpdateDTO;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MANAGER')")
class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates a new project.
     * Accessible via: POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) {
        ProjectDTO createdProject = projectService.createProject(createDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProject.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdProject);
    }

    /**
     * Gets a project by its ID.
     * Accessible via: GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ProjectDTO getProjectById(@PathVariable UUID id) {
        return projectService.findProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    /**
     * Lists/filters projects with pagination.
     * Accessible via: GET /api/projects?name=...&page=0&size=10&sort=...
     */
    @GetMapping
    public Page<ProjectDTO> getAllProjects(ProjectFilterDTO filter, Pageable pageable) {
        return projectService.findAllProjects(filter, pageable);
    }

    /**
     * Updates an existing project.
     * Accessible via: PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ProjectDTO updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectUpdateDTO updateDTO) {
        return projectService.updateProject(id, updateDTO);
    }

    /**
     * Deletes a project.
     * Accessible via: DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }


    /**
     * Assigns an employee to a project.
     * Accessible via: POST /api/projects/{projectId}/employees/{userId}
     */
    @PostMapping("/{projectId}/employees/{userId}")
    public ResponseEntity<ProjectDTO> assignEmployee(@PathVariable UUID projectId, @PathVariable UUID userId) {
        return ResponseEntity.ok(projectService.assignEmployeeToProject(projectId, userId));
    }

    /**
     * Unassigns an employee from a project.
     * Accessible via: DELETE /api/projects/{projectId}/employees/{userId}
     */
    @DeleteMapping("/{projectId}/employees/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignEmployee(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.unassignEmployeeFromProject(projectId, userId);
    }
}