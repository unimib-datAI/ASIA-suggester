package it.disco.unimib.suggester.model.table;


import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.model.translation.LanguageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private String originalWord;
    private String processedWord;
    private List<String> splitTerms;
    private List<TranslatedWord> translatedPhrases;
    private List<TranslatedWord> translatedWords;
    private LanguageType language;
    private List<Suggestion> suggestions;
}
