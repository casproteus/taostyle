// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.domain.orders;

import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;
import com.stgo.taostyle.domain.orders.MainOrder;
import com.stgo.taostyle.domain.orders.TaxonomyMaterialOrder;
import java.util.Date;

privileged aspect TaxonomyMaterialOrder_Roo_JavaBean {
    
    public MainOrder TaxonomyMaterialOrder.getMainOrder() {
        return this.mainOrder;
    }
    
    public void TaxonomyMaterialOrder.setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
    }
    
    public UserAccount TaxonomyMaterialOrder.getFactory() {
        return this.factory;
    }
    
    public void TaxonomyMaterialOrder.setFactory(UserAccount factory) {
        this.factory = factory;
    }
    
    public String TaxonomyMaterialOrder.getProduceFormat() {
        return this.produceFormat;
    }
    
    public void TaxonomyMaterialOrder.setProduceFormat(String produceFormat) {
        this.produceFormat = produceFormat;
    }
    
    public Date TaxonomyMaterialOrder.getOrderDate() {
        return this.orderDate;
    }
    
    public void TaxonomyMaterialOrder.setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public UserAccount TaxonomyMaterialOrder.getEmployee() {
        return this.employee;
    }
    
    public void TaxonomyMaterialOrder.setEmployee(UserAccount employee) {
        this.employee = employee;
    }
    
    public String TaxonomyMaterialOrder.getItemName() {
        return this.itemName;
    }
    
    public void TaxonomyMaterialOrder.setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String TaxonomyMaterialOrder.getColor() {
        return this.color;
    }
    
    public void TaxonomyMaterialOrder.setColor(String color) {
        this.color = color;
    }
    
    public String TaxonomyMaterialOrder.getSizeSpecification() {
        return this.sizeSpecification;
    }
    
    public void TaxonomyMaterialOrder.setSizeSpecification(String sizeSpecification) {
        this.sizeSpecification = sizeSpecification;
    }
    
    public UserAccount TaxonomyMaterialOrder.getSupplier() {
        return this.supplier;
    }
    
    public void TaxonomyMaterialOrder.setSupplier(UserAccount supplier) {
        this.supplier = supplier;
    }
    
    public int TaxonomyMaterialOrder.getQuantity() {
        return this.quantity;
    }
    
    public void TaxonomyMaterialOrder.setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int TaxonomyMaterialOrder.getCONS() {
        return this.CONS;
    }
    
    public void TaxonomyMaterialOrder.setCONS(int CONS) {
        this.CONS = CONS;
    }
    
    public String TaxonomyMaterialOrder.getUnit() {
        return this.unit;
    }
    
    public void TaxonomyMaterialOrder.setUnit(String unit) {
        this.unit = unit;
    }
    
    public int TaxonomyMaterialOrder.getOrderQuantity() {
        return this.orderQuantity;
    }
    
    public void TaxonomyMaterialOrder.setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
    
    public int TaxonomyMaterialOrder.getExt_quantity() {
        return this.ext_quantity;
    }
    
    public void TaxonomyMaterialOrder.setExt_quantity(int ext_quantity) {
        this.ext_quantity = ext_quantity;
    }
    
    public int TaxonomyMaterialOrder.getRealQuantity() {
        return this.realQuantity;
    }
    
    public void TaxonomyMaterialOrder.setRealQuantity(int realQuantity) {
        this.realQuantity = realQuantity;
    }
    
    public int TaxonomyMaterialOrder.getUnitPrice() {
        return this.unitPrice;
    }
    
    public void TaxonomyMaterialOrder.setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int TaxonomyMaterialOrder.getTotalAmount() {
        return this.totalAmount;
    }
    
    public void TaxonomyMaterialOrder.setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int TaxonomyMaterialOrder.getRecd_qty() {
        return this.recd_qty;
    }
    
    public void TaxonomyMaterialOrder.setRecd_qty(int recd_qty) {
        this.recd_qty = recd_qty;
    }
    
    public Date TaxonomyMaterialOrder.getRecd_date() {
        return this.recd_date;
    }
    
    public void TaxonomyMaterialOrder.setRecd_date(Date recd_date) {
        this.recd_date = recd_date;
    }
    
    public String TaxonomyMaterialOrder.getRemark() {
        return this.remark;
    }
    
    public void TaxonomyMaterialOrder.setRemark(String remark) {
        this.remark = remark;
    }
    
    public UserAccount TaxonomyMaterialOrder.getApprovedManager() {
        return this.approvedManager;
    }
    
    public void TaxonomyMaterialOrder.setApprovedManager(UserAccount approvedManager) {
        this.approvedManager = approvedManager;
    }
    
    public String TaxonomyMaterialOrder.getApprove_status() {
        return this.approve_status;
    }
    
    public void TaxonomyMaterialOrder.setApprove_status(String approve_status) {
        this.approve_status = approve_status;
    }
    
    public String TaxonomyMaterialOrder.getResonForApproveOrDeny() {
        return this.resonForApproveOrDeny;
    }
    
    public void TaxonomyMaterialOrder.setResonForApproveOrDeny(String resonForApproveOrDeny) {
        this.resonForApproveOrDeny = resonForApproveOrDeny;
    }
    
    public Person TaxonomyMaterialOrder.getPerson() {
        return this.person;
    }
    
    public void TaxonomyMaterialOrder.setPerson(Person person) {
        this.person = person;
    }
    
    public int TaxonomyMaterialOrder.getStatus() {
        return this.status;
    }
    
    public void TaxonomyMaterialOrder.setStatus(int status) {
        this.status = status;
    }
    
    public int TaxonomyMaterialOrder.getRecordStatus() {
        return this.recordStatus;
    }
    
    public void TaxonomyMaterialOrder.setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }
    
}
