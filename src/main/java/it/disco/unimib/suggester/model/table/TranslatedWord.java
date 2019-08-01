package it.disco.unimib.suggester.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslatedWord {
    private List<String> translatedWord;
    private Double confidence;
    private Integer numOfWords;

    public static TranslatedWord of(List<String> translatedWord, Double confidence, Integer numOfWords) {
        return new TranslatedWord(translatedWord, confidence, numOfWords);
    }
}
