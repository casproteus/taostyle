package com.stgo.taostyle.domain.orders;

import java.util.ArrayList;
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
import org.springframework.roo.addon.tostring.RooToString;

import com.stgo.taostyle.domain.Person;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class TaxonomyMaterial {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    private String itemName;

    private String quality;

    private String specification;

    private String color;

    private int quantity;

    private String location;

    private String remark;

    public static List<TaxonomyMaterial> findTaxonomyMaterialByLocation(
            String location) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<TaxonomyMaterial> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TaxonomyMaterial AS o WHERE o.location = :location order by o.id desc",
                        TaxonomyMaterial.class);
        tQuery = tQuery.setParameter("location", location);
        List<TaxonomyMaterial> taxonomyMaterials = null;
        try {
            taxonomyMaterials = tQuery.getResultList();
        } catch (Exception e) {
        }
        return taxonomyMaterials;
    }

    public static List<TaxonomyMaterial> findAllTaxonomyMaterialsByMainOrder(
            Long mainOrderId) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM TaxonomyMaterial o", TaxonomyMaterial.class)
                    .getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<TaxonomyMaterial> tQuery =
                    tEntityManager.createQuery("SELECT o FROM TaxonomyMaterial AS o WHERE o.mainOrder = :pKey",
                            TaxonomyMaterial.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            List<TaxonomyMaterial> taxonomyMaterials = new ArrayList<TaxonomyMaterial>();
            try {
                taxonomyMaterials = tQuery.getResultList();
            } catch (Exception e) {
            }
            return taxonomyMaterials;
        }
    }

    public static List<TaxonomyMaterial> findTaxonomyMaterialEntriesByMainOrder(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM TaxonomyMaterial o", TaxonomyMaterial.class)
                    .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<TaxonomyMaterial> tQuery =
                    tEntityManager.createQuery("SELECT o FROM TaxonomyMaterial AS o WHERE o.mainOrder = :pKey",
                            TaxonomyMaterial.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<TaxonomyMaterial> taxonomyMaterials = new ArrayList<TaxonomyMaterial>();
            try {
                taxonomyMaterials = tQuery.getResultList();
            } catch (Exception e) {
            }
            return taxonomyMaterials;
        }
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date logtime;
}
