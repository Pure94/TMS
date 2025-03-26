package pureapps.tms.user;

import org.springframework.stereotype.Component;
import pureapps.tms.user.dto.UserCreateDTO;
import pureapps.tms.user.dto.UserDTO;
import pureapps.tms.user.dto.UserUpdateDTO;

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

    public void updateUserFromDTO(final UserUpdateDTO updateDTO, User user) {
        if (updateDTO == null || user == null) {
            return;
        }

        // Manually check each field in the DTO and update the entity if not null
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getUserType() != null) {
            user.setUserType(updateDTO.getUserType());
        }
        if (updateDTO.getHourlyRate() != null) {
            user.setHourlyRate(updateDTO.getHourlyRate());
        }
        // login, passwordHash, createdAt, id are not updated from this DTO
        // updatedAt is handled by @UpdateTimestamp
    }
}