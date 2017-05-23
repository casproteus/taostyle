package com.stgo.taostyle.domain;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Person {

    @NotNull
    @Column(unique = true)
    private String name;

    private String password;
    /**
     */
    private int recordStatus;

    public static Person findPersonByName(
            String person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Person> tQuery =
                tEntityManager.createQuery("SELECT o FROM Person AS o WHERE o.name = :pName", Person.class);
        tQuery = tQuery.setParameter("pName", person);
        try {
            return tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password) {
        this.password = password;
    }

    public String toString() {
        return name;// ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
