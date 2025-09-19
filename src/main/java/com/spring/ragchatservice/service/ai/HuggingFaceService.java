package com.spring.ragchatservice.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class HuggingFaceService implements AIService {

    private final WebClient webClient;

    @Value("${huggingface.api.key}")
    private String apiKey;
    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Override
    public String generateResponse(String prompt, String context) {

        String input = (context != null ? context + "\n" : "") + prompt;

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue("{\"inputs\": \"" + input + "\"}")
                .retrieve()
                .bodyToMono(String.class)

                .block(); // wait for the response
    }

}
