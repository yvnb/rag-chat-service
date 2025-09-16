package com.spring.ragchatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class RagChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagChatServiceApplication.class, args);
    }

}
