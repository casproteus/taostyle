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
public class Cart {

    @ManyToOne
    private Product product;

    private int quantity;

    private int weight;

    private String unit;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date createdDate;

    private String status;

    @NotNull
    @ManyToOne
    private UserAccount owner;

    @ManyToOne
    private Person person;

    private int recordStatus;

    public static List<com.stgo.taostyle.domain.Cart> findCartsByUser(
            UserAccount userAccount) {
        if (userAccount == null)
            return null;
        EntityManager tEntityManager = entityManager();
        TypedQuery<Cart> tQuery =
                tEntityManager.createQuery("SELECT o FROM Cart AS o WHERE o.owner = :tUserAccount", Cart.class);
        tQuery = tQuery.setParameter("tUserAccount", userAccount);
        return tQuery.getResultList();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(
            Person person) {
        this.person = person;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(
            int recordStatus) {
        this.recordStatus = recordStatus;
    }

}
