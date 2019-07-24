package it.disco.unimib.suggester.service.translator;

import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import it.disco.unimib.suggester.model.translation.ILookedupTerm;
import it.disco.unimib.suggester.model.translation.ITranslation;
import it.disco.unimib.suggester.model.translation.LanguageType;

import java.io.IOException;
import java.util.List;

public interface ITranslator {


    void setTest(boolean test);

    List<IDetectedLanguage> detect(List<String> textList);

    List<ITranslation> translate(List<String> textList, LanguageType destLang) throws IOException;

    List<ILookedupTerm> lookup(List<String> textList, LanguageType sourceLang, LanguageType destLang);
}
