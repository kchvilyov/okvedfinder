package ru.okvedfinder.domain;

/**
 * Результат поиска ОКВЭД с дополнительной информацией о стратегии
 */
public record PhoneMatchResult(String normalizedPhone, OkvedEntry okved, int matchLength, String strategyUsed) {

    // Конструктор для основного поиска (не резервного)
    public PhoneMatchResult(String normalizedPhone, OkvedEntry okved, int matchLength) {
        this(normalizedPhone, okved, matchLength, "suffix_match");
    }

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