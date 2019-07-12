package it.disco.unimib.suggester;

import java.io.IOException;
import java.util.List;

public interface ITranslator {
    String Detect(List<String> textList) throws IOException;

    String Translate(List<String> textList) throws IOException;
}
