package com.stgo.taostyle.domain;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class City {

    @NotNull
    private String name;

    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    /**
     */
    @ManyToOne
    private Person person;

    /**
     */
    private int status;

    /**
     */
    private int recordStatus;
}
