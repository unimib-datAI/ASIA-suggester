package it.disco.unimib.suggester;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Summarizer {
    @NotNull
    private String mainEndpoint;
    @NotNull
    private String suggestEndpoint;
}
