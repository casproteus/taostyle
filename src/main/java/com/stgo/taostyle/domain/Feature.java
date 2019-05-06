package com.stgo.taostyle.domain;

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
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    /**
     */
    @NotNull
    private String menuIdx;

    /**
     */
    @NotNull
    private String refMenuIdx;

    /**
     */
    private String itemsToShow;

    /**
     */
    @NotNull
    @ManyToOne
    private Person person;

    public static List<Feature> findAllFeaturesByPerson(
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Feature> tQuery =
                tEntityManager.createQuery("SELECT o FROM Feature AS o WHERE o.person = :person", Feature.class);
        tQuery.setParameter("person", person);
        try {
            return tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Feature> findAllFeaturesByMenuIdxAndPerson(
            String menuIdx,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Feature> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Feature AS o WHERE o.menuIdx = :menuIdx and o.person = :person ORDER BY o.id",
                        Feature.class);
        tQuery.setParameter("menuIdx", menuIdx);
        tQuery.setParameter("person", person);
        try {
            return tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
    }


	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	@Version
    @Column(name = "version")
    private Integer version;

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public String getMenuIdx() {
        return this.menuIdx;
    }

	public void setMenuIdx(String menuIdx) {
        this.menuIdx = menuIdx;
    }

	public String getRefMenuIdx() {
        return this.refMenuIdx;
    }

	public void setRefMenuIdx(String refMenuIdx) {
        this.refMenuIdx = refMenuIdx;
    }

	public String getItemsToShow() {
        return this.itemsToShow;
    }

	public void setItemsToShow(String itemsToShow) {
        this.itemsToShow = itemsToShow;
    }

	public Person getPerson() {
        return this.person;
    }

	public void setPerson(Person person) {
        this.person = person;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "menuIdx", "groupTitle", "refMenuIdx", "itemsToShow", "person");

	public static final EntityManager entityManager() {
        EntityManager em = new Feature().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countFeatures() {
        return findAllFeatures().size();
    }

	@Transactional
    public static List<Feature> findAllFeatures() {
        return entityManager().createQuery("SELECT o FROM Feature o", Feature.class).getResultList();
    }

	@Transactional
    public static List<Feature> findAllFeatures(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Feature o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Feature.class).getResultList();
    }

	@Transactional
    public static Feature findFeature(Long id) {
        if (id == null) return null;
        return entityManager().find(Feature.class, id);
    }

	@Transactional
    public static List<Feature> findFeatureEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Feature o", Feature.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<Feature> findFeatureEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Feature o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Feature.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Feature attached = Feature.findFeature(this.id);
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
    public Feature merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Feature merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
