// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain.orders;

import com.stgo.taostyle.domain.orders.SizeProportion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SizeProportion_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager SizeProportion.entityManager;
    
    public static final List<String> SizeProportion.fieldNames4OrderClauseFilter = java.util.Arrays.asList("mainOrder", "Color", "smallsize", "middlesize", "largeSize", "extraLargeSize", "other1", "other2", "other3", "other4", "total", "Remark", "person", "recordStatus");
    
    public static final EntityManager SizeProportion.entityManager() {
        EntityManager em = new SizeProportion().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    @Transactional
    public static long SizeProportion.countSizeProportions() {
        return findAllSizeProportions().size();
    }
    
    @Transactional
    public static List<SizeProportion> SizeProportion.findAllSizeProportions() {
        return entityManager().createQuery("SELECT o FROM SizeProportion o", SizeProportion.class).getResultList();
    }
    
    @Transactional
    public static List<SizeProportion> SizeProportion.findAllSizeProportions(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM SizeProportion o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, SizeProportion.class).getResultList();
    }
    
    @Transactional
    public static SizeProportion SizeProportion.findSizeProportion(Long id) {
        if (id == null) return null;
        return entityManager().find(SizeProportion.class, id);
    }
    
    @Transactional
    public static List<SizeProportion> SizeProportion.findSizeProportionEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SizeProportion o", SizeProportion.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public static List<SizeProportion> SizeProportion.findSizeProportionEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM SizeProportion o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, SizeProportion.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void SizeProportion.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SizeProportion.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SizeProportion attached = SizeProportion.findSizeProportion(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SizeProportion.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SizeProportion.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SizeProportion SizeProportion.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SizeProportion merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
