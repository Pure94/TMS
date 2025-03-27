package pureapps.tms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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



}