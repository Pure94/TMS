package pureapps.tms.user;


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

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<UserDTO> dtoList = userMapper.toUserDTOList(userPage.getContent());
        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByLogin(String login) {
        return userRepository.findByLogin(login).map(userMapper::toUserDTO);
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO createDTO) {

        userRepository.findByLogin(createDTO.getLogin()).ifPresent(user -> {
            throw new ConflictException("Login already exists: " + createDTO.getLogin());
        });
        userRepository.findByEmail(createDTO.getEmail()).ifPresent(user -> {
            throw new ConflictException("Email already exists: " + createDTO.getEmail());
        });

        User user = userMapper.toUser(createDTO);
        user.setPasswordHash(passwordEncoder.encode(createDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        // Find existing user or throw ResourceNotFoundException
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(existingUser.getEmail())) {
            userRepository.findByEmail(updateDTO.getEmail()).ifPresent(user -> {
                if (!user.getId().equals(existingUser.getId())) {
                    throw new ConflictException("Email already exists: " + updateDTO.getEmail());
                }
            });
        }

        userMapper.updateUserFromDTO(updateDTO, existingUser);
        return userMapper.toUserDTO(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public Specification<User> buildSpecification(UserFilterDTO filter) {
        Specification<User> spec = Specification.where(null); // Start with a non-null specification

        String login = filter.getLogin();
        String name = filter.getName(); // Could be first or last name
        java.math.BigDecimal minRate = filter.getMinRate();
        java.math.BigDecimal maxRate = filter.getMaxRate();
        UserType userType = filter.getUserType();

        if (login != null && !login.isBlank()) {
            spec = spec.and(UserSpecifications.loginContains(login));
        }
        if (name != null && !name.isBlank()) {
            // Combine first and last name search using OR
            spec = spec.and(UserSpecifications.firstNameContains(name).or(UserSpecifications.lastNameContains(name)));
        }
        if (minRate != null || maxRate != null) {
            spec = spec.and(UserSpecifications.hourlyRateBetween(minRate, maxRate));
        }
        if (userType != null) {
            spec = spec.and(UserSpecifications.hasUserType(userType));
        }

        return spec;
    }
}