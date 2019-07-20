package it.disco.unimib.suggester;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "suggester")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigProperties {

    private String subscriptionKey;
    @NotNull
    private String mainEndpoint;
    @NotNull
    private String detectEndpoint;
    @NotNull
    private String translateEndpoint;
    @NotNull
    private String lookupEndpoint;


}
