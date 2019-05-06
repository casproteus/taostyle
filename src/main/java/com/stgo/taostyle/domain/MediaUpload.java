package com.stgo.taostyle.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
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
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.stgo.taostyle.web.CC;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
public class MediaUpload {

    @NotNull
    @Size(max = 128)
    @Column(unique = false)
    private String filepath;

    private long filesize;

    private String contentType;

    private byte[] content;

    @ManyToOne
    private UserAccount publisher;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date submitDate;

    @ManyToOne
    private UserAccount audient;

    private String status;

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;

    // to count how many pictures in database are using a filepath starts with this key string.
    public static long countMediaUploadsByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Long> tQuery = tEntityManager.createQuery(
                "SELECT COUNT(o) FROM MediaUpload AS o WHERE o.filepath LIKE :pKey and o.person = :person", Long.class);
        tQuery.setParameter("pKey", pKey + "_%");
        tQuery.setParameter("person", person);
        long tCount = 0;
        try {
            tCount = tQuery.getSingleResult();
        } catch (Exception e) {
            tCount = 0;
        }
        return tCount;
    }

    public static List<MediaUpload> listAllMediaUploadsByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<MediaUpload> tQuery = tEntityManager.createQuery(
                "SELECT o FROM MediaUpload AS o WHERE o.filepath LIKE :pKey and o.person = :person", MediaUpload.class);
        tQuery.setParameter("pKey", pKey + "_%");
        tQuery.setParameter("person", person);
        try {
            return tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> listAllMediaUploadsKeyByKeyAndPerson(
            String pKey,
            Person person) {
        EntityManager tEntityManager = entityManager();
        // note "ORDER BY o.filepath" is useless, because _10 will be infront of _2.
        TypedQuery<String> tQuery = tEntityManager.createQuery(
                "SELECT o.filepath FROM MediaUpload AS o WHERE o.filepath LIKE :pKey and o.person = :person",
                String.class);
        tQuery.setParameter("pKey", pKey + "_%");
        tQuery.setParameter("person", person);
        try {
            return tQuery.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    // used for getting out a image from DB.
    // @NOTE: status for image, is used to save it's "language version status".
    public static MediaUpload findMediaByKeyAndPerson(
            String key,
            Person person) {
        EntityManager tEntityManager = entityManager();
        tEntityManager.setFlushMode(FlushModeType.COMMIT);
        MediaUpload tObeFR;
        TypedQuery<MediaUpload> tQuery = tEntityManager.createQuery(
                "SELECT o FROM MediaUpload AS o WHERE o.filepath = :pKey and o.person =:person order by o.id DESC",
                MediaUpload.class);
        tQuery = tQuery.setParameter("pKey", key);
        tQuery = tQuery.setParameter("person", person);
        try {
            List<MediaUpload> mediaUploads = tQuery.getResultList();
            tObeFR = mediaUploads.get(0);
        } catch (Exception e) {
            tObeFR = null;
        }
        return tObeFR;
    }

    // get out files from DB by audient.
    public static List<MediaUpload> findMediaByAudientAndPerson(
            UserAccount usr,
            Person person) {
        EntityManager tEntityManager = entityManager();
        tEntityManager.setFlushMode(FlushModeType.COMMIT);
        List<MediaUpload> tObeFR;
        TypedQuery<MediaUpload> tQuery;
        Customize customize = Customize.findCustomizeByKeyAndPerson(CC.app_ContentManager, person);
        if (customize != null && usr.getLoginname().toLowerCase().equals(customize.getCusValue().toLowerCase())) {
            tQuery = tEntityManager.createQuery(
                    "SELECT o FROM MediaUpload AS o WHERE o.publisher is not null ORDER BY o.submitDate DESC",
                    MediaUpload.class);
        } else {
            tQuery = tEntityManager.createQuery(
                    "SELECT o FROM MediaUpload AS o WHERE o.audient = :pKey ORDER BY o.submitDate DESC",
                    MediaUpload.class);
            tQuery = tQuery.setParameter("pKey", usr);
        }
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            tObeFR = new ArrayList<MediaUpload>();
        }
        return tObeFR;
    }

    // get out files from DB by author.
    public static List<MediaUpload> findMediaByAuthor(
            UserAccount usr) {
        EntityManager tEntityManager = entityManager();
        tEntityManager.setFlushMode(FlushModeType.COMMIT);
        List<MediaUpload> tObeFR;
        TypedQuery<MediaUpload> tQuery = tEntityManager.createQuery(
                "SELECT o FROM MediaUpload AS o WHERE o.publisher = :pKey ORDER BY o.submitDate DESC",
                MediaUpload.class);
        tQuery = tQuery.setParameter("pKey", usr);
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            tObeFR = new ArrayList<MediaUpload>();
        }
        return tObeFR;
    }

    public static List<MediaUpload> findAllMediaUploadByPerson(
            Person person) {
        EntityManager tEntityManager = entityManager();
        tEntityManager.setFlushMode(FlushModeType.COMMIT);
        List<MediaUpload> tObeFR;
        TypedQuery<MediaUpload> tQuery = tEntityManager.createQuery(
                "SELECT o FROM MediaUpload AS o WHERE o.person = :pKey ORDER BY o.submitDate DESC", MediaUpload.class);
        tQuery = tQuery.setParameter("pKey", person);
        try {
            tObeFR = tQuery.getResultList();
        } catch (Exception e) {
            tObeFR = new ArrayList<MediaUpload>();
        }
        return tObeFR;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(
            Date submitDate) {
        this.submitDate = submitDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static MediaUpload fromJsonToMediaUpload(String json) {
        return new JSONDeserializer<MediaUpload>()
        .use(null, MediaUpload.class).deserialize(json);
    }

	public static String toJsonArray(Collection<MediaUpload> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<MediaUpload> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<MediaUpload> fromJsonArrayToMediaUploads(String json) {
        return new JSONDeserializer<List<MediaUpload>>()
        .use("values", MediaUpload.class).deserialize(json);
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("filepath", "filesize", "contentType", "content", "publisher", "submitDate", "audient", "status", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new MediaUpload().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countMediaUploads() {
        return findAllMediaUploads().size();
    }

	@Transactional
    public static List<MediaUpload> findAllMediaUploads() {
        return entityManager().createQuery("SELECT o FROM MediaUpload o", MediaUpload.class).getResultList();
    }

	@Transactional
    public static List<MediaUpload> findAllMediaUploads(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM MediaUpload o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, MediaUpload.class).getResultList();
    }

	@Transactional
    public static MediaUpload findMediaUpload(Long id) {
        if (id == null) return null;
        return entityManager().find(MediaUpload.class, id);
    }

	@Transactional
    public static List<MediaUpload> findMediaUploadEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MediaUpload o", MediaUpload.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<MediaUpload> findMediaUploadEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM MediaUpload o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, MediaUpload.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            MediaUpload attached = MediaUpload.findMediaUpload(this.id);
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
    public MediaUpload merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        MediaUpload merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String getFilepath() {
        return this.filepath;
    }

	public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

	public long getFilesize() {
        return this.filesize;
    }

	public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

	public String getContentType() {
        return this.contentType;
    }

	public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public byte[] getContent() {
        return this.content;
    }

	public void setContent(byte[] content) {
        this.content = content;
    }

	public UserAccount getPublisher() {
        return this.publisher;
    }

	public void setPublisher(UserAccount publisher) {
        this.publisher = publisher;
    }

	public UserAccount getAudient() {
        return this.audient;
    }

	public void setAudient(UserAccount audient) {
        this.audient = audient;
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
