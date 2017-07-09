package com.seven.zion.sitfolks;

/**
 * Created by Stephen on 27-Mar-17.
 */

public class Item {
    private String desc;
    private String image;
    private String price;
    private String stock;
    private String icode;

    public Item(){

    }

    public Item(String desc, String image, String price) {
        this.desc = desc;
        this.image = image;
        this.price = price;
    }
    public Item(String desc, String image, String price,String icode,String stock) {
        this.desc = desc;
        this.image = image;
        this.price = price;
        this.icode = icode;
        this.stock = stock;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getIcode() {
        return icode;
    }

    public void setIcode(String icode) {
        this.icode = icode;
    }
}
