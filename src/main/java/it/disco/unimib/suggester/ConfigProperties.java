package it.disco.unimib.suggester;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "suggester")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigProperties {

    private Translator translator;
    private Summarizer summarizer;




}

