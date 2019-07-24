package it.disco.unimib.suggester.model.translation;

public interface IDetectedLanguageBase {
    String getLanguage();

    LanguageType getLanguageEnum();

    Double getScore();
}
