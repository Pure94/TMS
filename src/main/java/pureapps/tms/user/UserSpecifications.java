package pureapps.tms.user;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import pureapps.tms.user.dto.UserFilterDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
class UserSpecifications {

    public static Specification<User> loginContains(final String login) {
        return (root, query, cb) -> {
            if (login == null || login.isBlank()) {
                return cb.conjunction(); // Always true if filter is empty/null
            }
            return cb.like(cb.lower(root.get("login")), "%" + login.toLowerCase() + "%");
        };
    }

    public static Specification<User> firstNameContains(final String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<User> lastNameContains(final String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    public static Specification<User> hourlyRateBetween(final BigDecimal minRate, final BigDecimal maxRate) {
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

    public static Specification<User> hasUserType(final UserType userType) {
        return (root, query, cb) -> {
            if (userType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("userType"), userType);
        };
    }

    public Specification<User> buildSpecification(final UserFilterDTO filter) {
        Specification<User> spec = Specification.where(null);

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