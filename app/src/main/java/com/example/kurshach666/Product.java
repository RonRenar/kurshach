package com.example.kurshach666;

public class Product {
    private String name;
    private String imageUrl;
    private Integer price;
    private String date;
    public Product(String name, String price, String imageUrl) {
        this.name = name;
        this.price = Integer.parseInt(price); // Преобразуем строку в Integer
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getPrice() {
        return price;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
