package com.emotions.emotions.impls;

import com.emotions.emotions.repositories.EmailRepository;

import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.method.P;
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
    private final EmailRepository emailRepository;

    @Autowired
    EmailCountRepository emailCountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    EmailCountImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public EmailCount getLastCount() {
        return emailCountRepository.getLastRecord();
    }

    public EmailCountDtoSum getSum(Specification<EmailCount> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmailCountDtoSum> query = cb.createQuery(EmailCountDtoSum.class);

        Root<EmailCount> root = query.from(EmailCount.class);

        Predicate predicate = spec.toPredicate(root, query, cb);

        if (predicate != null)
            query.where(predicate);

        query.select(cb.construct(EmailCountDtoSum.class,
                cb.coalesce(cb.sum(root.get("joy")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("sadness")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("anger")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("fear")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("disgust")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("surprise")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("nostalgia")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("intrigue")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("justice")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("contempt")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("anxiety")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("betrayal")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("repulsion")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("aversion")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("hate")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("sec_joy")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("sec_sadness")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("sec_anger")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("sec_disgust")).as(Long.class), 0L),
                cb.coalesce(cb.sum(root.get("sec_fear")).as(Long.class), 0L)));

        return entityManager.createQuery(query).getSingleResult();
    }

    public List<EmailCount> getEmailCounts(Specification<EmailCount> spec) {
        return emailCountRepository.findAll(spec);
    }

    public void incrementPrimaryOrCompound(EmailCount count, String emotion) {
        switch (emotion.toLowerCase()) {
            case "joy" -> count.setJoy(count.getJoy() + 1);
            case "sadness" -> count.setSadness(count.getSadness() + 1);
            case "anger" -> count.setAnger(count.getAnger() + 1);
            case "fear" -> count.setFear(count.getFear() + 1);
            case "disgust" -> count.setDisgust(count.getDisgust() + 1);
            case "surprise" -> count.setSurprise(count.getSurprise() + 1);
            case "nostalgia" -> count.setNostalgia(count.getNostalgia() + 1);
            case "intrigue" -> count.setIntrigue(count.getIntrigue() + 1);
            case "justice" -> count.setJustice(count.getJustice() + 1);
            case "contempt" -> count.setContempt(count.getContempt() + 1);
            case "anxiety" -> count.setAnxiety(count.getAnxiety() + 1);
            case "betrayal" -> count.setBetrayal(count.getBetrayal() + 1);
            case "repulsion" -> count.setRepulsion(count.getRepulsion() + 1);
            case "aversion" -> count.setAversion(count.getAversion() + 1);
            case "hate" -> count.setHate(count.getHate() + 1);
        }
    }

    public void incrementSecondary(EmailCount count, String emotion) {
        switch (emotion.toLowerCase()) {
            case "joy" -> count.setSec_joy(count.getSec_joy() + 1);
            case "sadness" -> count.setSec_sadness(count.getSec_sadness() + 1);
            case "anger" -> count.setSec_anger(count.getSec_anger() + 1);
            case "fear" -> count.setSec_fear(count.getSec_fear() + 1);
            case "disgust" -> count.setSec_disgust(count.getSec_disgust() + 1);
        }
    }

    public void decrementPrimaryOrCompound(EmailCount count, String emotion) {
        switch (emotion.toLowerCase()) {
            case "joy" -> count.setJoy(count.getJoy() - 1);
            case "sadness" -> count.setSadness(count.getSadness() - 1);
            case "anger" -> count.setAnger(count.getAnger() - 1);
            case "fear" -> count.setFear(count.getFear() - 1);
            case "disgust" -> count.setDisgust(count.getDisgust() - 1);
            case "surprise" -> count.setSurprise(count.getSurprise() - 1);
            case "nostalgia" -> count.setNostalgia(count.getNostalgia() - 1);
            case "intrigue" -> count.setIntrigue(count.getIntrigue() - 1);
            case "justice" -> count.setJustice(count.getJustice() - 1);
            case "contempt" -> count.setContempt(count.getContempt() - 1);
            case "anxiety" -> count.setAnxiety(count.getAnxiety() - 1);
            case "betrayal" -> count.setBetrayal(count.getBetrayal() - 1);
            case "repulsion" -> count.setRepulsion(count.getRepulsion() - 1);
            case "aversion" -> count.setAversion(count.getAversion() - 1);
            case "hate" -> count.setHate(count.getHate() - 1);
        }
    }

    public void decrementSecondary(EmailCount count, String emotion) {
        switch (emotion.toLowerCase()) {
            case "joy" -> count.setSec_joy(Math.max(0, count.getSec_joy() - 1));
            case "sadness" -> count.setSec_sadness(Math.max(0, count.getSec_sadness() - 1));
            case "anger" -> count.setSec_anger(Math.max(0, count.getSec_anger() - 1));
            case "fear" -> count.setSec_fear(Math.max(0, count.getSec_fear() - 1));
            case "disgust" -> count.setSec_disgust(Math.max(0, count.getSec_disgust() - 1));
        }
    }
}
