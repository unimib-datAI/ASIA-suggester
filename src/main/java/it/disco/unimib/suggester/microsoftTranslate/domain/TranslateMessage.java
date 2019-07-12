package it.disco.unimib.suggester.microsoftTranslate.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateMessage {
    private DetectedLanguage detectedLanguage;
    private List<Translation> translations;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DetectedLanguage {
        private String language;
        private Double score;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Translation {
        private String text;
        private String to;

    }

}




