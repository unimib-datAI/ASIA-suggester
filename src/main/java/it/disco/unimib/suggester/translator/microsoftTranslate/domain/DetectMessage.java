package it.disco.unimib.suggester.translator.microsoftTranslate.domain;

import it.disco.unimib.suggester.translator.domain.IDetectedLanguageBase;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectMessage implements IDetectedLanguage {
    private String language;
    private Double score;
    private Boolean isTranslationSupported;
    private Boolean isTransliterationSupported;
    private List<DetectMessageBase> alternatives;

    @Override
    public List<? extends IDetectedLanguageBase> getAlternatives() {
        return alternatives;
    }


}


