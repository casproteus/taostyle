package com.stgo.taostyle.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
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

@Configurable
@Entity
public class Cart {

    @ManyToOne
    private Product product;

    private int quantity;

    private int weight;

    private String unit;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date createdDate;

    private String status;

    @NotNull
    @ManyToOne
    private UserAccount owner;

    @ManyToOne
    private Person person;

    private int recordStatus;

    public static List<com.stgo.taostyle.domain.Cart> findCartsByUser(
            UserAccount userAccount) {
        if (userAccount == null)
            return null;
        EntityManager tEntityManager = entityManager();
        TypedQuery<Cart> tQuery =
                tEntityManager.createQuery("SELECT o FROM Cart AS o WHERE o.owner = :tUserAccount", Cart.class);
        tQuery = tQuery.setParameter("tUserAccount", userAccount);
        return tQuery.getResultList();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(
            Person person) {
        this.person = person;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(
            int recordStatus) {
        this.recordStatus = recordStatus;
    }


	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Cart fromJsonToCart(String json) {
        return new JSONDeserializer<Cart>().use(null, Cart.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Cart> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Cart> fromJsonArrayToCarts(String json) {
        return new JSONDeserializer<List<Cart>>().use(null, ArrayList.class).use("values", Cart.class).deserialize(json);
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

	public Product getProduct() {
        return this.product;
    }

	public void setProduct(Product product) {
        this.product = product;
    }

	public int getQuantity() {
        return this.quantity;
    }

	public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

	public int getWeight() {
        return this.weight;
    }

	public void setWeight(int weight) {
        this.weight = weight;
    }

	public String getUnit() {
        return this.unit;
    }

	public void setUnit(String unit) {
        this.unit = unit;
    }

	public Date getCreatedDate() {
        return this.createdDate;
    }

	public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

	public String getStatus() {
        return this.status;
    }

	public void setStatus(String status) {
        this.status = status;
    }

	public UserAccount getOwner() {
        return this.owner;
    }

	public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("product", "quantity", "weight", "unit", "createdDate", "status", "owner", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new Cart().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countCarts() {
        return findAllCarts().size();
    }

	@Transactional
    public static List<Cart> findAllCarts() {
        return entityManager().createQuery("SELECT o FROM Cart o", Cart.class).getResultList();
    }

	@Transactional
    public static List<Cart> findAllCarts(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Cart o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Cart.class).getResultList();
    }

	@Transactional
    public static Cart findCart(Long id) {
        if (id == null) return null;
        return entityManager().find(Cart.class, id);
    }

	@Transactional
    public static List<Cart> findCartEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Cart o", Cart.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<Cart> findCartEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Cart o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Cart.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Cart attached = Cart.findCart(this.id);
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
    public Cart merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Cart merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
