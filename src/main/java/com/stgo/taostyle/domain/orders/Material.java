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
import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;

@Entity
@Configurable
public class Material {

    @NotNull
    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    private String portionName;

    private String dencity;

    private String MenFu;

    private String color;

    private String location;

    private String remark;

    public static List<Material> findAllMaterialsByMainOrderId(
            Long mainOrderId) {
        MainOrder mainOrder = MainOrder.findMainOrder(mainOrderId);
        return findAllMaterialsByMainOrder(mainOrder);
    }

    // @!!when adding printer to material, make sure it starts and ends with ",", no space.
    public static List<Material> findAllMaterialsByMainOrderIdAndPrinter(
            MainOrder mainOrder,
            UserAccount printer) {
        EntityManager tEntityManager = entityManager();
        List<Material> materials = new ArrayList<Material>();
        if (mainOrder != null) {
            TypedQuery<Material> tQuery = tEntityManager.createQuery(
                    "SELECT o FROM Material AS o WHERE o.mainOrder = :mainOrder and o.MenFu like :printer",
                    Material.class);
            tQuery = tQuery.setParameter("mainOrder", mainOrder);
            tQuery = tQuery.setParameter("printer",
                    printer != null ? "%," + TaoEncrypt.stripUserName(printer.getLoginname()) + ",%" : "");
            try {
                materials = tQuery.getResultList();
            } catch (Exception e) {
            }
        }
        return materials;
    }

    public static List<Material> findMaterialEntriesByMainOrderId(
            Long mainOrderId,
            int firstResult,
            int maxResults) {
        MainOrder tMainOrder = MainOrder.findMainOrder(mainOrderId);
        if (tMainOrder == null) {
            return entityManager().createQuery("SELECT o FROM Material o", Material.class).setFirstResult(firstResult)
                    .setMaxResults(maxResults).getResultList();
        } else {
            EntityManager tEntityManager = entityManager();
            TypedQuery<Material> tQuery =
                    tEntityManager.createQuery("SELECT o FROM Material AS o WHERE o.mainOrder = :pKey", Material.class);
            tQuery = tQuery.setParameter("pKey", tMainOrder);
            if (firstResult > -1 && maxResults > 0)
                tQuery = tQuery.setFirstResult(firstResult).setMaxResults(maxResults);
            // to make sure not return a null;
            List<Material> materials = new ArrayList<Material>();
            try {
                materials = tQuery.getResultList();
            } catch (Exception e) {
            }
            return materials;
        }
    }

    public static List<Material> findAllMaterialsByMainOrder(
            MainOrder mainOrder) {
        EntityManager tEntityManager = entityManager();
        List<Material> materials = new ArrayList<Material>();
        if (mainOrder != null) {
            TypedQuery<Material> tQuery = tEntityManager.createQuery(
                    "SELECT o FROM Material AS o WHERE o.mainOrder = :pKey ORDER BY o.location", Material.class);
            tQuery = tQuery.setParameter("pKey", mainOrder);
            try {
                materials = tQuery.getResultList();
            } catch (Exception e) {
            }
        }
        return materials;
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("mainOrder", "portionName", "dencity", "MenFu", "color", "location", "remark", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new Material().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countMaterials() {
        return findAllMaterials().size();
    }

	@Transactional
    public static List<Material> findAllMaterials() {
        return entityManager().createQuery("SELECT o FROM Material o", Material.class).getResultList();
    }

	@Transactional
    public static List<Material> findAllMaterials(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Material o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Material.class).getResultList();
    }

	@Transactional
    public static Material findMaterial(Long id) {
        if (id == null) return null;
        return entityManager().find(Material.class, id);
    }

	@Transactional
    public static List<Material> findMaterialEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Material o", Material.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<Material> findMaterialEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Material o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Material.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Material attached = Material.findMaterial(this.id);
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
    public Material merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Material merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

	public String getPortionName() {
        return this.portionName;
    }

	public void setPortionName(String portionName) {
        this.portionName = portionName;
    }

	public String getDencity() {
        return this.dencity;
    }

	public void setDencity(String dencity) {
        this.dencity = dencity;
    }

	public String getMenFu() {
        return this.MenFu;
    }

	public void setMenFu(String MenFu) {
        this.MenFu = MenFu;
    }

	public String getColor() {
        return this.color;
    }

	public void setColor(String color) {
        this.color = color;
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
}
