package it.disco.unimib.suggester.translator.domain;

import java.util.List;

public interface ITranslation extends IDetectedLanguageBase {
    List<? extends ITranslationBase> getTranslations();
}
