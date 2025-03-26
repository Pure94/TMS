package pureapps.tms.user.dto;

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
public class UserFilterDTO {
    private String login;
    private String name; // Used for searching first OR last name
    private BigDecimal minRate;
    private BigDecimal maxRate;
    private UserType userType;
}