package com.stgo.taostyle.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    /**
     */
    @NotNull
    private String menuIdx;

    /**
     */
    @NotNull
    private String refMenuIdx;

    /**
     */
    private String itemsToShow;

    /**
     */
    @NotNull
    @ManyToOne
    private Person person;

    public static List<Feature> findAllFeaturesByPerson(
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Feature> tQuery =
                tEntityManager.createQuery("SELECT o FROM Feature AS o WHERE o.person = :person", Feature.class);
        tQuery.setParameter("person", person);
        try {
            return tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Feature> findAllFeaturesByMenuIdxAndPerson(
            String menuIdx,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Feature> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Feature AS o WHERE o.menuIdx = :menuIdx and o.person = :person ORDER BY o.id",
                        Feature.class);
        tQuery.setParameter("menuIdx", menuIdx);
        tQuery.setParameter("person", person);
        try {
            return tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
