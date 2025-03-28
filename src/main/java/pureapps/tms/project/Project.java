package pureapps.tms.project;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pureapps.tms.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"assignedEmployees"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_project_name", columnNames = "name")
})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent //end date must always be now or future
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Positive(message = "Budget must be positive")
    @Column(nullable = false, precision = 19, scale = 2) // Match NUMERIC precision/scale
    private BigDecimal budget;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // --- Relationships ---
    @ManyToMany(fetch = FetchType.LAZY) // LAZY fetching is important for collections
    @JoinTable(
            name = "project_assignments", // Name of the join table
            joinColumns = @JoinColumn(name = "project_id"), // FK column in join table for Project
            inverseJoinColumns = @JoinColumn(name = "user_id") // FK column in join table for User
    )
    private Set<User> assignedEmployees = new HashSet<>(); // Owning side of the relationship

}