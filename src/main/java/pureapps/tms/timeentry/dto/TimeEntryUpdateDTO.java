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
public class TimeEntryUpdateDTO {

    private UUID projectId;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private String description;
}