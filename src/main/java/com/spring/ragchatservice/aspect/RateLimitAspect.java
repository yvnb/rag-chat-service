package com.spring.ragchatservice.aspect;

import com.spring.ragchatservice.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedissonClient redissonClient;
    private final Map<String, LimitConfig> apiKeyConfig = new ConcurrentHashMap<>(Map.of(
            "3e413391c9fd5d17e6247377beb218a0", new LimitConfig(100, 60),
            "3e413391c9fd5d17e6247377beb218a1", new LimitConfig(1000, 60)
    ));

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable  {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String clientIp = getClientIp(request);
        String methodKey = getMethodKey(joinPoint);
        String apiKey = getApiKey(request);

        if (!apiKeyConfig.containsKey(apiKey)) {
            throw new RateLimitException("Invalid API key");
        }

        // Per-endpoint, per-IP limit
        String endpointKey = "endpoint:" + clientIp + ":" + methodKey;
        enforceLimiter(endpointKey, rateLimit.capacity(), rateLimit.interval());

        // Global per-API-key limit
        LimitConfig config = apiKeyConfig.get(apiKey);
        String globalKey = "global:{" + apiKey + "}";
        enforceLimiter(globalKey, config.getCapacity(), config.getIntervalInSeconds());

        return joinPoint.proceed();

    }

    private void enforceLimiter(String key, long capacity, long intervalSec) {
        RRateLimiter limiter = redissonClient.getRateLimiter(key);

        // Token-bucket
        limiter.trySetRate(RateType.OVERALL, capacity, Duration.ofSeconds(intervalSec));
        limiter.expire(Duration.ofMinutes(5)); // auto remove idle keys

        if (!limiter.tryAcquire(1)) {
            throw new RateLimitException("Rate limit exceeded");
        }
    }

    private String getClientIp(HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            return request.getRemoteAddr();
        }
        return ipAddress.split(",")[0].trim();
    }

    private String getApiKey(HttpServletRequest request) {
        return request.getHeader("X-API-Key");
    }

    private String getMethodKey(ProceedingJoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getName();
    }

}
