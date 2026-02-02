package ru.okvedfinder.domain;

public class MatchResult {
    private String normalizedPhone;
    private OkvedEntry okved;
    private int matchLength;

    public MatchResult(String normalizedPhone, OkvedEntry okved, int matchLength) {
        this.normalizedPhone = normalizedPhone;
        this.okved = okved;
        this.matchLength = matchLength;
    }

    public String getNormalizedPhone() {
        return normalizedPhone;
    }

    public void setNormalizedPhone(String normalizedPhone) {
        this.normalizedPhone = normalizedPhone;
    }

    public OkvedEntry getOkved() {
        return okved;
    }

    public void setOkved(OkvedEntry okved) {
        this.okved = okved;
    }

    public int getMatchLength() {
        return matchLength;
    }

    public void setMatchLength(int matchLength) {
        this.matchLength = matchLength;
    }
}