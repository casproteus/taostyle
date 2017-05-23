package com.stgo.taostyle.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Country {

    @NotNull
    private String name;

    private boolean isActive;

    @Size(max = 500)
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "country")
    private Set<City> cities = new HashSet<City>();

    @Override
    public String toString() {
        return this.name;
    }

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int recordStatus;
}
