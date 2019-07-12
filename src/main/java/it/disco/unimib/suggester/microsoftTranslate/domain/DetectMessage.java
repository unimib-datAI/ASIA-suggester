package it.disco.unimib.suggester.microsoftTranslate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectMessage {
    private String language;
    private Double score;
    private Boolean isTranslationSupported;
    private Boolean isTransliterationSupported;
    private List<DetectMessageBase> alternatives;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DetectMessageBase {
        private String language;
        private Double score;
        private Boolean isTranslationSupported;
        private Boolean isTransliterationSupported;
    }

}


