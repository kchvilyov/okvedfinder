package ru.okvedfinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.okvedfinder.domain.OkvedEntry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OkvedLoader {
    private static final String OKVED_URL = "https://github.com/bergstar/testcase/blob/master/okved.json";

    public static List<OkvedEntry> load() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OKVED_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        List<OkvedEntry> entries = mapper.readValue(response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, OkvedEntry.class));

        return entries;
    }
}