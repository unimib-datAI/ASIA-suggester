package it.disco.unimib.suggester.translator;

import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ITranslation;

import java.io.IOException;
import java.util.List;

public interface ITranslator {
    List<IDetectedLanguage> detect(List<String> textList) throws IOException;

    List<ITranslation> translate(List<String> textList) throws IOException;

    List<ILookedupTerm> lookup(List<String> textList) throws IOException;
}
