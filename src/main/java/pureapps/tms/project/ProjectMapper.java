package pureapps.tms.project;


import org.springframework.stereotype.Component;
import pureapps.tms.project.dto.ProjectCreateDTO;
import pureapps.tms.project.dto.ProjectDTO;
import pureapps.tms.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class ProjectMapper {

    public ProjectDTO toProjectDTO(Project project) {
        if (project == null) {
            return null;
        }

        Set<UUID> employeeIds = Collections.emptySet();
        if (project.getAssignedEmployees() != null) {
            employeeIds = project.getAssignedEmployees().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
        }

        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .budget(project.getBudget())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .assignedEmployeeIds(employeeIds)
                .build();
    }

    public List<ProjectDTO> toProjectDTOList(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }
        return projects.stream()
                .map(this::toProjectDTO)
                .collect(Collectors.toList());
    }

    public Project toProject(ProjectCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        Project project = new Project();
        project.setName(createDTO.getName());
        project.setDescription(createDTO.getDescription());
        project.setStartDate(createDTO.getStartDate());
        project.setEndDate(createDTO.getEndDate());
        project.setBudget(createDTO.getBudget());
        return project;
    }

}