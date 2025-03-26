package pureapps.tms.user;

import jakarta.validation.Valid; // For validating request bodies
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
// Import Pageable default/sort if needed, e.g.:
// import org.springframework.data.web.PageableDefault;
// import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pureapps.tms.user.dto.UserCreateDTO;
import pureapps.tms.user.dto.UserDTO;
import pureapps.tms.user.dto.UserFilterDTO;
import pureapps.tms.user.dto.UserUpdateDTO;

@RestController // Combines @Controller and @ResponseBody - simplifies REST endpoint creation
@RequestMapping("/api/users") // Base path for all endpoints in this controller
@RequiredArgsConstructor // Lombok: Creates constructor for final fields (UserService injection)
class UserController {

    private final UserService userService;

    /**
     * Endpoint to create a new user.
     * Accessible via: POST /api/users
     * @param createDTO Data for the new user from the request body.
     * @return The created user's data.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Return HTTP 201 Created on success
    // TODO: Add Security @PreAuthorize("hasRole('ADMINISTRATOR')")
    public UserDTO createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        // @Valid triggers validation defined in UserCreateDTO
        // @RequestBody maps the JSON request body to the DTO
        return userService.createUser(createDTO);
    }

    /**
     * Endpoint to get a user by their ID.
     * Accessible via: GET /api/users/{id}
     * @param id The ID of the user to retrieve.
     * @return The user's data.
     */
    @GetMapping("/{id}")
    // TODO: Add Security @PreAuthorize("hasRole('ADMINISTRATOR')") or allow users to get self?
    public UserDTO getUserById(@PathVariable Long id) {
        // @PathVariable extracts the 'id' from the URL path
        return userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id)); // TODO: Replace with specific ResourceNotFoundException and global handler
    }

    /**
     * Endpoint to get a list of users with filtering and pagination.
     * Accessible via: GET /api/users?login=...&name=...&minRate=...&maxRate=...&userType=...&page=0&size=20&sort=login,asc
     * @param filter DTO containing optional filter parameters (bound from request query parameters).
     * @param pageable Pagination and sorting information (bound from 'page', 'size', 'sort' query parameters).
     * @return A page of users matching the criteria.
     */
    @GetMapping
    // TODO: Add Security @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Page<UserDTO> getAllUsers(UserFilterDTO filter,
                                     //@PageableDefault(size = 20, sort = "login", direction = Sort.Direction.ASC) // Optional default paging/sorting
                                     Pageable pageable) {
        // Spring automatically maps query parameters matching field names in UserFilterDTO.
        // Spring automatically resolves 'page', 'size', 'sort' parameters into a Pageable object.

        Specification<User> spec = userService.buildSpecification(filter);
        return userService.findAllUsers(spec, pageable);
    }

    /**
     * Endpoint to update an existing user.
     * Accessible via: PUT /api/users/{id}
     * @param id The ID of the user to update.
     * @param updateDTO Data containing updates (fields can be optional).
     * @return The updated user's data.
     */
    @PutMapping("/{id}")
    // TODO: Add Security @PreAuthorize("hasRole('ADMINISTRATOR')")
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        return userService.updateUser(id, updateDTO);
    }

    /**
     * Endpoint to delete a user.
     * Accessible via: DELETE /api/users/{id}
     * @param id The ID of the user to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Return HTTP 204 No Content on success
    // TODO: Add Security @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}