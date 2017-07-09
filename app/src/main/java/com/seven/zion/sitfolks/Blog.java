package com.seven.zion.sitfolks;

import com.google.firebase.storage.StorageReference;

/**
 * Created by Stephen on 13-May-17.
 */

public class Blog {
    private String title;
    private String desc;
    private String image;
    private String imgSref;

    public Blog(){

    }
    public Blog(String title, String desc, String image,String imgSref)
    {
        this.desc = desc;
        this.title = title;
        this.image = image;
        this.imgSref = imgSref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getImgSref() {
        return imgSref;
    }

    public void setImgSref(String imgSref) {
        this.imgSref = imgSref;
    }
}
