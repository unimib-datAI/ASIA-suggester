package it.disco.unimib.suggester.translator;

import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;

import java.io.IOException;
import java.util.List;

public interface ITranslator {
    List<IDetectedLanguage> detect(List<String> textList) throws IOException;

    String translate(List<String> textList) throws IOException;

    String lookup(List<String> textList) throws IOException;
}
