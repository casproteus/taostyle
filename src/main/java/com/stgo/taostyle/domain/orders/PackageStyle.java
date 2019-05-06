package com.stgo.taostyle.domain.orders;

import java.util.ArrayList;
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
import com.stgo.taostyle.domain.Person;

@Entity
@Configurable
public class PackageStyle {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    private String itemName;

    private String qualitySpecification;

    private String sizeSpecification;

    private String color;

    private int quantity;

    private String location;

    private String remark;

    public static List<PackageStyle> findAllPackageStylesByMainOrder(
            Long mainOrderId) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<PackageStyle> tQuery =
                    tEntityManager.createQuery("SELECT o FROM PackageStyle AS o WHERE o.mainOrder = :pKey",
                            PackageStyle.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            List<PackageStyle> packageStyles = new ArrayList<PackageStyle>();
            try {
                packageStyles = tQuery.getResultList();
            } catch (Exception e) {
            }
            return packageStyles;
        }
    }

    public static List<PackageStyle> findPackageStyleEntriesByMainOrder(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class)
                    .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<PackageStyle> tQuery =
                    tEntityManager.createQuery("SELECT o FROM PackageStyle AS o WHERE o.mainOrder = :pKey",
                            PackageStyle.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<PackageStyle> packageStyles = new ArrayList<PackageStyle>();
            try {
                packageStyles = tQuery.getResultList();
            } catch (Exception e) {
            }
            return packageStyles;
        }
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

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

	public String getQualitySpecification() {
        return this.qualitySpecification;
    }

	public void setQualitySpecification(String qualitySpecification) {
        this.qualitySpecification = qualitySpecification;
    }

	public String getSizeSpecification() {
        return this.sizeSpecification;
    }

	public void setSizeSpecification(String sizeSpecification) {
        this.sizeSpecification = sizeSpecification;
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

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("mainOrder", "itemName", "qualitySpecification", "sizeSpecification", "color", "quantity", "location", "remark", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new PackageStyle().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countPackageStyles() {
        return findAllPackageStyles().size();
    }

	@Transactional
    public static List<PackageStyle> findAllPackageStyles() {
        return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class).getResultList();
    }

	@Transactional
    public static List<PackageStyle> findAllPackageStyles(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM PackageStyle o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, PackageStyle.class).getResultList();
    }

	@Transactional
    public static PackageStyle findPackageStyle(Long id) {
        if (id == null) return null;
        return entityManager().find(PackageStyle.class, id);
    }

	@Transactional
    public static List<PackageStyle> findPackageStyleEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<PackageStyle> findPackageStyleEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM PackageStyle o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, PackageStyle.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            PackageStyle attached = PackageStyle.findPackageStyle(this.id);
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
    public PackageStyle merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PackageStyle merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
