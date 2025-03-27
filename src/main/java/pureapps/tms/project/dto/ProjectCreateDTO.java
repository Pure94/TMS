package pureapps.tms.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {

    @NotBlank(message = "Project name cannot be blank")
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent
    private LocalDate endDate;

    @NotNull(message = "Budget cannot be null")
    @Positive(message = "Budget must be positive")
    private BigDecimal budget;
}