package com.stgo.taostyle.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Customize {

    @NotNull
    private String cusKey;

    @NotNull
    private String cusValue;

    @ManyToOne
    private Person person;

    private int recordStatus;

    public static com.stgo.taostyle.domain.Customize findCustomizeByKeyAndPerson(
            String cusKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Customize> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Customize AS o WHERE o.cusKey = :pCusKey and o.person = :person",
                        Customize.class);
        tQuery = tQuery.setParameter("pCusKey", cusKey);
        tQuery = tQuery.setParameter("person", person);
        try {
            return tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Customize> findAllCustomizesByPerson(
            Person person) {
        TypedQuery<Customize> query =
                entityManager().createQuery("SELECT o FROM Customize o where o.person = :person ORDER BY o.cusKey",
                        Customize.class);
        query.setParameter("person", person);
        return query.getResultList();
    }

    public static List<Customize> findCustomizeEntries(
            int firstResult,
            int maxResults) {
        return entityManager().createQuery("SELECT o FROM Customize o ORDER BY o.cusKey", Customize.class)
                .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public static List<Customize> findAllOrderedCustomizesByPerson(
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM Customize o where o.person = :person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Customize> query = entityManager().createQuery(jpaQuery, Customize.class);
        query.setParameter("person", person);
        return query.getResultList();
    }

    @Transactional
    public static long countCustomizesByPerson(
            Person person) {
        return findAllCustomizesByPerson(person).size();
    }

    @Transactional
    public static List<Customize> findOrderedCustomizeEntriesByPerson(
            int firstResult,
            int maxResults,
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM Customize o where o.person = :person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Customize> query = entityManager().createQuery(jpaQuery, Customize.class);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        query.setParameter("person", person);
        return query.getResultList();
    }
}
