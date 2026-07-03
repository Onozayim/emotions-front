package com.emotions.emotions.impls;


import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.EmailCount;
import com.emotions.emotions.entities.EmailCountDtoSum;
import com.emotions.emotions.repositories.EmailCountRepository;
import com.emotions.emotions.services.EmailCountService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class EmailCountImpl implements EmailCountService {
    @Autowired
    EmailCountRepository emailCountRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public EmailCount getLastCount() {
        return emailCountRepository.getLastRecord();
    }


    public EmailCountDtoSum getSum(Specification<EmailCount> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmailCountDtoSum> query = cb.createQuery(EmailCountDtoSum.class);

        Root<EmailCount> root = query.from(EmailCount.class);

        Predicate predicate = spec.toPredicate(root, query, cb);

        if(predicate != null)
            query.where(predicate);

        query.select(cb.construct(EmailCountDtoSum.class, 
            cb.coalesce(cb.sum(root.get("joy")).as(Long.class), 0L),
            cb.coalesce(cb.sum(root.get("sadness")).as(Long.class), 0L),
            cb.coalesce(cb.sum(root.get("anger")).as(Long.class), 0L),
            cb.coalesce(cb.sum(root.get("fear")).as(Long.class), 0L),
            cb.coalesce(cb.sum(root.get("love")).as(Long.class), 0L),
            cb.coalesce(cb.sum(root.get("surprise")).as(Long.class), 0L)
        )); 

        return entityManager.createQuery(query).getSingleResult();
    }
}
