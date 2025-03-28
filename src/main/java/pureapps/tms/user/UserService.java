package pureapps.tms.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pureapps.tms.exception.ConflictException;
import pureapps.tms.exception.ResourceNotFoundException;
import pureapps.tms.user.dto.UserCreateDTO;
import pureapps.tms.user.dto.UserDTO;
import pureapps.tms.user.dto.UserFilterDTO;
import pureapps.tms.user.dto.UserUpdateDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllUsers(final UserFilterDTO filterDTO, final Pageable pageable) {

        Specification<User> spec = UserSpecifications.buildSpecification(filterDTO);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<UserDTO> dtoList = userMapper.toUserDTOList(userPage.getContent());
        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(final UUID id) {
        return userRepository.findById(id).map(userMapper::toUserDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByLogin(final String login) {
        return userRepository.findByLogin(login).map(userMapper::toUserDTO);
    }

    @Transactional
    public UserDTO createUser(final UserCreateDTO createDTO) {

        userRepository.findByLogin(createDTO.getLogin()).ifPresent(user -> {
            throw new ConflictException("Login already exists: " + createDTO.getLogin());
        });
        userRepository.findByEmail(createDTO.getEmail()).ifPresent(user -> {
            throw new ConflictException("Email already exists: " + createDTO.getEmail());
        });

        User user = userMapper.toUser(createDTO);
        user.setPasswordHash(passwordEncoder.encode(createDTO.getPassword()));
        User managedUser = userRepository.save(user);
        // Force flush to execute INSERT and trigger @CreationTimestamp/DB default
        entityManager.flush();
        // Refresh the entity state from the database to get generated values
        entityManager.refresh(managedUser);
        return userMapper.toUserDTO(managedUser);
    }

    @Transactional
    public UserDTO updateUser(final UUID id, final UserUpdateDTO updateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(existingUser.getEmail())) {
            userRepository.findByEmail(updateDTO.getEmail()).ifPresent(user -> {
                if (!user.getId().equals(existingUser.getId())) {
                    throw new ConflictException("Email already exists: " + updateDTO.getEmail());
                }
            });
        }
        // Manually check each field in the DTO and update the entity if not null
        if (updateDTO.getFirstName() != null) {
            existingUser.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            existingUser.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getEmail() != null) {
            existingUser.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getUserType() != null) {
            existingUser.setUserType(updateDTO.getUserType());
        }
        if (updateDTO.getHourlyRate() != null) {
            existingUser.setHourlyRate(updateDTO.getHourlyRate());
        }
        // Force flush to execute UPDATE and trigger @UpdateTimestamp/DB default
        return userMapper.toUserDTO(userRepository.saveAndFlush(existingUser));
    }

    @Transactional
    public void deleteUser(final UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }


}