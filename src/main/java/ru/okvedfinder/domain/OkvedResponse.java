package ru.okvedfinder.domain;

import java.util.List;

public class OkvedResponse {
    private List<OkvedEntry> items;

    public List<OkvedEntry> getItems() {
        return items;
    }

    public void setItems(List<OkvedEntry> items) {
        this.items = items;
    }
}