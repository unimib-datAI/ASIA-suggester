package it.disco.unimib.suggester.microsoftTranslate.configuration;


import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConf {

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }
}
