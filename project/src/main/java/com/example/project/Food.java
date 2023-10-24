package com.example.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties({"description"}) // указали что свойство description нужно игнорировать
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class Food {

    private int kkal;
    private String title;
    public Integer id = null; // добавили идентификатор, по умолчанию равен null
    public Food(int kkal, String title) {
        this.setKkal(kkal);
        this.setTitle(title);
    }

    public Food() {};

    @Override
    public String toString() {
        return String.format("%s: %s ккал", this.getTitle(), this.getKkal());
    }

    public int getKkal() {
        return kkal;
    }

    public void setKkal(int kkal) {
        this.kkal = kkal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return "";
    }
}
