package ru.mirea.pethotel.desktop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.dto.CreateBookingRequest;
import ru.mirea.pethotel.desktop.dto.EnclosureDto;
import ru.mirea.pethotel.desktop.dto.PetDto;
import ru.mirea.pethotel.desktop.dto.UpdateBookingRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HttpBookingApi implements BookingApi {

    public static final String DEFAULT_BASE_URL = "http://localhost:8080/api";

    private final String baseUrl;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)       // даты как "2026-11-14", не как массив чисел
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);   // в ответе есть поля, которых нет в наших DTO

    public HttpBookingApi() {
        this(DEFAULT_BASE_URL);
    }

    public HttpBookingApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<BookingDto> getAll() {
        HttpRequest request = request("/bookings").GET().build();
        return read(send(request), new TypeReference<List<BookingDto>>() {});
    }

    @Override
    public BookingDto create(CreateBookingRequest body) {
        HttpRequest request = request("/bookings")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body)))
                .build();
        return read(send(request), new TypeReference<BookingDto>() {});
    }

    @Override
    public BookingDto update(UUID id, UpdateBookingRequest body) {
        HttpRequest request = request("/bookings/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(body)))
                .build();
        return read(send(request), new TypeReference<BookingDto>() {});
    }

    @Override
    public void delete(UUID id) {
        HttpRequest request = request("/bookings/" + id).DELETE().build();
        send(request);
    }

    @Override
    public List<PetDto> getPets() {
        HttpRequest request = request("/pets").GET().build();
        return read(send(request), new TypeReference<List<PetDto>>() {});
    }

    @Override
    public List<EnclosureDto> getEnclosures() {
        HttpRequest request = request("/enclosures").GET().build();
        return read(send(request), new TypeReference<List<EnclosureDto>>() {});
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl + path)).header("Accept", "application/json");
    }

    // Любой не-2xx ответ превращается в ApiException с текстом ошибки от backend
    private HttpResponse<String> send(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new ApiException(0, "Не удалось связаться с сервером: " + baseUrl);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Запрос прерван");
        }
        if (response.statusCode() / 100 != 2) {
            throw toApiException(response);
        }
        return response;
    }

    private ApiException toApiException(HttpResponse<String> response) {
        int status = response.statusCode();
        try {
            JsonNode json = mapper.readTree(response.body());
            String message = json.path("message").asText("Ошибка сервера (код " + status + ")");
            Map<String, String> fields = new HashMap<>();
            json.path("fields").properties().forEach(entry -> fields.put(entry.getKey(), entry.getValue().asText()));
            return new ApiException(status, message, fields);
        } catch (IOException e) {
            return new ApiException(status, "Ошибка сервера (код " + status + ")");
        }
    }

    private <T> T read(HttpResponse<String> response, TypeReference<T> type) {
        try {
            return mapper.readValue(response.body(), type);
        } catch (JsonProcessingException e) {
            throw new ApiException(response.statusCode(), "Не удалось разобрать ответ сервера");
        }
    }

    private String toJson(Object body) {
        try {
            return mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "Не удалось подготовить запрос");
        }
    }
}
