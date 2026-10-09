package ar.edu.unju.fi.arquitecturas.tp2daas.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ComisionesProperties.class)
/**
 * Habilita las tareas programadas mediante la anotación @Scheduled
 * La clase solamente requiere las anotaciones anteriores.
 */
public class SchedulingConfig {

}
