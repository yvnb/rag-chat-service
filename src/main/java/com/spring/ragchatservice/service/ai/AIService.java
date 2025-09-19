package com.spring.ragchatservice.service.ai;

public interface AIService {

    /**
     * Generate AI response based on prompt and context.
     * @param prompt The user input
     * @param context The conversation context
     * @return AI-generated text
     */
    String generateResponse(String prompt, String context);
}
