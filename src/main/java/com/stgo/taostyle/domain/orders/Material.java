package com.stgo.taostyle.domain.orders;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import com.stgo.taostyle.domain.Person;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Material {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    private String portionName;

    private String dencity;

    private String MenFu;

    private String color;

    private String location;

    private String remark;

    public static List<Material> findAllMaterialsByMainOrderId(
            Long mainOrderId) {
        MainOrder mainOrder = MainOrder.findMainOrder(mainOrderId);
        return findAllMaterialsByMainOrder(mainOrder);
    }

    public static List<Material> findMaterialEntriesByMainOrderId(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM Material o", Material.class).setFirstResult(firstResult)
                    .setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<Material> tQuery =
                    tEntityManager.createQuery("SELECT o FROM Material AS o WHERE o.mainOrder = :pKey", Material.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<Material> materials = new ArrayList<Material>();
            try {
                materials = tQuery.getResultList();
            } catch (Exception e) {
            }
            return materials;
        }
    }

    public static List<Material> findAllMaterialsByMainOrder(
            MainOrder mainOrder) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Material> tQuery =
                tEntityManager.createQuery("SELECT o FROM Material AS o WHERE o.mainOrder = :pKey", Material.class);
        tQuery = tQuery.setParameter("pKey", mainOrder);
        List<Material> materials = new ArrayList<Material>();
        try {
            materials = tQuery.getResultList();
        } catch (Exception e) {
        }
        return materials;
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;
}
