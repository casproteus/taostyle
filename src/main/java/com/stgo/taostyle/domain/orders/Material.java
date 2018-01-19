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

import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;

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

    // @!!when adding printer to material, make sure it starts and ends with ",", no space.
    public static List<Material> findAllMaterialsByMainOrderIdAndPrinter(
            MainOrder mainOrder,
            UserAccount printer) {
        EntityManager tEntityManager = entityManager();
        List<Material> materials = new ArrayList<Material>();
        if (mainOrder != null) {
            TypedQuery<Material> tQuery = tEntityManager.createQuery(
                    "SELECT o FROM Material AS o WHERE o.mainOrder = :mainOrder and o.MenFu like :printer",
                    Material.class);
            tQuery = tQuery.setParameter("mainOrder", mainOrder);
            tQuery = tQuery.setParameter("printer",
                    printer != null ? "%," + TaoEncrypt.stripUserName(printer.getLoginname()) + ",%" : "");
            try {
                materials = tQuery.getResultList();
            } catch (Exception e) {
            }
        }
        return materials;
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
        List<Material> materials = new ArrayList<Material>();
        if (mainOrder != null) {
            TypedQuery<Material> tQuery = tEntityManager.createQuery(
                    "SELECT o FROM Material AS o WHERE o.mainOrder = :pKey ORDER BY o.location", Material.class);
            tQuery = tQuery.setParameter("pKey", mainOrder);
            try {
                materials = tQuery.getResultList();
            } catch (Exception e) {
            }
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
