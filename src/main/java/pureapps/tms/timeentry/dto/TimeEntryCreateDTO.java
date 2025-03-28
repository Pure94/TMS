package pureapps.tms.timeentry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

// TODO: Add class-level validator or service check: endTime > startTime
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryCreateDTO {

    @NotNull
    private UUID projectId;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    private String description;
}