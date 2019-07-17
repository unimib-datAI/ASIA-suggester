package it.disco.unimib.suggester.translator.domain;

import it.disco.unimib.suggester.model.LanguageType;

public interface IDetectedLanguageBase {
    String getLanguage();

    LanguageType getLanguageEnum() throws Exception;

    Double getScore();
}
