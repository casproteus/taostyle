package com.stgo.taostyle.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
public class Customize {

    @NotNull
    private String cusKey;

    @NotNull
    private String cusValue;

    @ManyToOne
    private Person person;

    private int recordStatus;

    public static com.stgo.taostyle.domain.Customize findCustomizeByKeyAndPerson(
            String cusKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Customize> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Customize AS o WHERE o.cusKey = :pCusKey and o.person = :person",
                        Customize.class);
        tQuery = tQuery.setParameter("pCusKey", cusKey);
        tQuery = tQuery.setParameter("person", person);
        try {
            return tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Customize> findAllCustomizesByPerson(
            Person person) {
        TypedQuery<Customize> query =
                entityManager().createQuery("SELECT o FROM Customize o where o.person = :person ORDER BY o.cusKey",
                        Customize.class);
        query.setParameter("person", person);
        return query.getResultList();
    }

    public static List<Customize> findCustomizeEntries(
            int firstResult,
            int maxResults) {
        return entityManager().createQuery("SELECT o FROM Customize o ORDER BY o.cusKey", Customize.class)
                .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public static List<Customize> findAllOrderedCustomizesByPerson(
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM Customize o where o.person = :person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Customize> query = entityManager().createQuery(jpaQuery, Customize.class);
        query.setParameter("person", person);
        return query.getResultList();
    }

    @Transactional
    public static long countCustomizesByPerson(
            Person person) {
        return findAllCustomizesByPerson(person).size();
    }

    @Transactional
    public static List<Customize> findOrderedCustomizeEntriesByPerson(
            int firstResult,
            int maxResults,
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM Customize o where o.person = :person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Customize> query = entityManager().createQuery(jpaQuery, Customize.class);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        query.setParameter("person", person);
        return query.getResultList();
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("cusKey", "cusValue", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new Customize().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static Customize findCustomize(Long id) {
        if (id == null) return null;
        return entityManager().find(Customize.class, id);
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
            Customize attached = Customize.findCustomize(this.id);
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
    public Customize merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Customize merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
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

	public String getCusKey() {
        return this.cusKey;
    }

	public void setCusKey(String cusKey) {
        this.cusKey = cusKey;
    }

	public String getCusValue() {
        return this.cusValue;
    }

	public void setCusValue(String cusValue) {
        this.cusValue = cusValue;
    }

	public Person getPerson() {
        return this.person;
    }

	public void setPerson(Person person) {
        this.person = person;
    }

	public int getRecordStatus() {
        return this.recordStatus;
    }

	public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static Customize fromJsonToCustomize(String json) {
        return new JSONDeserializer<Customize>()
        .use(null, Customize.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Customize> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<Customize> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<Customize> fromJsonArrayToCustomizes(String json) {
        return new JSONDeserializer<List<Customize>>()
        .use("values", Customize.class).deserialize(json);
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
