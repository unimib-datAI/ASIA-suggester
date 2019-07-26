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

    public static TranslatedWord of(String translatedWord, Double confidence) {
        return new TranslatedWord(translatedWord, confidence);
    }
}
