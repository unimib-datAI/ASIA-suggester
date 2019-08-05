package it.disco.unimib.suggester;

import it.disco.unimib.suggester.configuration.ConfigProperties;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Objects;

@SpringBootApplication
@Log
public class SuggesterApplication {

    @Autowired(required = false)
    private ConfigProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(SuggesterApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        if (Objects.nonNull(properties))
            log.info("Azure subscription key for Translator Text: " + properties.getTranslator().getSubscriptionKey());
    }

}
