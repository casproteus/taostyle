package com.stgo.taostyle.web;

import java.util.ArrayList;
import java.util.List;

import com.stgo.taostyle.domain.Customize;
import com.stgo.taostyle.domain.Feature;
import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Service;
import com.stgo.taostyle.domain.TextContent;
import com.stgo.taostyle.domain.UserAccount;
import com.stgo.taostyle.domain.orders.MainOrder;
import com.stgo.taostyle.domain.orders.Material;
import com.stgo.taostyle.domain.orders.TaxonomyMaterial;

import flexjson.JSONDeserializer;

public class TaoDbUtil {
    public static int saveToLocalDB(
            Person person,
            List<String> tList) {
        if (!saveUserAccountToLocalDB(person, tList.get(0))) // useraccount
            return 0;
        if (!saveCustomizesToLocalDB(person, tList.get(1))) // customize
            return 1;
        if (!saveFeatureToLocalDB(person, tList.get(2))) // feature
            return 2;
        if (!saveMainOrdersToLocalDB(person, tList.get(3))) // mainOrder
            return 3;
        if (!saveMaterialsToLocalDB(person, tList.get(4))) // material
            return 4;
        if (!saveTaxonomyMaterialsToLocalDB(person, tList.get(5))) // taxonomyMaterial
            return 5;
        if (!saveServicesToLocalDB(person, tList.get(6))) // service
            return 6;
        if (!saveTextContentsToLocalDB(person, tList.get(7))) // textContent
            return 7;
        if (!saveMediaUploadsToLocalDB(person, tList.get(8))) // uploadMedia
            return 8;

        return -1;
    }

    private static boolean saveUserAccountToLocalDB(
            Person person,
            String json) {
        List<UserAccount> pList = new JSONDeserializer<List<UserAccount>>().use(null, ArrayList.class)
                .use("values", UserAccount.class).deserialize(json);

        System.out.println(
                "start to save UserAccount! there are [" + pList.size() + "] items to save---------------------");

        for (int i = 0; i < pList.size(); i++) {
            System.out.println(i);
            UserAccount pUA = pList.get(i);
            String loginname = pUA.getLoginname();
            int p = loginname.indexOf("*");
            loginname = loginname.substring(0, p + 1) + person.getId();
            UserAccount tUA = UserAccount.findUserAccountByName(loginname);
            if (tUA != null) { // have same one. update properties
                tUA.remove();
            }
            pUA.setLoginname(loginname);
            pUA.setId(null); // make the Id null, or the recode with that ID will be replaced.
            pUA.persist();
        }
        return true;
    }

    private static boolean saveCustomizesToLocalDB(
            Person person,
            String json) {
        List<Customize> items = new JSONDeserializer<List<Customize>>().use(null, ArrayList.class)
                .use("values", Customize.class).deserialize(json);

        System.out.println(
                "start to save customizes! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            Customize instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveFeatureToLocalDB(
            Person person,
            String json) {
        List<Feature> items = new JSONDeserializer<List<Feature>>().use(null, ArrayList.class)
                .use("values", Feature.class).deserialize(json);

        System.out
                .println("start to save Feature! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            Feature instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveMainOrdersToLocalDB(
            Person person,
            String json) {
        List<MainOrder> items = new JSONDeserializer<List<MainOrder>>().use(null, ArrayList.class)
                .use("values", MainOrder.class).deserialize(json);

        System.out.println(
                "start to save MainOrder! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            MainOrder instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveMaterialsToLocalDB(
            Person person,
            String json) {
        List<Material> items = new JSONDeserializer<List<Material>>().use(null, ArrayList.class)
                .use("values", Material.class).deserialize(json);

        System.out
                .println("start to save Material! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            Material instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveTaxonomyMaterialsToLocalDB(
            Person person,
            String json) {
        List<TaxonomyMaterial> items = new JSONDeserializer<List<TaxonomyMaterial>>().use(null, ArrayList.class)
                .use("values", TaxonomyMaterial.class).deserialize(json);

        System.out.println(
                "start to save TaxonomyMaterial! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            TaxonomyMaterial instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveServicesToLocalDB(
            Person person,
            String json) {
        List<Service> items = new JSONDeserializer<List<Service>>().use(null, ArrayList.class)
                .use("values", Service.class).deserialize(json);

        System.out
                .println("start to save Service! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            Service instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveTextContentsToLocalDB(
            Person person,
            String json) {
        List<TextContent> items = new JSONDeserializer<List<TextContent>>().use(null, ArrayList.class)
                .use("values", TextContent.class).deserialize(json);

        System.out.println(
                "start to save TextContent! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            TextContent instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveMediaUploadsToLocalDB(
            Person person,
            String json) {
        List<MediaUpload> items = new JSONDeserializer<List<MediaUpload>>().use(null, ArrayList.class)
                .use("values", MediaUpload.class).deserialize(json);

        System.out.println(
                "start to save MediaUpload! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            MediaUpload instance = items.get(i);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

}
