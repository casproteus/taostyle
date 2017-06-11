package com.stgo.taostyle.domain.orders;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class MainOrder {

    @ManyToOne
    private UserAccount client;

    private String clientSideOrderNumber;

    private String clientSideModelNumber;

    private String model;

    @ManyToOne
    private MediaUpload picture;

    private String productName;

    @ManyToOne
    private UserAccount contactPerson;

    private int quantity;

    private String payCondition;

    private Boolean colorProportionReceived;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date delieverdate;

    private String sizeTable;

    private String colorCard;

    private String testRequirement;

    private String sampleRequirement;

    private String Remark;

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

    public static List<MainOrder> findMainOrderByCON(
            String pClientSideOrderNumber) {
        TypedQuery<MainOrder> tQuery =
                entityManager().createQuery(
                        "SELECT o FROM MainOrder AS o WHERE UPPER(o.clientSideOrderNumber) = UPPER(:tCON)",
                        MainOrder.class);
        tQuery = tQuery.setParameter("tCON", pClientSideOrderNumber);
        List<MainOrder> tList = tQuery.getResultList();
        return tList;
    }

    public static List<MainOrder> finMainOrdersBySizeTableAndPerson(
            String SizeTable,
            Person person) {
        TypedQuery<MainOrder> tQuery =
                entityManager()
                        .createQuery(
                                "SELECT o FROM MainOrder AS o WHERE o.sizeTable = :sizeTable and o.person = :person and o.recordStatus = 0 order by o.id desc",
                                MainOrder.class);
        tQuery = tQuery.setParameter("sizeTable", SizeTable);
        tQuery = tQuery.setParameter("person", person);
        return tQuery.getResultList();
    }

    public String toString() {
        // StringBuilder tStrBuilder = new
        // StringBuilder(client.toString()).append(" | ").append(model).append(" | ").append(quantity);
        // return tStrBuilder.toString();//ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return clientSideOrderNumber;
    }

    @Transactional
    public static List<MainOrder> findMainOrdersByClientAndPerson(
            UserAccount client,
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.client = :client ORDER BY o.id "
                                + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);
        tQuery = tQuery.setParameter("client", client);

        // we display all uncompleated order, if want to get today's order, should open a new method.
        // and o.delieverdate > :delieverdate
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 1);
        // tQuery = tQuery.setParameter("delieverdate", calendar.getTime());

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findUnCompletedMainOrdersByPerson(
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager
                        .createQuery(
                                "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.recordStatus > -1 and o.recordStatus < 10000 ORDER BY o.id "
                                        + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);

        // we display all uncompleated order, if want to get today's order, should open a new method.
        // and o.delieverdate > :delieverdate
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 1);
        // tQuery = tQuery.setParameter("delieverdate", calendar.getTime());

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findMainOrdersByStatusAndPerson(
            int recordStatus,
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.recordStatus = :recordStatus ORDER BY o.id "
                                + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);
        tQuery = tQuery.setParameter("recordStatus", recordStatus);

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findCompletedMainOrdersByPerson(
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.recordStatus < 0 ORDER BY o.id "
                                + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);

        // we display all uncompleated order, if want to get today's order, should open a new method.
        // and o.delieverdate > :delieverdate
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 1);
        // tQuery = tQuery.setParameter("delieverdate", calendar.getTime());

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findMainOrdersByPerson(
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery("SELECT o FROM MainOrder AS o WHERE o.person = :person ORDER BY o.id "
                        + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

}
