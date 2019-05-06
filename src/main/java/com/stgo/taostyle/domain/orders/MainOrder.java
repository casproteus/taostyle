package com.stgo.taostyle.domain.orders;

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
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;

@Configurable
@Entity
public class MainOrder {

    @ManyToOne
    private UserAccount client;

    private String clientSideOrderNumber;

    private String clientSideModelNumber;

    private String model;

    @ManyToOne
    private MediaUpload picture;

    private String productName;

    @ManyToOne
    private UserAccount contactPerson;

    private int quantity;

    private String payCondition;

    private Boolean colorProportionReceived;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date delieverdate;

    private String sizeTable;

    private String colorCard;

    private String testRequirement;

    private String sampleRequirement;

    private String Remark;

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

    public static List<MainOrder> findMainOrderByCON(
            String pClientSideOrderNumber) {
        TypedQuery<MainOrder> tQuery =
                entityManager().createQuery(
                        "SELECT o FROM MainOrder AS o WHERE UPPER(o.clientSideOrderNumber) = UPPER(:tCON)",
                        MainOrder.class);
        tQuery = tQuery.setParameter("tCON", pClientSideOrderNumber);
        List<MainOrder> tList = tQuery.getResultList();
        return tList;
    }

    public static List<MainOrder> finMainOrdersBySizeTableAndPerson(
            String SizeTable,
            Person person) {
        TypedQuery<MainOrder> tQuery =
                entityManager()
                        .createQuery(
                                "SELECT o FROM MainOrder AS o WHERE o.sizeTable = :sizeTable and o.person = :person and o.recordStatus = 0 order by o.id desc",
                                MainOrder.class);
        tQuery = tQuery.setParameter("sizeTable", SizeTable);
        tQuery = tQuery.setParameter("person", person);
        return tQuery.getResultList();
    }

    public String toString() {
        // StringBuilder tStrBuilder = new
        // StringBuilder(client.toString()).append(" | ").append(model).append(" | ").append(quantity);
        // return tStrBuilder.toString();//ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return clientSideOrderNumber;
    }

    @Transactional
    public static List<MainOrder> findMainOrdersByClientAndPerson(
            UserAccount client,
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.client = :client ORDER BY o.id "
                                + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);
        tQuery = tQuery.setParameter("client", client);

        // we display all uncompleated order, if want to get today's order, should open a new method.
        // and o.delieverdate > :delieverdate
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 1);
        // tQuery = tQuery.setParameter("delieverdate", calendar.getTime());

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findUnCompletedMainOrdersByPerson(
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager
                        .createQuery(
                                "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.recordStatus > -1 and o.recordStatus < 10000 ORDER BY o.id "
                                        + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);

        // we display all uncompleated order, if want to get today's order, should open a new method.
        // and o.delieverdate > :delieverdate
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 1);
        // tQuery = tQuery.setParameter("delieverdate", calendar.getTime());

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findMainOrdersByStatusAndPerson(
            int recordStatus,
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.recordStatus = :recordStatus ORDER BY o.id "
                                + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);
        tQuery = tQuery.setParameter("recordStatus", recordStatus);

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findCompletedMainOrdersByPerson(
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MainOrder AS o WHERE o.person = :person and o.recordStatus < 0 ORDER BY o.id "
                                + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);

        // we display all uncompleated order, if want to get today's order, should open a new method.
        // and o.delieverdate > :delieverdate
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 1);
        // tQuery = tQuery.setParameter("delieverdate", calendar.getTime());

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }

    @Transactional
    public static List<MainOrder> findMainOrdersByPerson(
            Person person,
            String order) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MainOrder> tQuery =
                tEntityManager.createQuery("SELECT o FROM MainOrder AS o WHERE o.person = :person ORDER BY o.id "
                        + order, MainOrder.class);
        tQuery = tQuery.setParameter("person", person);

        List<MainOrder> mainOrders = null;
        try {
            mainOrders = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }

        return mainOrders;
    }


	public UserAccount getClient() {
        return this.client;
    }

	public void setClient(UserAccount client) {
        this.client = client;
    }

	public String getClientSideOrderNumber() {
        return this.clientSideOrderNumber;
    }

	public void setClientSideOrderNumber(String clientSideOrderNumber) {
        this.clientSideOrderNumber = clientSideOrderNumber;
    }

	public String getClientSideModelNumber() {
        return this.clientSideModelNumber;
    }

	public void setClientSideModelNumber(String clientSideModelNumber) {
        this.clientSideModelNumber = clientSideModelNumber;
    }

	public String getModel() {
        return this.model;
    }

	public void setModel(String model) {
        this.model = model;
    }

	public MediaUpload getPicture() {
        return this.picture;
    }

	public void setPicture(MediaUpload picture) {
        this.picture = picture;
    }

	public String getProductName() {
        return this.productName;
    }

	public void setProductName(String productName) {
        this.productName = productName;
    }

	public UserAccount getContactPerson() {
        return this.contactPerson;
    }

	public void setContactPerson(UserAccount contactPerson) {
        this.contactPerson = contactPerson;
    }

	public int getQuantity() {
        return this.quantity;
    }

	public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

	public String getPayCondition() {
        return this.payCondition;
    }

	public void setPayCondition(String payCondition) {
        this.payCondition = payCondition;
    }

	public Boolean getColorProportionReceived() {
        return this.colorProportionReceived;
    }

	public void setColorProportionReceived(Boolean colorProportionReceived) {
        this.colorProportionReceived = colorProportionReceived;
    }

	public Date getDelieverdate() {
        return this.delieverdate;
    }

	public void setDelieverdate(Date delieverdate) {
        this.delieverdate = delieverdate;
    }

	public String getSizeTable() {
        return this.sizeTable;
    }

	public void setSizeTable(String sizeTable) {
        this.sizeTable = sizeTable;
    }

	public String getColorCard() {
        return this.colorCard;
    }

	public void setColorCard(String colorCard) {
        this.colorCard = colorCard;
    }

	public String getTestRequirement() {
        return this.testRequirement;
    }

	public void setTestRequirement(String testRequirement) {
        this.testRequirement = testRequirement;
    }

	public String getSampleRequirement() {
        return this.sampleRequirement;
    }

	public void setSampleRequirement(String sampleRequirement) {
        this.sampleRequirement = sampleRequirement;
    }

	public String getRemark() {
        return this.Remark;
    }

	public void setRemark(String Remark) {
        this.Remark = Remark;
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

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("client", "clientSideOrderNumber", "clientSideModelNumber", "model", "picture", "productName", "contactPerson", "quantity", "payCondition", "colorProportionReceived", "delieverdate", "sizeTable", "colorCard", "testRequirement", "sampleRequirement", "Remark", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new MainOrder().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countMainOrders() {
        return findAllMainOrders().size();
    }

	@Transactional
    public static List<MainOrder> findAllMainOrders() {
        return entityManager().createQuery("SELECT o FROM MainOrder o", MainOrder.class).getResultList();
    }

	@Transactional
    public static List<MainOrder> findAllMainOrders(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM MainOrder o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, MainOrder.class).getResultList();
    }

	@Transactional
    public static MainOrder findMainOrder(Long id) {
        if (id == null) return null;
        return entityManager().find(MainOrder.class, id);
    }

	@Transactional
    public static List<MainOrder> findMainOrderEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MainOrder o", MainOrder.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<MainOrder> findMainOrderEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM MainOrder o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, MainOrder.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            MainOrder attached = MainOrder.findMainOrder(this.id);
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
    public MainOrder merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        MainOrder merged = this.entityManager.merge(this);
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
}
