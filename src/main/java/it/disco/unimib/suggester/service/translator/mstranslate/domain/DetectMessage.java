package it.disco.unimib.suggester.service.translator.mstranslate.domain;

import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import it.disco.unimib.suggester.model.translation.IDetectedLanguageBase;
import it.disco.unimib.suggester.model.translation.LanguageType;
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

    @Override
    public LanguageType getLanguageEnum() {
        return LanguageType.fromName(language);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DetectMessageBase implements IDetectedLanguageBase {
        private String language;
        private Double score;
        private Boolean isTranslationSupported;
        private Boolean isTransliterationSupported;

        @Override
        public LanguageType getLanguageEnum() {
            return LanguageType.fromName(language);
        }
    }

}


