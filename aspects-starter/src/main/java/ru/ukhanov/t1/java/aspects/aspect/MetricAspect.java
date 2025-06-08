package ru.ukhanov.t1.java.aspects.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import ru.ukhanov.t1.java.aspects.entity.MetricsError;
import ru.ukhanov.t1.java.aspects.entity.TimeLimitExceedLog;
import ru.ukhanov.t1.java.aspects.repository.MetricsErrorRepository;
import ru.ukhanov.t1.java.aspects.repository.TimeLimitExceedLogRepository;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class MetricAspect {
    private final TimeLimitExceedLogRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MetricsErrorRepository errorRepository;

    @Value("${metric.time-limit-ms}")
    private long maxExecutionTime;
    @Value("${spring.kafka.metrics.topic:t1_demo_metrics}")
    private String topic;
    private final String errorType = "METRICS";

    @Around("@annotation(ru.ukhanov.t1.java.aspects.annotation.Metric)")
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
            String methodName = pjp.getSignature().getName();
            String message = String.format(
                    "Method %s exceeded time limit. Execution time: %d ms, limit: %d ms",
                    methodName, timeExecuted, maxExecutionTime
            );
            TimeLimitExceedLog limitLog = new TimeLimitExceedLog();
            limitLog.setMethodSignature(pjp.getSignature().getName());
            limitLog.setExecutionTime(timeExecuted);
            repository.save(limitLog);

            sendMessageToKafka(topic, errorType, message,
                    endTime, pjp.getSignature().toShortString());
        }
        return result;
    }

    private void sendMessageToKafka(String topic,String errorType, String message,
                                    long executionTime, String signature) {
        try {
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader("errorType", errorType)
                    .build();

            log.info("Message {} sent to topic {}", kafkaMessage, topic);
            kafkaTemplate.send(topic, kafkaMessage.toString()).get();
        } catch (Exception ex) {
            log.error("Error while sending message to topic - {} with exception - {}"
                    , topic, ex.getMessage());
            saveDataInDb(topic, executionTime, signature, message);
        }
    }

    private void saveDataInDb(String topic, long executionTime,
                              String methodSignature, String message) {
        try {
            MetricsError metricsError = new MetricsError();
            metricsError.setKafkaTopic(topic);
            metricsError.setTime_executed(executionTime);
            metricsError.setMethodSignature(methodSignature);
            metricsError.setMessage(message);

            errorRepository.save(metricsError);
            log.info("Saved new metricsError in db");
        } catch (Exception ex) {
            log.error("Failed to save data in db with method signature - {}. Error: {}",
                    methodSignature, ex.getMessage(), ex);
        }
    }
}
