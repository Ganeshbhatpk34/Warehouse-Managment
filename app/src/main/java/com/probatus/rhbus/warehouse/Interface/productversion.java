package com.probatus.rhbus.warehouse.Interface;

import java.util.Comparator;

/**
 * Created by Ganapathi on 11-03-2018.
 */

public class productversion {

    private String android_version_name,product_description,product_price;
    private String product_quantity,product_total,product_itemcode,product_barcode ;
    private String product_delete,tender_desc,tender_code;
    private String product_hsn,product_discount,product_uom,product_lineno;
    private String cgst_percent,cgst_amt,sgst_percent,sgst_amt;
    private String product_linedisc,product_isFOC;
    private String product_category,product_subcategory,product_brand;
    private byte[] android_image_url;
    private boolean SelectedFlag=false;

    public String getAndroid_version_name() {
        return android_version_name;
    }

    public void setAndroid_version_name(String android_version_name) {
        this.android_version_name = android_version_name;
    }

    public byte[] getAndroid_image_url() {
        return android_image_url;
    }

    public void setAndroid_image_url(byte[] android_image_url) {
        this.android_image_url = android_image_url;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_barcode() {
        return product_barcode;
    }

    public void setProduct_barcode(String product_barcode) {
        this.product_barcode = product_barcode;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_total() {
        return product_total;
    }

    public void setProduct_total(String product_total) {
        this.product_total = product_total;
    }

    public String getProduct_itemcode() {
        return product_itemcode;
    }

    public void setProduct_itemcode(String product_itemcode) {
        this.product_itemcode = product_itemcode;
    }

    public String getProduct_delete() {
        return product_delete;
    }

    public void setProduct_delete(String product_delete) {
        this.product_delete = product_delete;
    }

    public String getProduct_linedisc() {
        return product_linedisc;
    }

    public void setProduct_linedisc(String linedisc) {
        this.product_linedisc = linedisc;
    }

    public Boolean getSelectedFlag() {
        return SelectedFlag;
    }

    public void setSelectedFlag(Boolean context) {
        this.SelectedFlag = context;
    }


    public String getProduct_hsn() {
        return product_hsn;
    }

    public void setProduct_hsn(String product_hsn) {
        this.product_hsn = product_hsn;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;}

    public String getProduct_uom() {
        return product_uom;
    }

    public void setProduct_uom(String product_uom) {
        this.product_uom = product_uom;
    }

    public String getProduct_lineno() {
        return product_lineno;
    }

    public void setProduct_lineno(String product_lineno) {
        this.product_lineno = product_lineno;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_subcategory() {
        return product_subcategory;
    }

    public void setProduct_subcategory(String product_subcategory) {
        this.product_subcategory = product_subcategory;
    }

    public String getProduct_brand() {
        return product_brand;
    }

    public void setProduct_brand(String product_brand) {
        this.product_brand = product_brand;
    }

    public String getProduct_sgstperc() {
        return sgst_percent;
    }

    public void setProduct_sgstperc(String sgst_percent) {
        this.sgst_percent = sgst_percent;
    }

    public String getProduct_sgstamt() {
        return sgst_amt;
    }

    public void setProduct_sgstamt(String sgst_amt) {
        this.sgst_amt = sgst_amt;
    }

    public String getProduct_cgstperc() {
        return cgst_percent;
    }

    public void setProduct_cgstperc(String cgst_percent) {
        this.cgst_percent = cgst_percent;
    }

    public String getProduct_cgstamt() {
        return cgst_amt;
    }

    public void setProduct_cgstamt(String cgst_amt) {
        this.cgst_amt = cgst_amt;
    }



    public static Comparator<productversion> StuNameComparator = new Comparator<productversion>() {

        public int compare(productversion s1, productversion s2) {
            String StudentName1 = s1.getProduct_itemcode().toUpperCase();
            String StudentName2 = s2.getProduct_itemcode().toUpperCase();

            return StudentName1.compareTo(StudentName2);

        }};

    /*Comparator for sorting the list by roll no*/
    public static Comparator<productversion> StuDescComparator = new Comparator<productversion>() {

        public int compare(productversion s1, productversion s2) {
            String StudentName1 = s1.getProduct_description().toUpperCase();
            String StudentName2 = s2.getProduct_description().toUpperCase();

            return StudentName1.compareTo(StudentName2);

        }};

}
