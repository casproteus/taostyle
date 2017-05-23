package com.stgo.taostyle.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.stgo.taostyle.web.CC;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class MediaUpload {

    @NotNull
    @Size(max = 128)
    @Column(unique = true)
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
        TypedQuery<Long> tQuery =
                tEntityManager.createQuery(
                        "SELECT COUNT(o) FROM MediaUpload AS o WHERE o.filepath LIKE :pKey and o.person = :person",
                        Long.class);
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
        TypedQuery<MediaUpload> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MediaUpload AS o WHERE o.filepath LIKE :pKey and o.person = :person",
                        MediaUpload.class);
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
        TypedQuery<String> tQuery =
                tEntityManager.createQuery(
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
        TypedQuery<MediaUpload> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MediaUpload AS o WHERE o.filepath = :pKey and o.person =:person",
                        MediaUpload.class);
        tQuery = tQuery.setParameter("pKey", key);
        tQuery = tQuery.setParameter("person", person);
        try {
            tObeFR = tQuery.getSingleResult();
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
            tQuery =
                    tEntityManager.createQuery(
                            "SELECT o FROM MediaUpload AS o WHERE o.publisher is not null ORDER BY o.submitDate DESC",
                            MediaUpload.class);
        } else {
            tQuery =
                    tEntityManager.createQuery(
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
        TypedQuery<MediaUpload> tQuery =
                tEntityManager.createQuery(
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
        TypedQuery<MediaUpload> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM MediaUpload AS o WHERE o.person = :pKey ORDER BY o.submitDate DESC",
                        MediaUpload.class);
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
}
