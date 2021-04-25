package com.leaf.collegeidleapp.bean;


// 我的收藏实体类

public class Collection {

    //学生学号
    private String username;
    //商品图片
//    private byte[] picture;
    private String picture;
    //商品标题
    private String title;
    //商品描述
    private String description;
    //商品价格
    private float price;
    //联系方式
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
