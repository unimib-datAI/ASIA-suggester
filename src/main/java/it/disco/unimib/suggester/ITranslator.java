package it.disco.unimib.suggester;

import java.io.IOException;
import java.util.List;

public interface ITranslator {
    String detect(List<String> textList) throws IOException;

    String translate(List<String> textList) throws IOException;

    String lookup(List<String> textList) throws IOException;
}
