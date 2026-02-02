package ru.okvedfinder.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OkvedEntry {
    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    public OkvedEntry() {}

    public OkvedEntry(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDigitsOnly() {
        return code.replaceAll("\\D", "");
    }

    @Override
    public String toString() {
        return String.format("%s: %s", code, name);
    }
}