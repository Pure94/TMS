package pureapps.tms.user;

import jakarta.persistence.criteria.Predicate; // JPA Criteria API Predicate
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class UserSpecifications {

    public static Specification<User> loginContains(String login) {
        return (root, query, cb) -> {
            if (login == null || login.isBlank()) {
                return cb.conjunction(); // Always true if filter is empty/null
            }
            return cb.like(cb.lower(root.get("login")), "%" + login.toLowerCase() + "%");
        };
    }

    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    public static Specification<User> hourlyRateBetween(BigDecimal minRate, BigDecimal maxRate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minRate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("hourlyRate"), minRate));
            }
            if (maxRate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("hourlyRate"), maxRate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> hasUserType(UserType userType) {
        return (root, query, cb) -> {
            if (userType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("userType"), userType);
        };
    }
}