package it.disco.unimib.suggester;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Translator {

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
