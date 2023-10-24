package com.example.project;

import java.util.ArrayList;

public class Cookie extends Food // булочка
{
    public Boolean withSugar;// с сахаром ли?
    public Boolean withPoppy;// или маком?
    public Boolean withSesame;// а может с кунжутом?

    public Cookie(int kkal, String title, Boolean withSugar, Boolean withPoppy, Boolean withSesame) {
        super(kkal, title);
        this.withSugar = withSugar;
        this.withPoppy = withPoppy;
        this.withSesame = withSesame;
    }

    public Cookie() {};

    @Override
    public String getDescription() {
        ArrayList<String> items = new ArrayList<>();
        if (this.withSugar)
            items.add("с сахаром");
        if (this.withPoppy)
            items.add("с маком");
        if (this.withSesame)
            items.add("с кунжутом");

        return String.format("Булочка %s", String.join(", ", items));
    }
}