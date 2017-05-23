// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain;

import com.stgo.taostyle.domain.Customize;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Customize_Roo_Json {
    
    public String Customize.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String Customize.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static Customize Customize.fromJsonToCustomize(String json) {
        return new JSONDeserializer<Customize>()
        .use(null, Customize.class).deserialize(json);
    }
    
    public static String Customize.toJsonArray(Collection<Customize> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String Customize.toJsonArray(Collection<Customize> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<Customize> Customize.fromJsonArrayToCustomizes(String json) {
        return new JSONDeserializer<List<Customize>>()
        .use("values", Customize.class).deserialize(json);
    }
    
}
