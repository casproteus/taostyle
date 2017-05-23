package com.stgo.taostyle.domain;

import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.stgo.taostyle.web.TaoEmail;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class UserAccount {

    private String loginname;

    private String password;

    private String cel;

    private String tel;

    private String fax;

    private String email;

    private String companyname;

    private String address;

    private String city;

    private String postcode;

    private int credit;

    private int balance;

    private String description;

    private String securitylevel;

    @ManyToOne
    private Person person;

    private int recordStatus;

    /**
     * to make it stronger, we also tack the possibility that the pUserName maybe actually email. note: the username is
     * unique, because there's *{personId} in the end.
     * 
     * @param userName
     * @return
     */
    public static com.stgo.taostyle.domain.UserAccount findUserAccountByName(
            String userName) {
        TypedQuery<UserAccount> tQuery =
                entityManager()
                        .createQuery("SELECT o FROM UserAccount AS o WHERE UPPER(o.loginname) = UPPER(:username)",
                                UserAccount.class);
        tQuery = tQuery.setParameter("username", userName);
        List<UserAccount> tList = tQuery.getResultList();
        if (tList != null && tList.size() == 1)
            return tList.get(0);
        else if (TaoEmail.isValidEmail(userName)) {
            // if it's null or have more than 1 match.
            tQuery =
                    entityManager().createQuery("SELECT o FROM UserAccount AS o WHERE UPPER(o.email) = UPPER(:tname)",
                            UserAccount.class);
            tQuery = tQuery.setParameter("tname", userName);
            tList = tQuery.getResultList();
            if (tList != null && tList.size() >= 1)
                return tList.get(0);
        }
        return null;
    }

    public static com.stgo.taostyle.domain.UserAccount findUserAccountByNameAndPassword(
            String userName,
            String password) {
        TypedQuery<UserAccount> tQuery =
                entityManager()
                        .createQuery(
                                "SELECT o FROM UserAccount AS o WHERE UPPER(o.loginname) = UPPER(:username) and o.password = :password",
                                UserAccount.class);
        tQuery = tQuery.setParameter("username", userName);
        tQuery = tQuery.setParameter("password", password);
        List<UserAccount> tList = tQuery.getResultList();
        if (tList != null && tList.size() == 1)
            return tList.get(0);
        else if (TaoEmail.isValidEmail(userName)) {
            // if it's null or have more than 1 match.
            tQuery =
                    entityManager()
                            .createQuery(
                                    "SELECT o FROM UserAccount AS o WHERE UPPER(o.email) = UPPER(:email) and o.password = :password",
                                    UserAccount.class);
            tQuery = tQuery.setParameter("email", userName);
            tQuery = tQuery.setParameter("password", password);
            tList = tQuery.getResultList();
            if (tList != null && tList.size() >= 1)
                return tList.get(0);
        }
        return null;
    }

    // override this method, so when the useraccount displayed in dropdown box, only the login name will be displayed
    // instead of all fields.
    @Override
    public String toString() {
        return this.getLoginname(); // return ReflectionToStringBuilder.toString(this,
                                    // ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static List<UserAccount> findAllUserAccountsByPerson(
            Person person) {
        TypedQuery<UserAccount> tQuery =
                entityManager().createQuery("SELECT o FROM UserAccount o where o.person = :person ORDER BY o.id DESC",
                        UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.getResultList();
    }

    public static List<UserAccount> findUserAccountEntriesByPerson(
            int firstResult,
            int maxResults,
            Person person) {
        TypedQuery<UserAccount> tQuery =
                entityManager().createQuery("SELECT o FROM UserAccount o where o.person = :person ORDER BY o.id DESC",
                        UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public static long countUserAccountsByPerson(
            Person person) {
        return findAllUserAccountsByPerson(person).size();
    }

    @Transactional
    public static List<UserAccount> findUserAccountEntriesByPerson(
            int firstResult,
            int maxResults,
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM UserAccount o where o.person = :person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }

        TypedQuery<UserAccount> tQuery = entityManager().createQuery(jpaQuery, UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public static List<UserAccount> findAllUserAccountsByPerson(
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM UserAccount o where o.person =:person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserAccount> tQuery = entityManager().createQuery(jpaQuery, UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.getResultList();
    }
}
