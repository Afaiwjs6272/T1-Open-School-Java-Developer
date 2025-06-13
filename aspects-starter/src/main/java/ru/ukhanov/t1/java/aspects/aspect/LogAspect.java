package ru.ukhanov.t1.java.aspects.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.ukhanov.t1.java.aspects.entity.DataSourceErrorLog;
import ru.ukhanov.t1.java.aspects.repository.DataSourceErrorLogRepository;

import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
@Aspect
public class LogAspect {
    private final DataSourceErrorLogRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.metrics.topic:t1_demo_metrics}")
    private String topic;
    private final String errorType = "DATA_SOURCE";


    @Around("@annotation(ru.ukhanov.t1.java.aspects.annotation.LogDataSourceError)")
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

    @Around("@annotation(ru.ukhanov.t1.java.aspects.annotation.LodDatasourceError)")
    public Object errorSender(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        try {
            result = pjp.proceed();

            String methodName = pjp.getSignature().toShortString();
            String message = String.format("Data source method accessed: %s", methodName);
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader("errorType", errorType)
                    .build();
            log.info("Message - {} sent to topic - {}", message, topic);
            kafkaTemplate.send(topic, kafkaMessage.toString()).get();
        } catch (Exception ex) {
            log.error("Error while running method or sending message to kafka");
            log.info("ASPECT AROUND ANNOTATION: Call method: {}", pjp.getSignature().getName());
            DataSourceErrorLog errorLog = new DataSourceErrorLog();
            errorLog.setStackTrace(Arrays.toString(ex.getStackTrace()));
            errorLog.setMessage(ex.getMessage());
            errorLog.setMethodSignature(pjp.getSignature().getName());
            log.info("Error info saved to db");
            repository.save(errorLog);
        }
        return result;
    }
}