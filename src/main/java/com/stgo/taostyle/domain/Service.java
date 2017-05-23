package com.stgo.taostyle.domain;

import java.util.ArrayList;
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

    private String c3;

    @ManyToOne
    private Person person;

    private int recordStatus;

    @Transient
    private String localName;

    @Transient
    private String localDescription;

    public static List<com.stgo.taostyle.domain.Service> findServiceByCatalogAndPerson(
            String pCatalog,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Service> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Service AS o WHERE o.c1 = :pKey and o.person = :person ORDER BY o.id DESC",
                        Service.class);
        tQuery = tQuery.setParameter("pKey", pCatalog);
        tQuery = tQuery.setParameter("person", person);
        List<Service> tServices;
        try {
            tServices = tQuery.getResultList();
        } catch (Exception e) {
            tServices = new ArrayList<Service>();
        }
        return tServices;
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
