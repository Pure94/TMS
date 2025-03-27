package pureapps.tms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDTO {
    private String name;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private List<UUID> userIds;
    private Boolean budgetExceeded;
}