package ru.okvedfinder.domain;

import java.util.List;

public class OkvedEntry {
    private String code;
    private String name;
    private List<OkvedEntry> items;

    // 👉 Конструктор по умолчанию (нужен для Jackson)
    public OkvedEntry() {
    }

    // Опционально: удобный конструктор для вашего кода
    public OkvedEntry(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OkvedEntry> getItems() {
        return items;
    }

    public void setItems(List<OkvedEntry> items) {
        this.items = items;
    }
}