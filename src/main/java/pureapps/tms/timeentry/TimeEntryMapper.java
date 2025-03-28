package pureapps.tms.timeentry;

import org.springframework.stereotype.Component;
import pureapps.tms.timeentry.dto.TimeEntryCreateDTO;
import pureapps.tms.timeentry.dto.TimeEntryDTO;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
class TimeEntryMapper {

    public TimeEntryDTO toTimeEntryDTO(final TimeEntry timeEntry) {
        if (timeEntry == null) {
            return null;
        }

        long duration = 0L;
        if (timeEntry.getStartTime() != null && timeEntry.getEndTime() != null) {
            duration = Duration.between(timeEntry.getStartTime(), timeEntry.getEndTime()).toMinutes();
        }

        return TimeEntryDTO.builder()
                .id(timeEntry.getId())
                .projectId(timeEntry.getProject() != null ? timeEntry.getProject().getId() : null)
                .userId(timeEntry.getUser() != null ? timeEntry.getUser().getId() : null)
                .startTime(timeEntry.getStartTime())
                .endTime(timeEntry.getEndTime())
                .description(timeEntry.getDescription())
                .durationMinutes(duration)
                .build();
    }

    public List<TimeEntryDTO> toTimeEntryDTOList(final List<TimeEntry> timeEntries) {
        if (timeEntries == null) {
            return List.of();
        }
        return timeEntries.stream()
                .map(this::toTimeEntryDTO)
                .collect(Collectors.toList());
    }

    public TimeEntry toTimeEntry(final TimeEntryCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        TimeEntry timeEntry = new TimeEntry();
        // Project and User objects must be set in the service after fetching
        timeEntry.setStartTime(createDTO.getStartTime());
        timeEntry.setEndTime(createDTO.getEndTime());
        timeEntry.setDescription(createDTO.getDescription());
        return timeEntry;
    }
}