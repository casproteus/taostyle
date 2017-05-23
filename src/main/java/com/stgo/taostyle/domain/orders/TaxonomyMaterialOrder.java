package com.stgo.taostyle.domain.orders;

import java.util.Date;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import com.stgo.taostyle.domain.UserAccount;
import com.stgo.taostyle.domain.Person;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class TaxonomyMaterialOrder {

    @ManyToOne
    private MainOrder mainOrder;

    @NotNull
    @ManyToOne
    private UserAccount factory;

    private String produceFormat;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date orderDate;

    @ManyToOne
    private UserAccount employee;

    private String itemName;

    private String color;

    private String sizeSpecification;

    @ManyToOne
    private UserAccount supplier;

    @NotNull
    private int quantity;

    private int CONS;

    private String unit;

    private int orderQuantity;

    private int ext_quantity;

    private int realQuantity;

    private int unitPrice;

    private int totalAmount;

    private int recd_qty;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date recd_date;

    private String remark;

    @ManyToOne
    private UserAccount approvedManager;

    private String approve_status;

    private String resonForApproveOrDeny;

    @Transient
    private String mainOrderStr;

    public String getMainOrderStr() {
        return mainOrderStr;
    }

    public void setMainOrderStr(
            String mainOrderStr) {
        this.mainOrderStr = mainOrderStr;
    }

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
