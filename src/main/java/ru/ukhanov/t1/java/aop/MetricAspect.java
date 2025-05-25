package ru.ukhanov.t1.java.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ukhanov.t1.java.model.timeLimitExceedLog.TimeLimitExceedLog;
import ru.ukhanov.t1.java.repository.TimeLimitExceedLogRepository;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class MetricAspect {
    private final TimeLimitExceedLogRepository repository;

    @Value("${metric.time-limit-ms}")
    private long maxExecutionTime;

    @Around("@annotation(ru.ukhanov.t1.java.aop.annotation.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Calling method - {}", pjp.getSignature());
        Object result = null;
        long startTime = System.currentTimeMillis();
        log.info("Method - {} start time - {}", pjp.getSignature(), startTime);
        try {
           result = pjp.proceed();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info("End time - {}", endTime);
        long timeExecuted = endTime - startTime;
        log.info("Execution time - {}", timeExecuted);

        if (timeExecuted > maxExecutionTime) {
            TimeLimitExceedLog limitLog = new TimeLimitExceedLog();
            limitLog.setMethodSignature(pjp.getSignature().getName());
            limitLog.setExecutionTime(timeExecuted);
            repository.save(limitLog);
        }
        return result;
    }
}
