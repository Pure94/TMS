package pureapps.tms.user.dto;

import jakarta.validation.constraints.*;
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
public class UserCreateDTO {

    @NotBlank(message = "Login cannot be blank")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    private String login;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotNull(message = "User type cannot be null")
    private UserType userType;

    @NotNull(message = "Hourly rate cannot be null")
    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    @NotBlank(message = "Password cannot be blank")
    // TODO: Add more specific password complexity rules if needed (e.g., @Pattern)
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
}
