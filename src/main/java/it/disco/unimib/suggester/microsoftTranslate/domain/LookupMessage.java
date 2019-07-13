package it.disco.unimib.suggester.microsoftTranslate.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupMessage {
    private String normalizedSource;
    private String displaySource;
    private List<LookupTranslationMessage> translations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class LookupTranslationMessage {
        private String normalizedTarget;
        private String displayTarget;
        private String posTag;
        private String confidence;
        private String prefixWord;
        private List<LookupBackTranslations> backTranslations;

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
