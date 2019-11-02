package com.example.rent;

public class Property {

    public Property(){

    }

   private String price , type , image;

    public String getPrice() {
        return price;
    }


    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public Property(String price, String type, String image) {
        this.price = price;
        this.type = type;
        this.image = image;
    }


}
