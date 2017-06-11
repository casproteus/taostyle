package com.stgo.taostyle.domain;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.stgo.taostyle.web.TaoDebug;

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
        if (!StringUtils.isEmpty(password)) {
            this.password = password;
        } else {
            TaoDebug.error("setting a empty string into person's password.");
            Thread.dumpStack();
        }
    }

    public String toString() {
        return name;// ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
