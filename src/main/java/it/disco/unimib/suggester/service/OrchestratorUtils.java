package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.configuration.ConfigProperties;
import it.disco.unimib.suggester.model.table.*;
import it.disco.unimib.suggester.model.translation.ILookedupTerm;
import it.disco.unimib.suggester.model.translation.LanguageType;
import org.paukov.combinatorics3.Generator;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Component
public class OrchestratorUtils {

    private static ConfigProperties properties;

    public OrchestratorUtils(ConfigProperties properties) {
        OrchestratorUtils.properties = properties;
    }

    static String headerPreProcessing(String rawHeader) {

        String[] splitWords = rawHeader.replaceAll("[^a-zA-Z0-9]", " ")
                .trim()
                .replaceAll(" +", " ").split("(?=\\p{Lu})");
        return String.join(" ", splitWords).replaceAll(" +", " ").toLowerCase();

    }

    private static String cleanJoinCapitalize(String s) {

        return Stream.of(headerPreProcessing(s)
                .split("\\s"))
                .map(StringUtils::capitalize)
                .collect(joining(" "));
    }

    private static List<Pair<String, Pair<Double, Integer>>> combineLists(List<Pair<String, Pair<Double, Integer>>> list1,
                                                                          List<Pair<String, Pair<Double, Integer>>> list2) {

        return list1.stream().map(pair -> combine(pair, list2)).flatMap(List::stream).collect(toList());


    }

    private static List<Pair<String, Pair<Double, Integer>>> combine(Pair<String, Pair<Double, Integer>> p1,
                                                                     List<Pair<String, Pair<Double, Integer>>> list) {

        return list.stream().map(p -> Pair.of(cleanJoinCapitalize(p1.getFirst()) + " " + cleanJoinCapitalize(p.getFirst()),
                Pair.of(p1.getSecond().getFirst() + p.getSecond().getFirst(),
                        p1.getSecond().getSecond() + p.getSecond().getSecond())))
                .collect(toList());
    }

    static TableSchema setProcessedWords(TableSchema schema) {
        List<Column> columnList = schema.getColumnList();
        columnList.stream().map(Column::getHeader)
                .forEach(header -> header.setProcessedWord(headerPreProcessing(header.getOriginalWord())));
        return schema;
    }

    static void setTranslatedPhrases(Pair<List<ILookedupTerm>, Header> h) {


        Optional<List<Pair<String, Pair<Double, Integer>>>> reduced =
                h.getFirst().stream()
                        .map(lu -> lu.getTranslations()
                                .stream()
                                .map(t -> Pair.of(
                                        t.getTarget(),
                                        Pair.of(t.getConfidence(), 1)
                                        )
                                )
                                .collect(toList())
                        )
                        .peek(System.out::println)
                        .reduce(OrchestratorUtils::combineLists);


        Double translatePhraseThreshold = properties.getTranslator().getTranslatedPhrasesThreshold();
        reduced.ifPresent(r -> h.getSecond().setTranslatedPhrases(
                r.stream()
                        .map(p -> TranslatedWord.of(
                                asList(p.getFirst()),
                                p.getSecond().getFirst() / p.getSecond().getSecond(),
                                p.getFirst().split("\\s").length
                                )
                        )
                        .filter(translatedWord -> translatedWord.getConfidence() >= translatePhraseThreshold)
                        .sorted((p1, p2) -> p2.getConfidence().compareTo(p1.getConfidence()))
                        .collect(toList())));
        System.out.println(reduced);
    }


    static LanguageWithStats createLanguageWithStats(Map.Entry<LanguageType, Long> languageTypeEntry) {
        return new LanguageWithStats(languageTypeEntry.getKey(), (double) languageTypeEntry.getValue());
    }

    static void setTranslatedWords(Pair<List<ILookedupTerm>, Header> pair) {
        Double translatedWordThreshold = properties.getTranslator().getTranslatedWordThreshold();

        pair.getSecond()
                .setTranslatedWords(pair.getFirst()
                        .stream()
                        .flatMap(lu ->
                                lu.getTranslations().stream()
                                        .map(t -> TranslatedWord.of(
                                                asList(t.getTarget()),
                                                t.getConfidence(),
                                                t.getNumWords()
                                        ))
                                        .filter(translatedWord -> translatedWord.getConfidence() >= translatedWordThreshold)
                        )
                        .sorted(comparing(TranslatedWord::getConfidence).reversed())
                        .collect(toList()));
    }

    static List<List<String>> generatePhrasesCombinatoriallyFromTranslatedWord(TranslatedWord phrase) {
        return Generator.subset(phrase.getTranslatedWord().get(0).split("\\s"))
                .simple().stream()
                .filter(s -> !s.isEmpty())
                .map(l -> Generator.permutation(l).simple().stream())
                .flatMap(identity())
                .map(l -> l.stream().collect(toList()))
                .distinct()
                .collect(toList());
    }

    static Header generateAndSetPhrasesCombinatorially(Header header) {

        List<Pair<List<String>, Integer>> collect = header
                .getTranslatedPhrases()
                .stream()
                .map(OrchestratorUtils::generatePhrasesCombinatoriallyFromTranslatedWord)
                .flatMap(l -> l.stream())
                .distinct()
                .map(w -> Pair.of(w, w.size()))
                .collect(toList());


        Stream<TranslatedWord> stringStreamJoined = collect.stream()
                .map(pair -> TranslatedWord.of(
                        asList(pair.getFirst().stream().collect(joining()),
                                pair.getFirst().stream().collect(joining("_")),
                                pair.getFirst().stream().collect(joining("%3A")),
                                pair.getFirst().stream().collect(joining("%20"))

                        ).stream().distinct().collect(toList()),
                        Double.NaN,
                        pair.getSecond()
                        )
                );
        List<TranslatedWord> manipulatedTranlatedPhrases = stringStreamJoined
                .distinct()
                .sorted(comparing(TranslatedWord::getNumOfWords).reversed()).collect(toList());
        header.setManipulatedTranslatedPhrases(manipulatedTranlatedPhrases);
        return header;
    }

}