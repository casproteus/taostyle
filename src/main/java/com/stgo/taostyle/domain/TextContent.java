package com.stgo.taostyle.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
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

@Entity
@Configurable
public class TextContent {

    @NotNull
    private String posInPage;

    private String content;

    private String publisher;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date markDate;

    @ManyToOne
    private Person person;

    private int recordStatus;

    public static com.stgo.taostyle.domain.TextContent findContentsByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<TextContent> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TextContent AS o WHERE o.posInPage = :pKey and o.person = :person",
                        TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        TextContent textContent = null;
        try {
            textContent = tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return textContent;
    }

    public static String findTextByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<String> tQuery =
                tEntityManager.createQuery(
                        "SELECT o.content FROM TextContent AS o WHERE o.posInPage = :pKey and o.person = :person",
                        String.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        String text = null;
        try {
            text = tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return text;
    }

    public static List<TextContent> findContentsByMediaIdAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<TextContent> tQuery =
                tEntityManager
                        .createQuery(
                                "SELECT o FROM TextContent AS o WHERE o.posInPage = :pKey and o.person = :person ORDER BY o.markDate DESC",
                                TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        List<TextContent> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static List<TextContent> findAllMatchedTextContents(
            String pKey,
            String sorter,
            Person person) {
        EntityManager tEntityManager = entityManager();
        if (sorter == null) {
            sorter = " ORDER BY o.markDate DESC";
        } else {
            sorter = " ORDER BY o." + sorter;
        }

        TypedQuery<TextContent> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person" + sorter,
                        TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        List<TextContent> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static List<TextContent> findAllMatchingTextContents(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();

        TypedQuery<TextContent> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person",
                        TextContent.class);
        tQuery = tQuery.setParameter("pKey", pKey);
        tQuery = tQuery.setParameter("person", person);
        List<TextContent> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static List<String> findAllMatchedContent(
            String pKey,
            String field,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<String> tQuery =
                tEntityManager
                        .createQuery(
                                "SELECT o."
                                        + field
                                        + " FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person ORDER BY o.posInPage",
                                String.class);
        tQuery = tQuery.setParameter("pKey", pKey + "%");
        tQuery = tQuery.setParameter("person", person);
        List<String> tObeFR;
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
        return tObeFR;
    }

    public static long countTextContentsByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Long> tQuery =
                tEntityManager.createQuery(
                        "SELECT COUNT(o) FROM TextContent AS o WHERE o.posInPage LIKE :pKey and o.person = :person",
                        Long.class);
        tQuery = tQuery.setParameter("pKey", pKey + "%");
        tQuery = tQuery.setParameter("person", person);
        return tQuery.getSingleResult();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(
            String publisher) {
        this.publisher = publisher;
    }

    public Date getMarkDate() {
        return markDate;
    }

    public void setMarkDate(
            Date markDate) {
        this.markDate = markDate;
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

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static TextContent fromJsonToTextContent(String json) {
        return new JSONDeserializer<TextContent>()
        .use(null, TextContent.class).deserialize(json);
    }

	public static String toJsonArray(Collection<TextContent> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<TextContent> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<TextContent> fromJsonArrayToTextContents(String json) {
        return new JSONDeserializer<List<TextContent>>()
        .use("values", TextContent.class).deserialize(json);
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public String getPosInPage() {
        return this.posInPage;
    }

	public void setPosInPage(String posInPage) {
        this.posInPage = posInPage;
    }

	public String getContent() {
        return this.content;
    }

	public void setContent(String content) {
        this.content = content;
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

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("posInPage", "content", "publisher", "markDate", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new TextContent().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countTextContents() {
        return findAllTextContents().size();
    }

	@Transactional
    public static List<TextContent> findAllTextContents() {
        return entityManager().createQuery("SELECT o FROM TextContent o", TextContent.class).getResultList();
    }

	@Transactional
    public static List<TextContent> findAllTextContents(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TextContent o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TextContent.class).getResultList();
    }

	@Transactional
    public static TextContent findTextContent(Long id) {
        if (id == null) return null;
        return entityManager().find(TextContent.class, id);
    }

	@Transactional
    public static List<TextContent> findTextContentEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TextContent o", TextContent.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<TextContent> findTextContentEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TextContent o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TextContent.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            TextContent attached = TextContent.findTextContent(this.id);
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
    public TextContent merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TextContent merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
