package it.disco.unimib.suggester.model.translation;

import java.util.List;

public interface IDetectedLanguage extends IDetectedLanguageBase {

    List<? extends IDetectedLanguageBase> getAlternatives();
}

