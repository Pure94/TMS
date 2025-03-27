package pureapps.tms.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pureapps.tms.exception.ConflictException;
import pureapps.tms.exception.ResourceNotFoundException;
import pureapps.tms.project.dto.ProjectCreateDTO;
import pureapps.tms.project.dto.ProjectDTO;
import pureapps.tms.project.dto.ProjectUpdateDTO;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;


    @Transactional
    public ProjectDTO createProject(ProjectCreateDTO createDTO) {
        projectRepository.findByName(createDTO.getName()).ifPresent(existing -> {
            throw new ConflictException("Project name already exists: " + createDTO.getName());
        });

        if (createDTO.getEndDate().isBefore(createDTO.getStartDate())) {
            throw new ConflictException("End date cannot be before start date.");
        }

        Project project = projectMapper.toProject(createDTO);

        Project initiallySavedProject = projectRepository.save(project);

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
    public ProjectDTO updateProject(UUID id, ProjectUpdateDTO updateDTO) {
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

        // Changes are saved automatically on transaction commit (dirty checking)
        // No explicit projectRepository.save(project) needed

        return projectMapper.toProjectDTO(project);
    }

    // TODO: --- Other methods to be added ---

    // public void deleteProject(UUID id) { ... }
    // public Optional<ProjectDTO> findProjectById(UUID id) { ... }
    // public Page<ProjectDTO> findAllProjects(Specification<Project> spec, Pageable pageable) { ... }
    // public Specification<Project> buildSpecification(ProjectFilterDTO filter) { ... }
    // public void assignEmployeeToProject(UUID projectId, UUID userId) { ... }
    // public void unassignEmployeeFromProject(UUID projectId, UUID userId) { ... }
    // private void calculateBudgetUtilization(ProjectDTO dto) { ... } // Needs Time Entries

}