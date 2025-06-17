package ru.ukhanov.t1.java.aop;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class CachedAspect {
    @Value("${cache.time-limit-ms:10000}")
    private long cacheTimeLimit;

    private Cache<String, Object> cache;

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(cacheTimeLimit, TimeUnit.MILLISECONDS)
                .removalListener((RemovalListener<String, Object>) (key, value, cause) -> {
                    if (cause == RemovalCause.EXPIRED) {
                        log.info("Cache expired and removed: key={}, value={}", key, value);
                    } else {
                        log.info("Cache removed: key={}, value={}", key, value);
                    }
                })
                .executor(ForkJoinPool.commonPool())
                .build();
    }

    @Around("@annotation(ru.ukhanov.t1.java.aop.annotation.Cached)")
    public Object cacheAdvice(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Calling method - {}", pjp.getSignature());
        String key = keyGenerator(pjp);
        Object cachedValue = cache.getIfPresent(key);

        if (cachedValue != null) {
            log.info("Received cachedValue - {}", cachedValue);
            return cachedValue;
        }
        Object result = pjp.proceed();
        if (result != null) {
            cache.put(key, result);
            log.info("Putted object - {} with key - {} in cache", result, key);
        } else {
            log.warn("Result is null, skipping cache for key = {}", key);
        }
        return result;
    }

    private String keyGenerator(ProceedingJoinPoint pjp) {
        return pjp.getSignature().toShortString() + ":" +
                java.util.Arrays.hashCode(pjp.getArgs());
    }
}
