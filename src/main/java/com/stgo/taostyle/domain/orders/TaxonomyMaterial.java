package com.stgo.taostyle.domain.orders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.stgo.taostyle.domain.Person;

@Entity
@Configurable
public class TaxonomyMaterial {

    // @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    private String itemName;

    private String quality;

    private String specification;

    private String color;

    private int quantity;

    private String location;

    private String remark;

    public static List<TaxonomyMaterial> findTaxonomyMaterialByLocation(
            String location) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<TaxonomyMaterial> tQuery = tEntityManager.createQuery(
                "SELECT o FROM TaxonomyMaterial AS o WHERE o.location = :location order by o.id desc",
                TaxonomyMaterial.class);
        tQuery = tQuery.setParameter("location", location);
        List<TaxonomyMaterial> taxonomyMaterials = null;
        try {
            taxonomyMaterials = tQuery.getResultList();
        } catch (Exception e) {
        }
        return taxonomyMaterials;
    }

    public static List<TaxonomyMaterial> findAllTaxonomyMaterialsByMainOrder(
            MainOrder mainOrder) {
        if (mainOrder == null) {
            return entityManager().createQuery("SELECT o FROM TaxonomyMaterial o", TaxonomyMaterial.class)
                    .getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<TaxonomyMaterial> query = tEntityManager.createQuery(
                    "SELECT o FROM TaxonomyMaterial AS o WHERE o.mainOrder = :pKey", TaxonomyMaterial.class);
            query = query.setParameter("pKey", mainOrder);
            List<TaxonomyMaterial> taxonomyMaterials = new ArrayList<TaxonomyMaterial>();
            try {
                taxonomyMaterials = query.getResultList();
            } catch (Exception e) {
            }
            return taxonomyMaterials;
        }
    }

    public static List<TaxonomyMaterial> findAllTaxonomyMaterialsByMainOrderID(
            Long mainOrderId) {
        MainOrder mainOrder = MainOrder.findMainOrder(mainOrderId);
        return findAllTaxonomyMaterialsByMainOrder(mainOrder);
    }

    public static List<TaxonomyMaterial> findTaxonomyMaterialEntriesByMainOrder(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM TaxonomyMaterial o", TaxonomyMaterial.class)
                    .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<TaxonomyMaterial> tQuery = tEntityManager.createQuery(
                    "SELECT o FROM TaxonomyMaterial AS o WHERE o.mainOrder = :pKey", TaxonomyMaterial.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<TaxonomyMaterial> taxonomyMaterials = new ArrayList<TaxonomyMaterial>();
            try {
                taxonomyMaterials = tQuery.getResultList();
            } catch (Exception e) {
            }
            return taxonomyMaterials;
        }
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date logtime;

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("mainOrder", "itemName", "quality", "specification", "color", "quantity", "location", "remark", "person", "recordStatus", "logtime");

	public static final EntityManager entityManager() {
        EntityManager em = new TaxonomyMaterial().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countTaxonomyMaterials() {
        return findAllTaxonomyMaterials().size();
    }

	@Transactional
    public static List<TaxonomyMaterial> findAllTaxonomyMaterials() {
        return entityManager().createQuery("SELECT o FROM TaxonomyMaterial o", TaxonomyMaterial.class).getResultList();
    }

	@Transactional
    public static List<TaxonomyMaterial> findAllTaxonomyMaterials(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TaxonomyMaterial o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TaxonomyMaterial.class).getResultList();
    }

	@Transactional
    public static TaxonomyMaterial findTaxonomyMaterial(Long id) {
        if (id == null) return null;
        return entityManager().find(TaxonomyMaterial.class, id);
    }

	@Transactional
    public static List<TaxonomyMaterial> findTaxonomyMaterialEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TaxonomyMaterial o", TaxonomyMaterial.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<TaxonomyMaterial> findTaxonomyMaterialEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TaxonomyMaterial o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TaxonomyMaterial.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            TaxonomyMaterial attached = TaxonomyMaterial.findTaxonomyMaterial(this.id);
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
    public TaxonomyMaterial merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TaxonomyMaterial merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public MainOrder getMainOrder() {
        return this.mainOrder;
    }

	public void setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
    }

	public String getItemName() {
        return this.itemName;
    }

	public void setItemName(String itemName) {
        this.itemName = itemName;
    }

	public String getQuality() {
        return this.quality;
    }

	public void setQuality(String quality) {
        this.quality = quality;
    }

	public String getSpecification() {
        return this.specification;
    }

	public void setSpecification(String specification) {
        this.specification = specification;
    }

	public String getColor() {
        return this.color;
    }

	public void setColor(String color) {
        this.color = color;
    }

	public int getQuantity() {
        return this.quantity;
    }

	public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

	public String getLocation() {
        return this.location;
    }

	public void setLocation(String location) {
        this.location = location;
    }

	public String getRemark() {
        return this.remark;
    }

	public void setRemark(String remark) {
        this.remark = remark;
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

	public Date getLogtime() {
        return this.logtime;
    }

	public void setLogtime(Date logtime) {
        this.logtime = logtime;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
