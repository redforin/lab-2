package com.example.project;

public class Fruit extends Food {
    public Boolean isFresh;// свежий ли фрукт

    public Fruit(int kkal, String title, Boolean isFresh) {
        super(kkal, title);
        this.isFresh = isFresh;
    }

    public Fruit() {};
    @Override
    public String getDescription() {
        String isFreshString = this.isFresh ? "свежий" : "не свежий";
        return String.format("Фрукт %s", isFreshString);
    }
}
