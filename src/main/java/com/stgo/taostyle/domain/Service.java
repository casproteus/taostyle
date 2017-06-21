package com.stgo.taostyle.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Service implements MultiLanguageabl {

    private String name;

    private String description;

    private String c1;

    private String c2;

    private String c3;// c3 is now used to put printers string.

    @ManyToOne
    private Person person;

    private int recordStatus;

    @Transient
    private String localName;

    @Transient
    private String localDescription;

    public static Service findServiceByCatalogAndPerson(
            String catalog1,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Service> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Service AS o WHERE o.c1 = :catalog1 and o.person = :person ORDER BY o.id DESC",
                        Service.class);
        tQuery = tQuery.setParameter("catalog1", catalog1);
        tQuery = tQuery.setParameter("person", person);
        Service service = null;
        try {
            service = tQuery.getSingleResult();
        } catch (Exception e) {
            // do nothing.
        }
        return service;
    }

    public static List<Service> findServiceByPerson(
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Service> tQuery =
                tEntityManager.createQuery("SELECT o FROM Service AS o WHERE o.person = :person ORDER BY o.id DESC",
                        Service.class);
        tQuery = tQuery.setParameter("person", person);
        Service service = null;
        List<Service> services = null;
        try {
            services = tQuery.getResultList();
        } catch (Exception e) {
            // do nothing.
        }
        return services;
    }

    public String getLocalName() {
        return localName;
    }

    @Override
    public void setLocalName(
            String localName) {
        this.localName = localName;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    @Override
    public void setLocalDescription(
            String localDescription) {
        this.localDescription = localDescription;
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
