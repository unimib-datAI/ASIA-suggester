package it.disco.unimib.suggester.service;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;
import it.disco.unimib.suggester.model.LanguageType;
import it.disco.unimib.suggester.translator.ITranslator;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;


@Service
public class Orchestrator {


    private ITranslator translator;

    @Autowired
    public Orchestrator(ITranslator translator) {
        this.translator = translator;
    }


    public static String headerPreprocessing(String rawHeader) {


        return rawHeader.replaceAll("[^a-zA-Z0-9]", " ")
                .trim()
                .replaceAll(" +", " ");

    }

    public static void main(String[] args) {
        String rowHeader = "yo-dude: like, ... []{}this?is_a string";
        System.out.println(headerPreprocessing(rowHeader));

    }

    public java.util.List<IDetectedLanguage> detectLanguage(String processedString) throws IOException {
        return translator.detect(List.of(processedString));

    }


    public String translation(String processedString) {

        String[] words = Strings.split(processedString, ' ');
        if (words.length == 1) {
            //only lookup

        } else {
            //combinatorial lookup + translation

        }
        return null;
    }

    public String translate(String processedString) throws IOException {
        translator.translate(List.of(processedString), LanguageType.EN);

        return null;
    }


    public java.util.List<Pair<String, Double>> lookup(String processedString) throws IOException {
        java.util.List<ILookedupTerm> list = translator.lookup(singletonList(processedString), LanguageType.IT, LanguageType.EN);

        return list.get(0).getTranslations().stream()
                .map(t -> new Pair<String, Double>(
                        t.getTarget(),
                        t.getConfidence())).collect(Collectors.toList());

    }

}
