package pureapps.tms.project;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import pureapps.tms.project.dto.ProjectFilterDTO;
import pureapps.tms.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
class ProjectSpecifications {

    public Specification<Project> buildSpecification(final ProjectFilterDTO filter) {
        Specification<Project> spec = Specification.where(null);

        if (filter == null) {
            return spec;
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and(ProjectSpecifications.nameContains(filter.getName()));
        }

        if (filter.getDateFrom() != null || filter.getDateTo() != null) {
            spec = spec.and(ProjectSpecifications.isActiveDuring(filter.getDateFrom(), filter.getDateTo()));
        }

        if (!CollectionUtils.isEmpty(filter.getUserIds())) {
            spec = spec.and(ProjectSpecifications.hasAssignedUsers(filter.getUserIds()));
        }

        if (filter.getBudgetExceeded() != null) {
            spec = spec.and(ProjectSpecifications.isBudgetExceeded(filter.getBudgetExceeded()));
        }

        return spec;
    }


    public static Specification<Project> nameContains(final String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification to find projects active within a given date range.
     * A project is active if its period overlaps with the filter range.
     * Condition: project.startDate <= dateTo AND project.endDate >= dateFrom
     */
    public static Specification<Project> isActiveDuring(final LocalDate dateFrom, final LocalDate dateTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), dateTo));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), dateFrom));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            } else {
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    /**
     * Creates a specification to find projects that have AT LEAST ONE of the specified users assigned.
     */
    public static Specification<Project> hasAssignedUsers(final List<UUID> userIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(userIds)) {
                return cb.conjunction(); // No user filter applied
            }
            Join<Project, User> userJoin = root.join("assignedEmployees", JoinType.INNER);
            query.distinct(true);
            return userJoin.get("id").in(userIds);
        };
    }

    /**
     * Creates a specification to find projects where the budget is exceeded.
     * Placeholder - Requires Time Entry data and calculation logic.
     */
    public static Specification<Project> isBudgetExceeded(final Boolean budgetExceeded) {
        return (root, query, cb) -> {
            if (budgetExceeded == null) {
                return cb.conjunction();
            }
            // TODO: Implement actual budget calculation logic later
            System.err.println("WARNING: Budget exceeded filter is not implemented yet.");
            if (Boolean.TRUE.equals(budgetExceeded)) {
                return cb.conjunction();
            } else {
                return cb.conjunction();
            }
        };
    }
}