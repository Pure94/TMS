package pureapps.tms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private UUID id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    // Represent assigned employees simply by their IDs for now
    private Set<UUID> assignedEmployeeIds;
    private BigDecimal budgetUtilizationPercentage;
}