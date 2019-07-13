package it.disco.unimib.suggester.translator.domain;

import java.util.List;

public interface IDetectedLanguage extends IDetectedLanguageBase {

    List<? extends IDetectedLanguageBase> getAlternatives();
}

