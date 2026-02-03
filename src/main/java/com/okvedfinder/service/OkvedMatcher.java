package ru.okvedfinder.service;

import ru.okvedfinder.domain.OkvedEntry;
import ru.okvedfinder.domain.PhoneMatchResult;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class OkvedMatcher {

    /**
     * Реализация резервной стратегии поиска ОКВЭД
     *
     * Алгоритм выбора резервной стратегии (по приоритету):
     * 1. Поиск по любой подстроке номера в коде ОКВЭД
     * 2. Поиск по контрольной сумме (детерминированный)
     * 3. Случайный выбор с seed из номера
     * 4. Возврат дефолтного ОКВЭД
     */
    public PhoneMatchResult applyReserveStrategy(String phoneDigits, List<OkvedEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return createDefaultResult(phoneDigits);
        }

        // Удаляем код страны для поиска (первые 2 цифры: 79)
        String searchDigits = phoneDigits.length() > 2 ?
                phoneDigits.substring(2) : phoneDigits;

        // Стратегия 1: Поиск по любой подстроке номера в коде ОКВЭД
        Optional<OkvedEntry> substringMatch = findBySubstring(searchDigits, entries);
        if (substringMatch.isPresent()) {
            return new PhoneMatchResult(
                    "+" + phoneDigits,
                    substringMatch.get(),
                    0, // Длина совпадения = 0 для резервной стратегии
                    "substring_reserve"
            );
        }

        // Стратегия 2: Поиск по контрольной сумме
        OkvedEntry checksumMatch = findByChecksum(phoneDigits, entries);
        if (checksumMatch != null && !isDefaultOkved(checksumMatch)) {
            return new PhoneMatchResult(
                    "+" + phoneDigits,
                    checksumMatch,
                    0,
                    "checksum_reserve"
            );
        }

        // Стратегия 3: Детерминированный случайный выбор (seed из номера)
        OkvedEntry deterministicRandom = findByDeterministicRandom(phoneDigits, entries);
        return new PhoneMatchResult(
                "+" + phoneDigits,
                deterministicRandom,
                0,
                "deterministic_random_reserve"
        );
    }

    /**
     * Стратегия 1: Поиск по любой подстроке номера в цифрах кода ОКВЭД
     * Ищет максимальную подстроку номера в коде ОКВЭД
     */
    private Optional<OkvedEntry> findBySubstring(String phoneDigits, List<OkvedEntry> entries) {
        // Пытаемся найти подстроки разной длины, начиная с самых длинных
        for (int len = Math.min(phoneDigits.length(), 6); len >= 3; len--) {
            for (int i = 0; i <= phoneDigits.length() - len; i++) {
                String substring = phoneDigits.substring(i, i + len);

                for (OkvedEntry entry : entries) {
                    String codeDigits = extractDigits(entry.getCode());
                    if (codeDigits.contains(substring)) {
                        return Optional.of(entry);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Стратегия 2: Детерминированный выбор по контрольной сумме
     * Всегда возвращает один и тот же ОКВЭД для одного номера
     */
    private OkvedEntry findByChecksum(String phoneDigits, List<OkvedEntry> entries) {
        if (entries.isEmpty()) return null;

        // Простая контрольная сумма из цифр номера
        int sum = 0;
        for (char c : phoneDigits.toCharArray()) {
            if (Character.isDigit(c)) {
                sum += Character.getNumericValue(c);
            }
        }

        // Добавляем вес позиции для уменьшения коллизий
        for (int i = 0; i < phoneDigits.length(); i++) {
            char c = phoneDigits.charAt(i);
            if (Character.isDigit(c)) {
                sum += Character.getNumericValue(c) * (i + 1);
            }
        }

        int index = Math.abs(sum) % entries.size();
        return entries.get(index);
    }

    /**
     * Стратегия 3: Детерминированный случайный выбор
     * Использует номер как seed для Random, поэтому всегда один результат
     */
    private OkvedEntry findByDeterministicRandom(String phoneDigits, List<OkvedEntry> entries) {
        if (entries.isEmpty()) {
            return createDefaultOkved();
        }

        // Создаем seed из номера
        long seed = 0;
        for (char c : phoneDigits.toCharArray()) {
            seed = seed * 31 + c;
        }

        // Детерминированный random с seed
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // Note: ThreadLocalRandom не поддерживает seed в основном API
        // Вместо этого используем простой детерминированный алгоритм
        int index = (int) (Math.abs(seed) % entries.size());
        return entries.get(index);
    }

    /**
     * Создает результат с дефолтным ОКВЭД
     */
    private PhoneMatchResult createDefaultResult(String phoneDigits) {
        return new PhoneMatchResult(
                "+" + phoneDigits,
                createDefaultOkved(),
                0,
                "default_reserve"
        );
    }

    /**
     * Создает дефолтный ОКВЭД
     */
    private OkvedEntry createDefaultOkved() {
        return new OkvedEntry(
                "99.99",
                "Деятельность не определена по данному номеру телефона"
        );
    }

    /**
     * Проверяет, является ли ОКВЭД дефолтным
     */
    private boolean isDefaultOkved(OkvedEntry entry) {
        return entry.getCode().equals("99.99") ||
                entry.getCode().equals("00.00");
    }

    /**
     * Извлекает только цифры из строки
     */
    private String extractDigits(String str) {
        return str.replaceAll("\\D", "");
    }

    /**
     * Конфигурируемая резервная стратегия через функциональный интерфейс
     */
    public Function<String, Optional<OkvedEntry>> createConfigurableStrategy(
            List<OkvedEntry> entries, String strategyType) {

        return switch (strategyType.toLowerCase()) {
            case "substring" -> phone -> findBySubstring(phone, entries);
            case "checksum" -> phone -> Optional.ofNullable(findByChecksum(phone, entries));
            case "first_digit" -> phone -> findByFirstDigit(phone, entries);
            case "last_digit" -> phone -> findByLastDigit(phone, entries);
            default -> phone -> Optional.of(findByDeterministicRandom(phone, entries));
        };
    }

    /**
     * Дополнительная стратегия: по первой цифре после кода страны
     */
    private Optional<OkvedEntry> findByFirstDigit(String phoneDigits, List<OkvedEntry> entries) {
        if (phoneDigits.length() < 3) return Optional.empty();

        char firstDigit = phoneDigits.charAt(2); // Первая цифра после 79
        return entries.stream()
                .filter(e -> e.getCode().startsWith(String.valueOf(firstDigit)))
                .findFirst();
    }

    /**
     * Дополнительная стратегия: по последней цифре
     */
    private Optional<OkvedEntry> findByLastDigit(String phoneDigits, List<OkvedEntry> entries) {
        if (phoneDigits.isEmpty()) return Optional.empty();

        char lastDigit = phoneDigits.charAt(phoneDigits.length() - 1);
        return entries.stream()
                .filter(e -> e.getCode().endsWith(String.valueOf(lastDigit)))
                .findFirst();
    }
}