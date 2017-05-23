// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain;

import com.stgo.taostyle.domain.MediaUpload;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect MediaUpload_Roo_Json {
    
    public String MediaUpload.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String MediaUpload.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static MediaUpload MediaUpload.fromJsonToMediaUpload(String json) {
        return new JSONDeserializer<MediaUpload>()
        .use(null, MediaUpload.class).deserialize(json);
    }
    
    public static String MediaUpload.toJsonArray(Collection<MediaUpload> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String MediaUpload.toJsonArray(Collection<MediaUpload> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<MediaUpload> MediaUpload.fromJsonArrayToMediaUploads(String json) {
        return new JSONDeserializer<List<MediaUpload>>()
        .use("values", MediaUpload.class).deserialize(json);
    }
    
}
