package it.disco.unimib.suggester;

import it.disco.unimib.suggester.microsoftTranslate.Detect;
import it.disco.unimib.suggester.microsoftTranslate.Translate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import static it.disco.unimib.suggester.microsoftTranslate.Detect.prettify;

@SpringBootApplication
public class SuggesterApplication {

    @Autowired
    public Detect detectRequest;

    @Autowired
    public Translate translateRequest;


    public static void main(String[] args) {
        SpringApplication.run(SuggesterApplication.class, args);
    }


/*
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        try {

            String response = detectRequest.Post();
            System.out.println(prettify(response));
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            String response = translateRequest.Post();
            System.out.println(prettify(response));
        } catch (Exception e) {
            System.out.println(e);
        }

    }
*/

}
