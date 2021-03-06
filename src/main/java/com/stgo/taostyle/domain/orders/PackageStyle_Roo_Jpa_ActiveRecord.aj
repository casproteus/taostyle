// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain.orders;

import com.stgo.taostyle.domain.orders.PackageStyle;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PackageStyle_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager PackageStyle.entityManager;
    
    public static final List<String> PackageStyle.fieldNames4OrderClauseFilter = java.util.Arrays.asList("mainOrder", "itemName", "qualitySpecification", "sizeSpecification", "color", "quantity", "location", "remark", "person", "recordStatus");
    
    public static final EntityManager PackageStyle.entityManager() {
        EntityManager em = new PackageStyle().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    @Transactional
    public static long PackageStyle.countPackageStyles() {
        return findAllPackageStyles().size();
    }
    
    @Transactional
    public static List<PackageStyle> PackageStyle.findAllPackageStyles() {
        return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class).getResultList();
    }
    
    @Transactional
    public static List<PackageStyle> PackageStyle.findAllPackageStyles(String sortFieldName, String sortOrder) {
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
    public static PackageStyle PackageStyle.findPackageStyle(Long id) {
        if (id == null) return null;
        return entityManager().find(PackageStyle.class, id);
    }
    
    @Transactional
    public static List<PackageStyle> PackageStyle.findPackageStyleEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PackageStyle o", PackageStyle.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public static List<PackageStyle> PackageStyle.findPackageStyleEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
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
    public void PackageStyle.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void PackageStyle.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            PackageStyle attached = PackageStyle.findPackageStyle(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void PackageStyle.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void PackageStyle.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public PackageStyle PackageStyle.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PackageStyle merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
