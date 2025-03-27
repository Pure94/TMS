package pureapps.tms.user;


import jakarta.persistence.*;
import lombok.*;
import pureapps.tms.project.Project;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"passwordHash" , "projects"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_login", columnNames = "login"),
                @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String login;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String email;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    // --- Relationships ---
    @ManyToMany(mappedBy = "assignedEmployees", fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    // Note: While @Column includes nullable=false, using validation annotations (@NotNull, @Email, @Size, @Positive)
    // on Data Transfer Objects (DTOs) is crucial for validating incoming API requests *before* attempting persistence.
}