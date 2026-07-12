package com.emotions.emotions.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.emotions.emotions.entities.EmailCount;

public class EmailCountSpecifications {
    public static Specification<EmailCount> fromDate(LocalDate from) {
        if (from == null)
            return Specification.unrestricted();

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);

    }

    public static Specification<EmailCount> toDate(LocalDate to) {
        if (to == null)
            return Specification.unrestricted();

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), to);
    }
}
