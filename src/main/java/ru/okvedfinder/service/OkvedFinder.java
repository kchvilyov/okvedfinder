package ru.okvedfinder.service;

import ru.okvedfinder.domain.OkvedEntry;
import ru.okvedfinder.domain.MatchResult;

import java.util.List;

public class OkvedFinder {
    public static MatchResult findBestMatch(String normalizedPhone, List<OkvedEntry> okvedList) {
        // Извлекаем цифры из номера (без '+')
        String phoneDigits = normalizedPhone.substring(1); // убираем '+'
        
        // Поиск по окончанию
        MatchResult bestEndingMatch = findBestEndingMatch(phoneDigits, okvedList);
        if (bestEndingMatch.getMatchLength() > 0) {
            return bestEndingMatch;
        }
        
        // Резервная стратегия: поиск по префиксу
        return findBestPrefixMatch(phoneDigits, okvedList);
    }
    
    private static MatchResult findBestEndingMatch(String phoneDigits, List<OkvedEntry> okvedList) {
        OkvedEntry bestOkved = null;
        int maxLength = 0;
        
        for (OkvedEntry entry : okvedList) {
            // Извлекаем цифры из кода ОКВЭД
            String okvedDigits = entry.getCode().replaceAll("\\D", "");
            
            // Максимальная длина для сравнения окончания
            int maxPossibleLength = Math.min(okvedDigits.length(), phoneDigits.length());
            for (int len = maxPossibleLength; len > 0; len--) {
                String phoneEnd = phoneDigits.substring(phoneDigits.length() - len);
                String okvedEnd = okvedDigits.substring(okvedDigits.length() - len);
                if (phoneEnd.equals(okvedEnd)) {
                    if (len > maxLength) {
                        maxLength = len;
                        bestOkved = entry;
                    }
                    break; // не нужно проверять меньшие len для этого ОКВЭД, так как уже нашли максимальное для него
                }
            }
        }
        
        return new MatchResult("+" + phoneDigits, bestOkved, maxLength);
    }
    
    private static MatchResult findBestPrefixMatch(String phoneDigits, List<OkvedEntry> okvedList) {
        OkvedEntry bestOkved = null;
        int maxLength = 0;
        
        for (OkvedEntry entry : okvedList) {
            String okvedDigits = entry.getCode().replaceAll("\\D", "");
            
            int maxPossibleLength = Math.min(okvedDigits.length(), phoneDigits.length());
            for (int len = maxPossibleLength; len > 0; len--) {
                String phonePrefix = phoneDigits.substring(0, len);
                String okvedPrefix = okvedDigits.substring(0, len);
                if (phonePrefix.equals(okvedPrefix)) {
                    if (len > maxLength) {
                        maxLength = len;
                        bestOkved = entry;
                    }
                    break;
                }
            }
        }
        
        // Если не нашли по префиксу, возвращаем первый ОКВЭД
        if (bestOkved == null) {
            bestOkved = okvedList.get(0);
        }
        
        return new MatchResult("+" + phoneDigits, bestOkved, maxLength);
    }
}