package fr.ensim.interop.introrest.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fr.ensim.interop.introrest.model.telegram.ApiResponseTelegram;
import fr.ensim.interop.introrest.model.telegram.Chat;
import fr.ensim.interop.introrest.model.telegram.Message;

@RestController
public class MessageRestController {

    @Value("${telegram.api.url}")
    private String telegramApiUrl;

    RestTemplate restTemplate = new RestTemplate();

    // Opérations sur la ressource Message

    @GetMapping(value = "/message", params = { "chat_id", "text" })
    public ResponseEntity<ApiResponseTelegram> sendMessage(
            @RequestParam("chat_id") Long chat_id,
            @RequestParam("text") String text) {

        // text obligatoire, non vide, non blanc
        if (!StringUtils.hasText(text)) {
            return ResponseEntity.badRequest().build();
        }

        Message messageToSend = new Message();
        messageToSend.setText(text);
        messageToSend.setChat(new Chat(chat_id, "private"));

        URI targetUrl = UriComponentsBuilder.fromUriString(telegramApiUrl) // Build the base link
                .path("/sendMessage") // Add path
                .queryParam("chat_id", chat_id) // Add one or more query params
                .queryParam("text", text)
                .build() // Build the URL
                .encode() // Encode any URI items that need to be encoded
                .toUri();

        ApiResponseTelegram<Message> response = restTemplate.getForObject(targetUrl, ApiResponseTelegram.class);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/getUpdates", params = { "offset" })
    public ResponseEntity<ApiResponseTelegram> getUpdates(@RequestParam("offset") int offset) {
        URI targetUrl = UriComponentsBuilder.fromUriString(telegramApiUrl)
                .path("/getUpdates")
                .queryParam("offset", offset)
                .build().encode().toUri();
        ApiResponseTelegram response = restTemplate.getForObject(targetUrl, ApiResponseTelegram.class);
        return ResponseEntity.ok().body(response);
    }
}
