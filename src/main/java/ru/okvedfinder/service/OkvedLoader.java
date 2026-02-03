package ru.okvedfinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.okvedfinder.domain.OkvedEntry;
import ru.okvedfinder.exceptions.ValidationException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OkvedLoader {
    private static final String OKVED_URL = "https://raw.githubusercontent.com/bergstar/testcase/master/okved.json";

    public static List<OkvedEntry> load() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OKVED_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new ValidationException("HTTP " + response.statusCode() + ": " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        // Парсим напрямую в список, так как JSON — это массив
        return mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(List.class, OkvedEntry.class));
    }
}