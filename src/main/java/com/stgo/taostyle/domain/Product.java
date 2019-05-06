package com.stgo.taostyle.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import com.stgo.taostyle.web.TaoUtil;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configurable
@Entity
public class Product implements MultiLanguageabl {

    @NotNull
    @Column(unique = true)
    @Size(min = 2)
    private String partNo;

    private String name;

    private String producedplace;

    private String c1;

    private String c2;

    private String c3;

    private String Litorweight;

    private String unit;

    private String packagesname;

    private String itemquantity;

    private int lenth;

    private int width;

    private int height;

    private int price1;

    private int price2;

    private int price3;

    private String description;

    @ManyToOne
    private Person person;

    private int recordStatus;

    @Transient
    private String localName;

    @Transient
    private String localDescription;

    public static Product findProductByCatalogAndPerson(
            String pCatalog,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Product> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Product AS o WHERE o.c1 = :pKey and o.person = :person ORDER BY o.id DESC",
                        Product.class);
        tQuery = tQuery.setParameter("pKey", pCatalog);
        tQuery = tQuery.setParameter("person", person);
        Product product = null;
        try {
            product = tQuery.getSingleResult();
        } catch (Exception e) {
            // do nothing
        }
        return product;
    }

    public static com.stgo.taostyle.domain.Product findProductsByPartNoAndPerson(
            String tPartNo,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Product> tQuery =
                tEntityManager.createQuery("SELECT o FROM Product AS o WHERE o.partNo = :pKey and o.person = :person",
                        Product.class);
        tQuery = tQuery.setParameter("pKey", tPartNo);
        tQuery = tQuery.setParameter("person", person);
        Product tProduct = null;
        try {
            tProduct = tQuery.getSingleResult();
        } catch (Exception e) {
        }
        return tProduct;
    }

    public String getLocalName() {
        return localName;
    }

    @Override
    public void setLocalName(
            String localName) {
        this.localName = localName;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    @Override
    public void setLocalDescription(
            String localDescription) {
        this.localDescription = localDescription;
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

    @Transient
    public Object[] getPrices() {
        Integer[] prices = new Integer[3];
        prices[0] = this.price1;
        prices[1] = this.price2;
        prices[2] = this.price3;
        return TaoUtil.vacuumArray(prices);
    }

    @Transient
    public Object[] getCategories() {
        String[] categories = new String[3];
        categories[0] = this.c1;
        categories[1] = this.c2;
        categories[2] = this.c3;
        return TaoUtil.vacuumArray(categories);
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Product().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countProducts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Product o", Long.class).getSingleResult();
    }

	public static List<Product> findAllProducts() {
        return entityManager().createQuery("SELECT o FROM Product o", Product.class).getResultList();
    }

	public static Product findProduct(Long id) {
        if (id == null) return null;
        return entityManager().find(Product.class, id);
    }

	public static List<Product> findProductEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Product o", Product.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
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
            Product attached = Product.findProduct(this.id);
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
    public Product merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Product merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String getPartNo() {
        return this.partNo;
    }

	public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public String getProducedplace() {
        return this.producedplace;
    }

	public void setProducedplace(String producedplace) {
        this.producedplace = producedplace;
    }

	public String getC1() {
        return this.c1;
    }

	public void setC1(String c1) {
        this.c1 = c1;
    }

	public String getC2() {
        return this.c2;
    }

	public void setC2(String c2) {
        this.c2 = c2;
    }

	public String getC3() {
        return this.c3;
    }

	public void setC3(String c3) {
        this.c3 = c3;
    }

	public String getLitorweight() {
        return this.Litorweight;
    }

	public void setLitorweight(String Litorweight) {
        this.Litorweight = Litorweight;
    }

	public String getUnit() {
        return this.unit;
    }

	public void setUnit(String unit) {
        this.unit = unit;
    }

	public String getPackagesname() {
        return this.packagesname;
    }

	public void setPackagesname(String packagesname) {
        this.packagesname = packagesname;
    }

	public String getItemquantity() {
        return this.itemquantity;
    }

	public void setItemquantity(String itemquantity) {
        this.itemquantity = itemquantity;
    }

	public int getLenth() {
        return this.lenth;
    }

	public void setLenth(int lenth) {
        this.lenth = lenth;
    }

	public int getWidth() {
        return this.width;
    }

	public void setWidth(int width) {
        this.width = width;
    }

	public int getHeight() {
        return this.height;
    }

	public void setHeight(int height) {
        this.height = height;
    }

	public int getPrice1() {
        return this.price1;
    }

	public void setPrice1(int price1) {
        this.price1 = price1;
    }

	public int getPrice2() {
        return this.price2;
    }

	public void setPrice2(int price2) {
        this.price2 = price2;
    }

	public int getPrice3() {
        return this.price3;
    }

	public void setPrice3(int price3) {
        this.price3 = price3;
    }

	public String getDescription() {
        return this.description;
    }

	public void setDescription(String description) {
        this.description = description;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Product fromJsonToProduct(String json) {
        return new JSONDeserializer<Product>().use(null, Product.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Product> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Product> fromJsonArrayToProducts(String json) {
        return new JSONDeserializer<List<Product>>().use(null, ArrayList.class).use("values", Product.class).deserialize(json);
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
