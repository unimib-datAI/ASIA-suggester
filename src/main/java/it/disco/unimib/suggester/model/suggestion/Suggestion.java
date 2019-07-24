package it.disco.unimib.suggester.model.suggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {
    private String prefix;
    private String suggestion;
    private String namespace;
    private Long occurrence;
    private String dataset;

}
