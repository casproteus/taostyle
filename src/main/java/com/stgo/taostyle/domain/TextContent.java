package com.stgo.taostyle.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class TextContent {

    @NotNull
    private String posInPage;

    private String content;

    private String publisher;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date markDate;

    @ManyToOne
    private Person person;

    private int recordStatus;

    public static com.stgo.taostyle.domain.TextContent findContentsByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<TextContent> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TextContent AS o WHERE o.posInPage = :pKey and o.person = :person",
                        TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        TextContent textContent = null;
        try {
            textContent = tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return textContent;
    }

    public static String findTextByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<String> tQuery =
                tEntityManager.createQuery(
                        "SELECT o.content FROM TextContent AS o WHERE o.posInPage = :pKey and o.person = :person",
                        String.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        String text = null;
        try {
            text = tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return text;
    }

    public static List<TextContent> findContentsByMediaIdAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<TextContent> tQuery =
                tEntityManager
                        .createQuery(
                                "SELECT o FROM TextContent AS o WHERE o.posInPage = :pKey and o.person = :person ORDER BY o.markDate DESC",
                                TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        List<TextContent> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static List<TextContent> findAllMatchedTextContents(
            String pKey,
            String sorter,
            Person person) {
        EntityManager tEntityManager = entityManager();
        if (sorter == null) {
            sorter = " ORDER BY o.markDate DESC";
        } else {
            sorter = " ORDER BY o." + sorter;
        }

        TypedQuery<TextContent> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person" + sorter,
                        TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        List<TextContent> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static List<TextContent> findAllMatchingTextContents(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();

        TypedQuery<TextContent> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person",
                        TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        List<TextContent> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static List<String> findAllMatchedContent(
            String pKey,
            String field,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<String> tQuery =
                tEntityManager
                        .createQuery(
                                "SELECT o."
                                        + field
                                        + " FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person ORDER BY o.posInPage",
                                String.class);
        tQuery = tQuery.setParameter("pKey", pKey + "%");
        tQuery = tQuery.setParameter("person", person);
        List<String> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static long countTextContentsByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Long> tQuery =
                tEntityManager.createQuery(
                        "SELECT COUNT(o) FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person",
                        Long.class);
        tQuery = tQuery.setParameter("pKey", pKey + "%");
        tQuery = tQuery.setParameter("person", person);
        return tQuery.getSingleResult();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(
            String publisher) {
        this.publisher = publisher;
    }

    public Date getMarkDate() {
        return markDate;
    }

    public void setMarkDate(
            Date markDate) {
        this.markDate = markDate;
    }

}
