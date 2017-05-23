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
public class PackageStyle {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    private String itemName;

    private String qualitySpecification;

    private String sizeSpecification;

    private String color;

    private int quantity;

    private String location;

    private String remark;

    public static List<PackageStyle> findAllPackageStylesByMainOrder(
            Long mainOrderId) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<PackageStyle> tQuery =
                    tEntityManager.createQuery("SELECT o FROM PackageStyle AS o WHERE o.mainOrder = :pKey",
                            PackageStyle.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            List<PackageStyle> packageStyles = new ArrayList<PackageStyle>();
            try {
                packageStyles = tQuery.getResultList();
            } catch (Exception e) {
            }
            return packageStyles;
        }
    }

    public static List<PackageStyle> findPackageStyleEntriesByMainOrder(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class)
                    .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<PackageStyle> tQuery =
                    tEntityManager.createQuery("SELECT o FROM PackageStyle AS o WHERE o.mainOrder = :pKey",
                            PackageStyle.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<PackageStyle> packageStyles = new ArrayList<PackageStyle>();
            try {
                packageStyles = tQuery.getResultList();
            } catch (Exception e) {
            }
            return packageStyles;
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
