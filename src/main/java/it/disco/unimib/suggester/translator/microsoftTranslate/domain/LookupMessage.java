package it.disco.unimib.suggester.translator.microsoftTranslate.domain;


import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ILookedupTermTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupMessage implements ILookedupTerm {
    private String normalizedSource;
    private String displaySource;
    private List<LookupTranslationMessage> translations;

    @Override
    public String getSource() {
        return normalizedSource;
    }

    @Override
    public List<? extends ILookedupTermTarget> getTranslations() {
        return translations;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class LookupTranslationMessage implements ILookedupTermTarget {
        private String normalizedTarget;
        private String displayTarget;
        private String posTag;
        private Double confidence;
        private String prefixWord;
        private List<LookupBackTranslations> backTranslations;

        @Override
        public String getTarget() {
            return normalizedTarget;
        }

        @Override
        public Double getConfidence() {
            return confidence;
        }

        @Override
        public String getType() {
            return posTag;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class LookupBackTranslations {
        private String normalizedText;
        private String displayText;
        private Integer numExamples;
        private Integer frequencyCount;

    }

}
