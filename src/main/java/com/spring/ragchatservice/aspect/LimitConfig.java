package com.spring.ragchatservice.aspect;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LimitConfig {
    long capacity;
    long intervalInSeconds;
}
