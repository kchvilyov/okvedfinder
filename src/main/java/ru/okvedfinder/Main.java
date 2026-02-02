package ru.okvedfinder;

import ru.okvedfinder.domain.OkvedEntry;
import ru.okvedfinder.domain.MatchResult;
import ru.okvedfinder.service.OkvedFinder;
import ru.okvedfinder.service.OkvedLoader;
import ru.okvedfinder.service.PhoneNumberNormalizer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Нормализация номера
            String inputPhone = args.length > 0 ? args[0] : "8 (916) 123-45-67";
            String normalizedPhone = PhoneNumberNormalizer.normalize(inputPhone);
            System.out.println("Нормализованный номер: " + normalizedPhone);
            
            // 2. Загрузка ОКВЭД
            List<OkvedEntry> okvedList = OkvedLoader.load();
            
            // 3. Поиск ОКВЭД
            MatchResult result = OkvedFinder.findBestMatch(normalizedPhone, okvedList);
            
            // 4. Вывод результата
            System.out.println("Найденный ОКВЭД: " + result.getOkved().getCode() + " — " + result.getOkved().getName());
            System.out.println("Длина совпадения: " + result.getMatchLength());
            
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка нормализации: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}