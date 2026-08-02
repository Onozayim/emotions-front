package com.emotions.emotions.specifications;

import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

import com.emotions.emotions.entities.Email;

public class EmailSpecification {

    public static Specification<Email> hasEmotion(String emotion) {
        if (emotion == null || emotion.isBlank())
            return Specification.unrestricted();

        return (root, query, cb) -> cb.or(
                cb.equal(root.get("emotion"), emotion),
                cb.equal(root.get("compoundEmotion"), emotion));
    }

    public static Specification<Email> fromDate(Date from) {
        if (from == null)
            return Specification.unrestricted();

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);

    }

    public static Specification<Email> toDate(Date to) {
        if (to == null)
            return Specification.unrestricted();

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), to);
    }
}
