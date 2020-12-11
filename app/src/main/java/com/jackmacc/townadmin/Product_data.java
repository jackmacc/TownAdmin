package com.jackmacc.townadmin;

class Product_data {

    private String name;
    private String price;
    private String desc;
    private String photo;

    private String id;

    public Product_data() {
    }

    public Product_data(String name, String photo,String price, String id) {
        this.name = name;
        this.photo = photo;
        this.id = id;
        this.price=price;
    }


    public Product_data(String name, String photo,String price) {
        this.name = name;
        this.photo = photo;

        this.price=price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
