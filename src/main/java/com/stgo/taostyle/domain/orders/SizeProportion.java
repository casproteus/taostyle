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
public class SizeProportion {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    private String Color;

    private int smallsize;

    private int middlesize;

    private int largeSize;

    private int extraLargeSize;

    private int other1;

    private int other2;

    private int other3;

    private int other4;

    private int total;

    private String Remark;

    public static List<SizeProportion> findAllSizeProportionsByMainOrder(
            Long mainOrderId) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM SizeProportion o", SizeProportion.class).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<SizeProportion> tQuery =
                    tEntityManager.createQuery("SELECT o FROM SizeProportion AS o WHERE o.mainOrder = :pKey",
                            SizeProportion.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            List<SizeProportion> sizeProportions = new ArrayList<SizeProportion>();
            try {
                sizeProportions = tQuery.getResultList();
            } catch (Exception e) {
            }
            return sizeProportions;
        }
    }

    public static List<SizeProportion> findSizeProportionEntriesByMainOrder(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM SizeProportion o", SizeProportion.class)
                    .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<SizeProportion> tQuery =
                    tEntityManager.createQuery("SELECT o FROM SizeProportion AS o WHERE o.mainOrder = :pKey",
                            SizeProportion.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<SizeProportion> sizeProportions = new ArrayList<SizeProportion>();
            try {
                sizeProportions = tQuery.getResultList();
            } catch (Exception e) {
            }
            return sizeProportions;
        }
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;
}
