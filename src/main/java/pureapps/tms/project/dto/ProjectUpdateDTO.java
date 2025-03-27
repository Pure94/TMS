package pureapps.tms.project.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class ProjectUpdateDTO {

    @Size(max = 255)
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Positive(message = "Budget must be positive if provided")
    private BigDecimal budget;
}