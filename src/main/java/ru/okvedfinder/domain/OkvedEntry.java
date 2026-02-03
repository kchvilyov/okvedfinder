package ru.okvedfinder.domain;

import java.util.List;

public class OkvedEntry {
    private String code;
    private String name;
    private List<OkvedEntry> items;

    //Конструктор по умолчанию (нужен для Jackson)
    public OkvedEntry() {
    }

    public OkvedEntry(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<OkvedEntry> getItems() {
        return items;
    }
}