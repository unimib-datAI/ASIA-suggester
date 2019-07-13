package it.disco.unimib.suggester.translator.microsoftTranslate.domain;

import it.disco.unimib.suggester.translator.domain.IDetectedLanguageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectMessageBase implements IDetectedLanguageBase {
    private String language;
    private Double score;
    private Boolean isTranslationSupported;
    private Boolean isTransliterationSupported;

}