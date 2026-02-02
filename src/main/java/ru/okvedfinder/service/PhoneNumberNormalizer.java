package ru.okvedfinder.service;

public class PhoneNumberNormalizer {
    public static String normalize(String phoneNumber) throws IllegalArgumentException {
        // Удаляем все нецифровые символы
        String digits = phoneNumber.replaceAll("\\D", "");

        // Если первая цифра 8, заменяем на 7
        if (digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }

        // Проверяем, что теперь номер начинается с 79 и имеет длину 11 цифр
        if (digits.length() != 11) {
            throw new IllegalArgumentException("Номер должен содержать 11 цифр после очистки");
        }
        if (!digits.startsWith("79")) {
            throw new IllegalArgumentException("Российский мобильный номер должен начинаться с 79");
        }

        return "+" + digits;
    }
}