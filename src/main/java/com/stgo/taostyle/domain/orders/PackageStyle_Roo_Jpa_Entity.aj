// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain.orders;

import com.stgo.taostyle.domain.orders.PackageStyle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect PackageStyle_Roo_Jpa_Entity {
    
    declare @type: PackageStyle: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long PackageStyle.id;
    
    @Version
    @Column(name = "version")
    private Integer PackageStyle.version;
    
    public Long PackageStyle.getId() {
        return this.id;
    }
    
    public void PackageStyle.setId(Long id) {
        this.id = id;
    }
    
    public Integer PackageStyle.getVersion() {
        return this.version;
    }
    
    public void PackageStyle.setVersion(Integer version) {
        this.version = version;
    }
    
}
