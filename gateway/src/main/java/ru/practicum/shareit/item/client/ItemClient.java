package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, NewCommentRequest request) {
        return post("/" + itemId + "/comment", userId, request);
    }

    public ResponseEntity<Object> getUserItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getById(long itemId) {
        return get("/" + itemId, itemId);
    }

    public ResponseEntity<Object> searchItem(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search", parameters);
    }

    public ResponseEntity<Object> createItem(long userId, NewItemRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, UpdateItemRequest request) {
        return patch("/" + itemId, userId, request);
    }
}
