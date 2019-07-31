package it.disco.unimib.suggester.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslatedWord {
    private String translatedWord;
    private Double confidence;
    private Integer numOfWords;

    public static TranslatedWord of(String translatedWord, Double confidence, Integer numOfWords) {
        return new TranslatedWord(translatedWord, confidence, numOfWords);
    }
}
