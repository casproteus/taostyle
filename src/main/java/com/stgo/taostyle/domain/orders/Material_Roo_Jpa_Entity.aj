// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain.orders;

import com.stgo.taostyle.domain.orders.Material;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect Material_Roo_Jpa_Entity {
    
    declare @type: Material: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Material.id;
    
    @Version
    @Column(name = "version")
    private Integer Material.version;
    
    public Long Material.getId() {
        return this.id;
    }
    
    public void Material.setId(Long id) {
        this.id = id;
    }
    
    public Integer Material.getVersion() {
        return this.version;
    }
    
    public void Material.setVersion(Integer version) {
        this.version = version;
    }
    
}
