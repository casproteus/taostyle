// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain;

import com.stgo.taostyle.domain.City;
import com.stgo.taostyle.domain.Country;
import com.stgo.taostyle.domain.Person;
import java.util.Set;

privileged aspect Country_Roo_JavaBean {
    
    public String Country.getName() {
        return this.name;
    }
    
    public void Country.setName(String name) {
        this.name = name;
    }
    
    public boolean Country.isIsActive() {
        return this.isActive;
    }
    
    public void Country.setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public String Country.getDescription() {
        return this.description;
    }
    
    public void Country.setDescription(String description) {
        this.description = description;
    }
    
    public Set<City> Country.getCities() {
        return this.cities;
    }
    
    public void Country.setCities(Set<City> cities) {
        this.cities = cities;
    }
    
    public Person Country.getPerson() {
        return this.person;
    }
    
    public void Country.setPerson(Person person) {
        this.person = person;
    }
    
    public int Country.getRecordStatus() {
        return this.recordStatus;
    }
    
    public void Country.setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }
    
}
