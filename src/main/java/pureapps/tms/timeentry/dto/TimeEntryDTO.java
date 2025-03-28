package pureapps.tms.timeentry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDTO {
    private UUID id;
    private UUID projectId;
    private UUID userId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String description;
    private Long durationMinutes;
}