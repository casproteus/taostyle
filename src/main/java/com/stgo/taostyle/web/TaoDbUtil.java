package com.stgo.taostyle.web;

import java.util.ArrayList;
import java.util.HashMap;
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
        // clean existing db content of person.
        cleanPersonRecords(person);

        HashMap<String, UserAccount> userMap = new HashMap<String, UserAccount>();
        HashMap<Long, MainOrder> mainOrderMap = new HashMap<Long, MainOrder>();

        if (!saveUserAccountToLocalDB(person, tList.get(0), userMap)) // useraccount
            return 0;
        if (!saveCustomizesToLocalDB(person, tList.get(1))) // customize
            return 1;
        if (!saveFeatureToLocalDB(person, tList.get(2))) // feature
            return 2;
        // if (!saveMainOrdersToLocalDB(person, tList.get(3), mainOrderMap, userMap)) // mainOrder
        // return 3;
        // if (!saveMaterialsToLocalDB(person, tList.get(4), mainOrderMap)) // material
        // return 4;
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

    public static void cleanPersonRecords(
            Person person) {
        List<Customize> customizes = Customize.findAllCustomizesByPerson(person);
        for (Customize customize : customizes) {
            customize.remove();
        }

        List<TextContent> textContents = TextContent.findAllMatchedTextContents("%", null, person);
        for (TextContent textContent : textContents) {
            textContent.remove();
        }

        List<MediaUpload> mediaUploads = MediaUpload.findAllMediaUploadByPerson(person);
        for (MediaUpload mediaUpload : mediaUploads) {
            mediaUpload.remove();
        }

        List<Feature> features = Feature.findAllFeaturesByPerson(person);
        for (Feature feature : features) {
            feature.remove();
        }

        List<Service> services = Service.findServiceByPerson(person);
        for (Service service : services) {
            service.remove();
        }

        List<MainOrder> mainOrders = MainOrder.findMainOrdersByPerson(person, "DESC");
        for (MainOrder mainOrder : mainOrders) {
            List<Material> materials = Material.findAllMaterialsByMainOrder(mainOrder);
            for (Material material : materials) {
                material.remove();
            }
            List<TaxonomyMaterial> taxonomyMaterials = TaxonomyMaterial.findAllTaxonomyMaterialsByMainOrder(mainOrder);
            for (TaxonomyMaterial taxonomyMaterial : taxonomyMaterials) {
                taxonomyMaterial.remove();
            }
            mainOrder.remove();
        }

        List<UserAccount> userAccounts = UserAccount.findAllUserAccountsByPerson(person);
        for (UserAccount userAccount : userAccounts) {
            mediaUploads = MediaUpload.findMediaByAuthor(userAccount);
            for (MediaUpload mediaUpload : mediaUploads) {
                mediaUpload.remove();
            }
            userAccount.remove();
        }
    }

    private static boolean saveUserAccountToLocalDB(
            Person person,
            String json,
            HashMap<String, UserAccount> userMap) {
        List<UserAccount> pList = new JSONDeserializer<List<UserAccount>>().use(null, ArrayList.class)
                .use("values", UserAccount.class).deserialize(json);

        System.out.println(
                "start to save UserAccount! there are [" + pList.size() + "] items to save---------------------");

        for (int i = 0; i < pList.size(); i++) {
            System.out.println(i);
            UserAccount pUA = pList.get(i);
            String oldLoginname = pUA.getLoginname();
            int p = oldLoginname.indexOf("*");
            String newLoginname = oldLoginname.substring(0, p + 1) + person.getId();
            UserAccount tUA = UserAccount.findUserAccountByName(newLoginname);
            if (tUA != null) { // have same one. update properties
                TaoDebug.error(
                        "UserAccount not cleaned well before saving exported db into local db. useraccount name: {}, person: {}",
                        newLoginname);
                tUA.remove();
            }
            pUA.setLoginname(newLoginname);
            pUA.setPerson(person);
            pUA.setId(null); // make the Id null, or the recode with that ID will be replaced.
            pUA.persist();
            userMap.put(oldLoginname, pUA);
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
            Customize customize = Customize.findCustomizeByKeyAndPerson(instance.getCusKey(), person);
            if (customize != null) { // have same one. update properties
                TaoDebug.error(
                        "Customize not cleaned well before saving exported db into local db. Customize key: {}, person: {}",
                        instance.getCusKey());
                customize.remove();
            }
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
            String json,
            HashMap<Long, MainOrder> mainOrderMap,
            HashMap<String, UserAccount> userMap) {
        List<MainOrder> items = new JSONDeserializer<List<MainOrder>>().use(null, ArrayList.class)
                .use("values", MainOrder.class).deserialize(json);

        System.out.println(
                "start to save MainOrder! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            MainOrder instance = items.get(i);
            String loginName = instance.getContactPerson().getLoginname();
            instance.setContactPerson(userMap.get(loginName));
            mainOrderMap.put(instance.getId(), instance);
            instance.setPerson(person);
            instance.setId(null); // make the Id null, or the recode with that ID will be replaced.
            instance.persist();
        }
        return true;
    }

    private static boolean saveMaterialsToLocalDB(
            Person person,
            String json,
            HashMap<Long, MainOrder> mainOrderMap) {
        List<Material> items = new JSONDeserializer<List<Material>>().use(null, ArrayList.class)
                .use("values", Material.class).deserialize(json);

        System.out
                .println("start to save Material! there are [" + items.size() + "] items to save---------------------");

        for (int i = 0; i < items.size(); i++) {
            System.out.println(i);
            Material instance = items.get(i);
            instance.setMainOrder(mainOrderMap.get(instance.getMainOrder().getId()));
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
