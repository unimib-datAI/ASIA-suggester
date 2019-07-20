package it.disco.unimib.suggester.translator;

import it.disco.unimib.suggester.model.LanguageType;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ITranslation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ITranslator {

    List<IDetectedLanguage> detect(List<String> textList);

    List<ITranslation> translate(List<String> textList, LanguageType destLang) throws IOException;

    Optional<List<ILookedupTerm>> lookup(List<String> textList, LanguageType sourceLang, LanguageType destLang);
}
