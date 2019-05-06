package com.stgo.taostyle.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class City {

    @NotNull
    private String name;

    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int status;

    /**
     */
    private int recordStatus;

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static City fromJsonToCity(String json) {
        return new JSONDeserializer<City>()
        .use(null, City.class).deserialize(json);
    }

	public static String toJsonArray(Collection<City> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<City> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<City> fromJsonArrayToCitys(String json) {
        return new JSONDeserializer<List<City>>()
        .use("values", City.class).deserialize(json);
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public boolean isIsActive() {
        return this.isActive;
    }

	public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

	public Country getCountry() {
        return this.country;
    }

	public void setCountry(Country country) {
        this.country = country;
    }

	public Person getPerson() {
        return this.person;
    }

	public void setPerson(Person person) {
        this.person = person;
    }

	public int getStatus() {
        return this.status;
    }

	public void setStatus(int status) {
        this.status = status;
    }

	public int getRecordStatus() {
        return this.recordStatus;
    }

	public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
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

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("name", "isActive", "country", "person", "status", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new City().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countCitys() {
        return findAllCitys().size();
    }

	@Transactional
    public static List<City> findAllCitys() {
        return entityManager().createQuery("SELECT o FROM City o", City.class).getResultList();
    }

	@Transactional
    public static List<City> findAllCitys(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM City o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, City.class).getResultList();
    }

	@Transactional
    public static City findCity(Long id) {
        if (id == null) return null;
        return entityManager().find(City.class, id);
    }

	@Transactional
    public static List<City> findCityEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM City o", City.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<City> findCityEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM City o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, City.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            City attached = City.findCity(this.id);
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
    public City merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        City merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
