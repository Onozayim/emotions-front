package com.emotions.emotions.specifications;

import java.util.Date;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import com.emotions.emotions.entities.Email;

public class EmailSpecification {

    public static Specification<Email> hasEmotion(String emotion) {
    if (emotion == null || emotion.isBlank()) {
        return Specification.unrestricted();
    }

    return (root, query, cb) -> {
            Expression<String> primaryEmotion = cb.coalesce(
                    root.get("fixedEmotion"),
                    root.get("emotion")
            );

            Expression<String> compoundEmotion = cb.coalesce(
                    root.get("fixedCompoundEmotion"),
                    root.get("compoundEmotion")
            );

            Expression<String> secondaryEmotion = cb.coalesce(
                    root.get("fixedSecondaryEmotion"),
                    root.get("secondaryEmotion")
            );

            String selectedEmotion = emotion.trim().toLowerCase();

            return cb.or(
                    cb.equal(cb.lower(primaryEmotion), selectedEmotion),
                    cb.equal(cb.lower(compoundEmotion), selectedEmotion),
                    cb.equal(cb.lower(secondaryEmotion), selectedEmotion)
            );
        };
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
