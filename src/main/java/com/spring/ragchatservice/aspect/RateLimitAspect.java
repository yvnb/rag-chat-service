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

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable  {

        String clientIp = getClientIp();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodKey = signature.getName();

        String rateLimitKey = "rate_limit:%s:%s".formatted(clientIp, methodKey);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimitKey);

        // Initialize rate if not exists (permits, interval, timeunit)
        rateLimiter.trySetRate(RateType.OVERALL, rateLimit.capacity(), Duration.ofSeconds(rateLimit.interval()));
        rateLimiter.expire(Duration.ofMinutes(3));

        if (!rateLimiter.tryAcquire(1)) {
            throw new RateLimitException("Rate limit exceeded. Please try again later");
        }

        return joinPoint.proceed();

    }

    private String getClientIp() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            return request.getRemoteAddr();
        }
        return ipAddress.split(",")[0].trim();
    }

}
