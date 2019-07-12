package it.disco.unimib.suggester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuggesterApplication {




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
