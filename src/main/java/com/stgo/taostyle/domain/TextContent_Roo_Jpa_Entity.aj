// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain;

import com.stgo.taostyle.domain.TextContent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect TextContent_Roo_Jpa_Entity {
    
    declare @type: TextContent: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long TextContent.id;
    
    @Version
    @Column(name = "version")
    private Integer TextContent.version;
    
    public Long TextContent.getId() {
        return this.id;
    }
    
    public void TextContent.setId(Long id) {
        this.id = id;
    }
    
    public Integer TextContent.getVersion() {
        return this.version;
    }
    
    public void TextContent.setVersion(Integer version) {
        this.version = version;
    }
    
}
