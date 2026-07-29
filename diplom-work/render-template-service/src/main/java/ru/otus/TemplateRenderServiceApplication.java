package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import ru.otus.hw.template_service.config.TemplateProperties;

@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties(TemplateProperties.class)
public class TemplateRenderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplateRenderServiceApplication.class, args);
	}
}