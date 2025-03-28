package pureapps.tms.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pureapps.tms.exception.ConflictException;
import pureapps.tms.exception.ResourceNotFoundException;
import pureapps.tms.project.dto.ProjectCreateDTO;
import pureapps.tms.project.dto.ProjectDTO;
import pureapps.tms.project.dto.ProjectFilterDTO;
import pureapps.tms.project.dto.ProjectUpdateDTO;
import pureapps.tms.user.User;
import pureapps.tms.user.UserRepository;
import pureapps.tms.user.UserType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    @Transactional
    public ProjectDTO assignEmployeeToProject(UUID projectId, UUID userId) { // <-- Change return type to ProjectDTO

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getUserType() == UserType.ADMINISTRATOR) {
            throw new IllegalArgumentException("Cannot assign an ADMINISTRATOR to a project.");
        }

        project.getAssignedEmployees().add(user);
        log.info("Assigned user {} to project {}", userId, projectId);

        ProjectDTO updatedProjectDTO = projectMapper.toProjectDTO(project);
        projectRepository.saveAndFlush(project);
        // Set placeholder budget util (will be calculated later)
        updatedProjectDTO.setBudgetUtilizationPercentage(BigDecimal.ZERO); // Or existing calculation logic

        return updatedProjectDTO;
    }


    @Transactional
    public void unassignEmployeeFromProject(final UUID projectId, final UUID userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        boolean removed = project.getAssignedEmployees().remove(user);

        if (removed) {
            log.info("Unassigned user {} from project {}", userId, projectId);
        } else {
            log.warn("User {} was not found in assignments for project {}", userId, projectId);
        }
    }

    @Transactional
    public ProjectDTO createProject(final ProjectCreateDTO createDTO) {
        projectRepository.findByName(createDTO.getName()).ifPresent(existing -> {
            throw new ConflictException("Project name already exists: " + createDTO.getName());
        });

        if (createDTO.getEndDate().isBefore(createDTO.getStartDate())) {
            throw new ConflictException("End date cannot be before start date.");
        }

        Project project = projectMapper.toProject(createDTO);

        Project initiallySavedProject = projectRepository.saveAndFlush(project);

        // Re-fetch to get DB-generated values (ID, createdAt, updatedAt)
        // Essential because save() doesn't guarantee returning DB defaults in the object
        UUID generatedId = initiallySavedProject.getId();
        Project savedProject = projectRepository.findById(generatedId)
                .orElseThrow(() -> new ResourceNotFoundException("Failed to fetch newly created project with id: " + generatedId));

        ProjectDTO resultDTO = projectMapper.toProjectDTO(savedProject);

        // TODO:. Placeholder for budget calculation (set to 0 or null initially)
        resultDTO.setBudgetUtilizationPercentage(BigDecimal.ZERO);

        return resultDTO;
    }

    @Transactional
    public ProjectDTO updateProject(final UUID id, final ProjectUpdateDTO updateDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (updateDTO.getName() != null && !updateDTO.getName().equals(project.getName())) {
            projectRepository.findByName(updateDTO.getName()).ifPresent(existing -> {
                throw new ConflictException("Project name already exists: " + updateDTO.getName());
            });
            project.setName(updateDTO.getName());
        }

        if (updateDTO.getDescription() != null) {
            project.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStartDate() != null) {
            project.setStartDate(updateDTO.getStartDate());
        }
        if (updateDTO.getEndDate() != null) {
            project.setEndDate(updateDTO.getEndDate());
        }
        if (project.getEndDate().isBefore(project.getStartDate())) {
            throw new ConflictException("End date cannot be before start date.");
        }

        if (updateDTO.getBudget() != null) {
            project.setBudget(updateDTO.getBudget());
        }

        projectRepository.saveAndFlush(project);
        return projectMapper.toProjectDTO(project);
    }

    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findProjectById(final UUID id) {
        return projectRepository.findById(id)
                .map(projectMapper::toProjectDTO)
                .map(dto -> {
                    //TODO: Calculate budget utilization percentage
                    dto.setBudgetUtilizationPercentage(BigDecimal.ZERO); // Placeholder
                    return dto;
                });
    }

    @Transactional
    public void deleteProject(final UUID id) {
        // Check if project exists before trying to delete
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Perform deletion
        projectRepository.delete(project);
        // Note: project_assignments rows related to this project will be deleted automatically
        // due to the ON DELETE CASCADE constraint we added in Liquibase.
    }

    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAllProjects(final ProjectFilterDTO filter, final Pageable pageable) {

        Specification<Project> spec = ProjectSpecifications.buildSpecification(filter);
        Page<Project> projectPage = projectRepository.findAll(spec, pageable);
        List<ProjectDTO> dtoList = projectMapper.toProjectDTOList(projectPage.getContent());

        // 4. Set placeholder for budget utilization
        // (Later, this loop would call a method to calculate the actual percentage)
        dtoList.forEach(dto -> {
            if (dto.getBudgetUtilizationPercentage() == null) {
                dto.setBudgetUtilizationPercentage(BigDecimal.ZERO);
            }
        });

        return new PageImpl<>(dtoList, pageable, projectPage.getTotalElements());
    }

    // TODO: --- Other methods to be added ---

    // private void calculateBudgetUtilization(ProjectDTO dto) { ... } // Needs Time Entries

}