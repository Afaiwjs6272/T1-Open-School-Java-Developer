package ru.ukhanov.t1.java.aspects.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import ru.ukhanov.t1.java.aspects.aspect.LogAspect;
import ru.ukhanov.t1.java.aspects.aspect.MetricAspect;
import ru.ukhanov.t1.java.aspects.repository.DataSourceErrorLogRepository;
import ru.ukhanov.t1.java.aspects.repository.MetricsErrorRepository;
import ru.ukhanov.t1.java.aspects.repository.TimeLimitExceedLogRepository;

@AutoConfiguration
@Import({ru.ukhanov.t1.java.aspects.config.KafkaConfig.class, ru.ukhanov.t1.java.aspects.config.DbConfig.class})
public class AspectsAutoConfiguration {

    @Bean
    @ConditionalOnBean({DataSourceErrorLogRepository.class, KafkaTemplate.class})
    public LogAspect logAspect(
            DataSourceErrorLogRepository repository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        return new LogAspect(repository, kafkaTemplate);
    }

    @Bean
    @ConditionalOnBean({MetricsErrorRepository.class, KafkaTemplate.class, TimeLimitExceedLogRepository.class})
    public MetricAspect metricAspect(
            TimeLimitExceedLogRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            MetricsErrorRepository errorRepository
    ) {
        return new MetricAspect(repository, kafkaTemplate, errorRepository);
    }
}
