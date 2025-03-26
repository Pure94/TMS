package pureapps.tms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pureapps.tms.user.UserType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private UserType userType;
    private BigDecimal hourlyRate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}