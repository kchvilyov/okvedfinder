package com.okvedfinder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.okvedfinder.domain.OkvedEntry;
import ru.okvedfinder.service.OkvedLoader;

public class Main {
    private static final Pattern RUS_MOBILE_PATTERN =
            Pattern.compile("^\\+?7[9|8]\\d{9}$");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar okved-finder.jar \"phone number\"");
            System.out.println("Example: java -jar okved-finder.jar \"8 (916) 123-45-67\"");
            return;
        }

        try {
            // 1. Нормализация
            String normalized = normalizePhone(args[0]);
            System.out.println("✅ Normalized: " + normalized);

            // 2. Загрузка ОКВЭД
            List<OkvedEntry> okvedList = OkvedLoader.load();

            System.out.println("✅ Loaded OKVED records: " + okvedList.size());

            // 3. Поиск
            findAndPrintMatch(normalized, okvedList);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static String normalizePhone(String phone) {
        String digits = phone.replaceAll("[^\\d+]", "");

        if (digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        } else if (digits.startsWith("+7")) {
            digits = digits.substring(1);
        }

        if (!digits.startsWith("7") || digits.length() != 11) {
            throw new IllegalArgumentException("Invalid Russian mobile number");
        }

        return "+" + digits;
    }

    private static List<OkvedEntry> loadOkved() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (var inputStream = Main.class.getClassLoader().getResourceAsStream("okved.json")) {
            if (inputStream == null) {
                throw new IOException("okved.json not found in resources");
            }
            return mapper.readValue(inputStream, new TypeReference<List<OkvedEntry>>() {});
        }
    }

    private static void findAndPrintMatch(String phone, List<OkvedEntry> entries) {
        String phoneDigits = phone.replaceAll("\\D", "");
        OkvedEntry bestMatch = null;
        int maxLength = 0;

        for (OkvedEntry entry : entries) {
            String codeDigits = entry.getCode().replaceAll("\\D", "");

            for (int len = Math.min(codeDigits.length(), phoneDigits.length());
                 len > 0; len--) {

                String phoneEnd = phoneDigits.substring(phoneDigits.length() - len);
                String codeEnd = codeDigits.substring(codeDigits.length() - len);

                if (phoneEnd.equals(codeEnd) && len > maxLength) {
                    bestMatch = entry;
                    maxLength = len;
                    break;
                }
            }
        }

        if (bestMatch != null) {
            System.out.printf("\n📱 Phone: %s\n", phone);
            System.out.printf("📊 OKVED: %s — %s\n",
                    bestMatch.getCode(), bestMatch.getName());
            System.out.printf("🔢 Match length: %d\n", maxLength);
        } else {
            System.out.println("\n⚠️  No matches found, using default");
            System.out.printf("📱 Phone: %s\n", phone);
            System.out.println("📊 OKVED: 99.99 — Activity not determined");
            System.out.println("🔢 Match length: 0");
        }
    }
}