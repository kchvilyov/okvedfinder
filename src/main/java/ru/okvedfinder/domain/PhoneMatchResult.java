package ru.okvedfinder.domain;

/**
 * Результат поиска ОКВЭД с дополнительной информацией о стратегии
 */
public class PhoneMatchResult {
    private final String normalizedPhone;
    private final OkvedEntry okved;
    private final int matchLength;
    private final String strategyUsed;
    
    public PhoneMatchResult(String normalizedPhone, OkvedEntry okved, 
                           int matchLength, String strategyUsed) {
        this.normalizedPhone = normalizedPhone;
        this.okved = okved;
        this.matchLength = matchLength;
        this.strategyUsed = strategyUsed;
    }
    
    // Конструктор для основного поиска (не резервного)
    public PhoneMatchResult(String normalizedPhone, OkvedEntry okved, int matchLength) {
        this(normalizedPhone, okved, matchLength, "suffix_match");
    }
    
    public String getNormalizedPhone() { return normalizedPhone; }
    public OkvedEntry getOkved() { return okved; }
    public int getMatchLength() { return matchLength; }
    public String getStrategyUsed() { return strategyUsed; }
    
    public boolean isReserveStrategy() {
        return !"suffix_match".equals(strategyUsed);
    }
    
    @Override
    public String toString() {
        return String.format(
            "Номер: %s%nОКВЭД: %s — %s%nСовпадение: %d цифр%nСтратегия: %s",
            normalizedPhone,
            okved.getCode(),
            okved.getName(),
            matchLength,
            strategyUsed
        );
    }
    
    /**
     * Форматированный вывод для консоли
     */
    public String toFormattedString() {
        String strategyInfo = isReserveStrategy() ? 
            "(резервная стратегия: " + strategyUsed + ")" : "";
        
        return String.format(
            """
            📱 Номер: %s
            📊 ОКВЭД: %s — %s
            🔢 Длина совпадения: %d %s
            """,
            normalizedPhone,
            okved.getCode(),
            okved.getName(),
            matchLength,
            strategyInfo
        );
    }
}