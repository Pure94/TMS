package pureapps.tms.timeentry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID>, JpaSpecificationExecutor<TimeEntry> {

    @EntityGraph(attributePaths = {"user"})
    Page<TimeEntry> findByUserId(UUID userId, Pageable pageable);

    List<TimeEntry> findByProjectId(UUID projectId);

}