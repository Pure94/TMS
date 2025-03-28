package pureapps.tms.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {

    /**
     * Finds a project by its unique name. Useful for validation.
     * @param name The name of the project.
     * @return An Optional containing the project if found, otherwise empty.
     */
    Optional<Project> findByName(String name);

    @Override
    @EntityGraph(attributePaths = { "assignedEmployees" })
    Page<Project> findAll(@Nullable final Specification<Project> spec, final Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "assignedEmployees" })
    Page<Project> findAll(final Pageable pageable);
}