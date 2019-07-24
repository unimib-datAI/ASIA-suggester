package it.disco.unimib.suggester.model.translation;

import java.util.List;

public interface ITranslation extends IDetectedLanguageBase {
    List<? extends ITranslationBase> getTranslations();
}
