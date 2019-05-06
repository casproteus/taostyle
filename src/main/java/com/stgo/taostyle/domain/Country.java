package com.stgo.taostyle.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.ManyToOne;

@Entity
@Configurable
public class Country {

    @NotNull
    private String name;

    private boolean isActive;

    @Size(max = 500)
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "country")
    private Set<City> cities = new HashSet<City>();

    @Override
    public String toString() {
        return this.name;
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static Country fromJsonToCountry(String json) {
        return new JSONDeserializer<Country>()
        .use(null, Country.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Country> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<Country> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<Country> fromJsonArrayToCountrys(String json) {
        return new JSONDeserializer<List<Country>>()
        .use("values", Country.class).deserialize(json);
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

	public String getDescription() {
        return this.description;
    }

	public void setDescription(String description) {
        this.description = description;
    }

	public Set<City> getCities() {
        return this.cities;
    }

	public void setCities(Set<City> cities) {
        this.cities = cities;
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

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("name", "isActive", "description", "cities", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new Country().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countCountrys() {
        return findAllCountrys().size();
    }

	@Transactional
    public static List<Country> findAllCountrys() {
        return entityManager().createQuery("SELECT o FROM Country o", Country.class).getResultList();
    }

	@Transactional
    public static List<Country> findAllCountrys(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Country o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Country.class).getResultList();
    }

	@Transactional
    public static Country findCountry(Long id) {
        if (id == null) return null;
        return entityManager().find(Country.class, id);
    }

	@Transactional
    public static List<Country> findCountryEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Country o", Country.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<Country> findCountryEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Country o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Country.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Country attached = Country.findCountry(this.id);
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
    public Country merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Country merged = this.entityManager.merge(this);
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
}
