package pureapps.tms.user;

import org.springframework.stereotype.Component;
import pureapps.tms.user.dto.UserCreateDTO;
import pureapps.tms.user.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
class UserMapper {

    public UserDTO toUserDTO(final User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .hourlyRate(user.getHourlyRate())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public List<UserDTO> toUserDTOList(final List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    public User toUser(final UserCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        User user = new User();
        user.setLogin(createDTO.getLogin());
        user.setFirstName(createDTO.getFirstName());
        user.setLastName(createDTO.getLastName());
        user.setEmail(createDTO.getEmail());
        user.setUserType(createDTO.getUserType());
        user.setHourlyRate(createDTO.getHourlyRate());
        //IMPORTANT :DO NOT set passwordHash here - it's handled in the service
        return user;
    }
}