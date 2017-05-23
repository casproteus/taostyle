package com.stgo.taostyle.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.stgo.taostyle.web.TaoUtil;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Product implements MultiLanguageabl {

    @NotNull
    @Column(unique = true)
    @Size(min = 2)
    private String partNo;

    private String name;

    private String producedplace;

    private String c1;

    private String c2;

    private String c3;

    private String Litorweight;

    private String unit;

    private String packagesname;

    private String itemquantity;

    private int lenth;

    private int width;

    private int height;

    private int price1;

    private int price2;

    private int price3;

    private String description;

    @ManyToOne
    private Person person;

    private int recordStatus;

    @Transient
    private String localName;

    @Transient
    private String localDescription;

    public static List<com.stgo.taostyle.domain.Product> findProductsByCatalogAndPerson(
            String pCatalog,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Product> tQuery =
                tEntityManager.createQuery(
                        "SELECT o FROM Product AS o WHERE o.c1 = :pKey and o.person = :person ORDER BY o.id DESC",
                        Product.class);
        tQuery = tQuery.setParameter("pKey", pCatalog);
        tQuery = tQuery.setParameter("person", person);
        List<Product> tProducts;
        try {
            tProducts = tQuery.getResultList();
        } catch (Exception e) {
            tProducts = new ArrayList<Product>();
        }
        return tProducts;
    }

    public static com.stgo.taostyle.domain.Product findProductsByPartNoAndPerson(
            String tPartNo,
            Person person) {
        EntityManager tEntityManager = entityManager();
        TypedQuery<Product> tQuery =
                tEntityManager.createQuery("SELECT o FROM Product AS o WHERE o.partNo = :pKey and o.person = :person",
                        Product.class);
        tQuery = tQuery.setParameter("pKey", tPartNo);
        tQuery = tQuery.setParameter("person", person);
        Product tProduct = null;
        try {
            tProduct = tQuery.getSingleResult();
        } catch (Exception e) {
        }
        return tProduct;
    }

    public String getLocalName() {
        return localName;
    }

    @Override
    public void setLocalName(
            String localName) {
        this.localName = localName;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    @Override
    public void setLocalDescription(
            String localDescription) {
        this.localDescription = localDescription;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(
            Person person) {
        this.person = person;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(
            int recordStatus) {
        this.recordStatus = recordStatus;
    }

    @Transient
    public Object[] getPrices() {
        Integer[] prices = new Integer[3];
        prices[0] = this.price1;
        prices[1] = this.price2;
        prices[2] = this.price3;
        return TaoUtil.vacuumArray(prices);
    }

    @Transient
    public Object[] getCategories() {
        String[] categories = new String[3];
        categories[0] = this.c1;
        categories[1] = this.c2;
        categories[2] = this.c3;
        return TaoUtil.vacuumArray(categories);
    }
}
