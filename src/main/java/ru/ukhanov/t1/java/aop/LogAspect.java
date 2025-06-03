package ru.ukhanov.t1.java.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.ukhanov.t1.java.model.dataSourceErrorLog.DataSourceErrorLog;
import ru.ukhanov.t1.java.repository.DataSourceErrorLogRepository;
import java.util.Arrays;

@RequiredArgsConstructor
@Aspect
@Slf4j
@Component
public class LogAspect {
    private final DataSourceErrorLogRepository repository;

    @Around("@annotation(ru.ukhanov.t1.java.aop.annotation.LogDataSourceError)")
    public Object logError(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Trying to catch CRUD exceptions");
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            log.info("Caught the exception {}", ex.getClass());
            log.info("ASPECT AROUND ANNOTATION: Call method: {}", pjp.getSignature().getName());
            DataSourceErrorLog log = new DataSourceErrorLog();
            log.setStackTrace(Arrays.toString(ex.getStackTrace()));
            log.setMessage(ex.getMessage());
            log.setMethodSignature(pjp.getSignature().toShortString());
            repository.save(log);
            throw ex;
        }
    }
}
