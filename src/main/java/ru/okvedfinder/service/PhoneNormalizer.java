package ru.okvedfinder.service;

import ru.okvedfinder.exceptions.ValidationException;

import java.util.regex.Pattern;

public class PhoneNormalizer {
    private static final Pattern RUS_MOBILE_PATTERN = 
        Pattern.compile("^\\+?7[9|8]\\d{9}$");
    
    public String normalize(String phone) throws ValidationException {
        // Удаляем все нецифровые символы кроме плюса
        String digits = phone.replaceAll("[^\\d+]", "");
        
        // Приводим к формату 79XXXXXXXXX
        if (digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        } else if (digits.startsWith("+7")) {
            digits = digits.substring(1); // Убираем +
        }
        
        // Валидация российского мобильного номера
        if (!RUS_MOBILE_PATTERN.matcher("7" + digits).matches()) {
            throw new ValidationException("Неверный формат российского мобильного номера");
        }
        
        return "+7" + digits;
    }
}