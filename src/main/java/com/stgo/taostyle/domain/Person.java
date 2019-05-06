package com.stgo.taostyle.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.stgo.taostyle.web.TaoDebug;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.Collection;
import java.util.List;

@Entity
@Configurable
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

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static Person fromJsonToPerson(String json) {
        return new JSONDeserializer<Person>()
        .use(null, Person.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Person> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<Person> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<Person> fromJsonArrayToPeople(String json) {
        return new JSONDeserializer<List<Person>>()
        .use("values", Person.class).deserialize(json);
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public int getRecordStatus() {
        return this.recordStatus;
    }

	public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("name", "password", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new Person().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countPeople() {
        return findAllPeople().size();
    }

	@Transactional
    public static List<Person> findAllPeople() {
        return entityManager().createQuery("SELECT o FROM Person o", Person.class).getResultList();
    }

	@Transactional
    public static List<Person> findAllPeople(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Person o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Person.class).getResultList();
    }

	@Transactional
    public static Person findPerson(Long id) {
        if (id == null) return null;
        return entityManager().find(Person.class, id);
    }

	@Transactional
    public static List<Person> findPersonEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Person o", Person.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<Person> findPersonEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Person o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Person.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Person attached = Person.findPerson(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public Person merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Person merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
