package pureapps.tms.timeentry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pureapps.tms.exception.ConflictException;
import pureapps.tms.exception.ResourceNotFoundException;
import pureapps.tms.project.Project;
import pureapps.tms.project.ProjectRepository;
import pureapps.tms.timeentry.dto.TimeEntryCreateDTO;
import pureapps.tms.timeentry.dto.TimeEntryDTO;
import pureapps.tms.timeentry.dto.TimeEntryUpdateDTO;
import pureapps.tms.user.User;
import pureapps.tms.user.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository; // Needed to get User from security context
    private final TimeEntryMapper timeEntryMapper;

    @Transactional
    public TimeEntryDTO createTimeEntry(final TimeEntryCreateDTO createDTO) {
        User currentUser = getCurrentUser();

        if (!createDTO.getEndTime().isAfter(createDTO.getStartTime())) {
            throw new ConflictException("End time must be after start time.");
        }

        Project project = projectRepository.findById(createDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + createDTO.getProjectId()));

        boolean isAssigned = project.getAssignedEmployees().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));
        if (!isAssigned) {
            throw new AccessDeniedException("User not assigned to the selected project.");
        }

        TimeEntry timeEntry = timeEntryMapper.toTimeEntry(createDTO);
        timeEntry.setUser(currentUser);
        timeEntry.setProject(project);

        TimeEntry initiallySaved = timeEntryRepository.save(timeEntry);
        TimeEntry savedEntry = timeEntryRepository.findById(initiallySaved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Failed to fetch newly created time entry with id: " + initiallySaved.getId()));

        return timeEntryMapper.toTimeEntryDTO(savedEntry);
    }

    @Transactional(readOnly = true)
    public Page<TimeEntryDTO> getMyTimeEntries(final Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<TimeEntry> entryPage = timeEntryRepository.findByUserId(currentUser.getId(), pageable);
        List<TimeEntryDTO> dtoList = timeEntryMapper.toTimeEntryDTOList(entryPage.getContent());
        return new PageImpl<>(dtoList, pageable, entryPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TimeEntryDTO getTimeEntryById(final UUID id) {
        User currentUser = getCurrentUser();
        TimeEntry timeEntry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found with id: " + id));

        if (!timeEntry.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this time entry.");
        }

        return timeEntryMapper.toTimeEntryDTO(timeEntry);
    }

    @Transactional
    public TimeEntryDTO updateTimeEntry(final UUID id, final TimeEntryUpdateDTO timeEntryUpdateDTO) {
        User currentUser = getCurrentUser();
        TimeEntry timeEntry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found with id: " + id));

        if (!timeEntry.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to update this time entry.");
        }
        boolean timeChanged = false;
        if (timeEntryUpdateDTO.getStartTime() != null) {
            timeEntry.setStartTime(timeEntryUpdateDTO.getStartTime());
            timeChanged = true;
        }
        if (timeEntryUpdateDTO.getEndTime() != null) {
            timeEntry.setEndTime(timeEntryUpdateDTO.getEndTime());
            timeChanged = true;
        }
        if (timeEntryUpdateDTO.getDescription() != null) {
            timeEntry.setDescription(timeEntryUpdateDTO.getDescription());
        }
        if (timeEntryUpdateDTO.getProjectId() != null && !timeEntryUpdateDTO.getProjectId().equals(timeEntry.getProject().getId())) {
            Project newProject = projectRepository.findById(timeEntryUpdateDTO.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("New project not found with id: " + timeEntryUpdateDTO.getProjectId()));
            boolean isAssigned = newProject.getAssignedEmployees().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));
            if (!isAssigned) {
                throw new AccessDeniedException("User not assigned to the new project.");
            }
            timeEntry.setProject(newProject);
        }
        if (timeChanged && !timeEntry.getEndTime().isAfter(timeEntry.getStartTime())) {
            throw new ConflictException("End time must be after start time after update.");
        }

        return timeEntryMapper.toTimeEntryDTO(timeEntry);
    }

    @Transactional
    public void deleteTimeEntry(final UUID id) {
        User currentUser = getCurrentUser();
        TimeEntry timeEntry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found with id: " + id));

        if (!timeEntry.getUser().getId().equals(currentUser.getId())) {
            // TODO: Allow ADMIN/MANAGER roles? Maybe.
            throw new AccessDeniedException("You do not have permission to delete this time entry.");
        }

        timeEntryRepository.delete(timeEntry);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("User is not authenticated.");
        }
        String currentLogin = authentication.getName();
        return userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in database: " + currentLogin));
    }
}