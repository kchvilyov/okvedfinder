package ru.okvedfinder.service;

import ru.okvedfinder.domain.OkvedEntry;

import java.util.List;

public class ReserveStrategy {
    // 1. Поиск по контрольной сумме (детерминированный)
    public OkvedEntry findByChecksum(String phoneDigits, List<OkvedEntry> entries) {
        int hash = Math.abs(phoneDigits.hashCode());
        int index = hash % entries.size();
        return entries.get(index);
    }
    
    // 2. Поиск по подстроке в середине номера
    public OkvedEntry findBySubstring(String phoneDigits, List<OkvedEntry> entries) {
        // Берем 4 средние цифры номера
        int start = phoneDigits.length() / 2 - 2;
        String middle = phoneDigits.substring(start, start + 4);
        
        return entries.stream()
            .filter(e -> e.getCode().contains(middle))
            .findFirst()
            .orElse(entries.get(0)); // Дефолтный
    }
    
    // 3. Комбинированная стратегия
    public OkvedEntry findReserve(String phoneDigits, List<OkvedEntry> entries) {
        // Пробуем разные методы по порядку
        OkvedEntry result = findBySubstring(phoneDigits, entries);
        if (result.getCode().equals("00.00")) {
            result = findByChecksum(phoneDigits, entries);
        }
        return result;
    }
}