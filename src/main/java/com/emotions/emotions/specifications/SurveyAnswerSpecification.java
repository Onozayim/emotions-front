package com.emotions.emotions.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyStatus;

public class SurveyAnswerSpecification {
    public static Specification<SurveyAnswer> hasStatus(SurveyStatus status) {
        return (root, query, cb) -> {

            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(
                root.get("status"),
                status
            );
        };
    }

    public static Specification<SurveyAnswer> hasEmotion(String emotion) {
        return (root, query, cb) -> {

            if (emotion == null || emotion.isBlank()) {
                return cb.conjunction();
            }

            return cb.equal(
                root.get("survey").get("emotion"),
                emotion
            );
        };
    }

    public static Specification<SurveyAnswer> hasEmail(String email) {
        return (root, query, cb) -> {

            if (email == null || email.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                cb.lower(root.get("email")),
                "%" + email.toLowerCase() + "%"
            );
        };
    }

    public static Specification<SurveyAnswer> fromDate(LocalDate from) {
        return (root, query, cb) -> {

            if (from == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                root.get("createdAt"),
                from.atStartOfDay()
            );
        };
    }

    public static Specification<SurveyAnswer> toDate(LocalDate to) {
        return (root, query, cb) -> {

            if (to == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                root.get("createdAt"),
                to.plusDays(1).atStartOfDay()
            );
        };
    }

    private SurveyAnswerSpecification() {
    }
}
