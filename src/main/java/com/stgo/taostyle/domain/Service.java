package com.stgo.taostyle.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
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
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
public class Service implements MultiLanguageabl {

    private String name;

    private String description;

    private String c1;

    private String c2;

    private String c3;// c3 is now used to put printers string.

    @ManyToOne
    private Person person;

    private int recordStatus;

    @Transient
    private String localName;

    @Transient
    private String localDescription;

    public static Service findServiceByCatalogAndPerson(
            String catalog1,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Service> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Service AS o WHERE o.c1 = :catalog1 and o.person = :person ORDER BY o.id DESC",
                        Service.class);
        tQuery = tQuery.setParameter("catalog1", catalog1);
        tQuery = tQuery.setParameter("person", person);
        Service service = null;
        try {
            service = tQuery.getSingleResult();
        } catch (Exception e) {
            // do nothing.
        }
        return service;
    }

    public static List<Service> findServiceByPerson(
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Service> tQuery =
                tEntityManager.createQuery("SELECT o FROM Service AS o WHERE o.person = :person ORDER BY o.id DESC",
                        Service.class);
        tQuery = tQuery.setParameter("person", person);
        Service service = null;
        List<Service> services = null;
        try {
            services = tQuery.getResultList();
        } catch (Exception e) {
            // do nothing.
        }
        return services;
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


	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
        return this.description;
    }

	public void setDescription(String description) {
        this.description = description;
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

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Service fromJsonToService(String json) {
        return new JSONDeserializer<Service>().use(null, Service.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Service> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Service> fromJsonArrayToServices(String json) {
        return new JSONDeserializer<List<Service>>().use(null, ArrayList.class).use("values", Service.class).deserialize(json);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Service().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countServices() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Service o", Long.class).getSingleResult();
    }

	public static List<Service> findAllServices() {
        return entityManager().createQuery("SELECT o FROM Service o", Service.class).getResultList();
    }

	public static Service findService(Long id) {
        if (id == null) return null;
        return entityManager().find(Service.class, id);
    }

	public static List<Service> findServiceEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Service o", Service.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Service attached = Service.findService(this.id);
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
    public Service merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Service merged = this.entityManager.merge(this);
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
}
