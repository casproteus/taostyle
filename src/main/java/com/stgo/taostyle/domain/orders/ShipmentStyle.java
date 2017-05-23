package com.stgo.taostyle.domain.orders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
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
public class ShipmentStyle {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    private int quantity;

    @NotNull
    private String shipment;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date outdoorDate;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date onboardDate;

    private String remark;

    @Transient
    private String mainOrderStr;

    @Transient
    private String outdoorDateStr = "abc";

    @Transient
    private String onboardDateStr;

    public static List<ShipmentStyle> findAllShipmentStylesByMainOrder(
            Long mainOrderId) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM ShipmentStyle o", ShipmentStyle.class).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<ShipmentStyle> tQuery =
                    tEntityManager.createQuery("SELECT o FROM ShipmentStyle AS o WHERE o.mainOrder = :pKey",
                            ShipmentStyle.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            List<ShipmentStyle> shipmentStyles = new ArrayList<ShipmentStyle>();
            try {
                shipmentStyles = tQuery.getResultList();
            } catch (Exception e) {
            }
            return shipmentStyles;
        }
    }

    public static List<ShipmentStyle> findShipmentStyleEntriesByMainOrder(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM ShipmentStyle o", ShipmentStyle.class)
                    .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<ShipmentStyle> tQuery =
                    tEntityManager.createQuery("SELECT o FROM ShipmentStyle AS o WHERE o.mainOrder = :pKey",
                            ShipmentStyle.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<ShipmentStyle> shipmentStyles = new ArrayList<ShipmentStyle>();
            try {
                shipmentStyles = tQuery.getResultList();
            } catch (Exception e) {
            }
            return shipmentStyles;
        }
    }

    public String getMainOrderStr() {
        return mainOrderStr;
    }

    public void setMainOrderStr(
            String mainOrderStr) {
        this.mainOrderStr = mainOrderStr;
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;
}
