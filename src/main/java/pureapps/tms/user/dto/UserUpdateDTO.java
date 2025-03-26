package pureapps.tms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pureapps.tms.user.UserType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    // Login is typically not updated or handled separately
    // Password change should be handled via a dedicated endpoint/process

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName; // Nullable / Optional

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName; // Nullable / Optional

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email; // Nullable / Optional

    private UserType userType; // Nullable / Optional

    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate; // Nullable / Optional
}