package pureapps.tms.user; // Or user.repository

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {


    // --- Specification Executor Methods ---
    // (findOne(Specification<User>), findAll(Specification<User>), findAll(Specification<User>, Sort), etc.)
    // are inherited from JpaSpecificationExecutor

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

}