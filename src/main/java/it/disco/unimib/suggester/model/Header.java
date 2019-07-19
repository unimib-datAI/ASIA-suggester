package it.disco.unimib.suggester.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private String originalWord;
    private String processedWord;
    private List<String> splitTerms;
    private List<Pair<String, Double>> translatedWord;
    private LanguageType language;
}
