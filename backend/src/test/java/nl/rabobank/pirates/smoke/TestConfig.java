package nl.rabobank.pirates.smoke;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"nl.rabobank.pirates.service", "nl.rabobank.pirates.client", "nl.rabobank.pirates.model"})
public class TestConfig {
}
