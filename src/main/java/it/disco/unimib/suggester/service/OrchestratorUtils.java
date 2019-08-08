package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.configuration.ConfigProperties;
import it.disco.unimib.suggester.model.table.*;
import it.disco.unimib.suggester.model.translation.ILookedupTerm;
import it.disco.unimib.suggester.model.translation.LanguageType;
import org.paukov.combinatorics3.Generator;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

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

    static Column setProcessedWords(Column column) {
        Header header = column.getHeader();
        header.setProcessedWord(headerPreProcessing(header.getOriginalWord()));
        return column;
    }

    static TableSchema setProcessedWords(TableSchema schema) {
        List<Column> columnList = schema.getColumnList();
        columnList.forEach(OrchestratorUtils::setProcessedWords);
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
                                Collections.singletonList(p.getFirst()),
                                p.getSecond().getFirst() / p.getSecond().getSecond(),
                                p.getFirst().split("\\s").length
                                )
                        )
                        .filter(translatedWord -> translatedWord.getConfidence() >= translatePhraseThreshold)
                        .sorted((p1, p2) -> p2.getConfidence().compareTo(p1.getConfidence()))
                        .collect(toList())));
        System.out.println(reduced);
    }


    private static LanguageWithStats createLanguageWithStats(Map.Entry<LanguageType, Long> languageTypeEntry) {
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
                                                Collections.singletonList(t.getTarget()),
                                                t.getConfidence(),
                                                t.getNumWords()
                                        ))
                                        .filter(translatedWord -> translatedWord.getConfidence() >= translatedWordThreshold)
                        )
                        .sorted(comparing(TranslatedWord::getConfidence).reversed())
                        .collect(toList()));
    }

    private static List<List<String>> generatePhrasesCombinatoriallyFromTranslatedWord(TranslatedWord phrase) {
        return Generator.subset(phrase.getTranslatedWord().get(0).split("\\s"))
                .simple().stream()
                .filter(s -> !s.isEmpty())
                .map(l -> Generator.permutation(l).simple().stream())
                .flatMap(identity())
                .map(ArrayList::new)
                .distinct()
                .collect(toList());
    }

    static void generateAndSetPhrasesCombinatorially(Header header) {

        double max = header
                .getTranslatedPhrases()
                .stream()
                .mapToDouble(TranslatedWord::getConfidence)
                .max().orElse(.0);
        double threshold = max - ((max / 3) * 100);

        List<Pair<List<String>, Integer>> collect = header
                .getTranslatedPhrases()
                .stream()
                .filter(translatedWord -> translatedWord.getConfidence() > threshold)
                .map(OrchestratorUtils::generatePhrasesCombinatoriallyFromTranslatedWord)
                .flatMap(Collection::stream)
                .distinct()
                .map(w -> Pair.of(w, w.size()))
                .limit(20)
                .collect(toList());


        Stream<TranslatedWord> stringStreamJoined = collect.stream()
                .map(pair -> TranslatedWord.of(
                        joiningVariousWays(pair.getFirst()).stream().distinct().collect(toList()),
                        Double.NaN,
                        pair.getSecond()
                        )
                );
        List<TranslatedWord> manipulatedTranslatedPhrases = stringStreamJoined
                .distinct()
                .sorted(comparing(TranslatedWord::getNumOfWords).reversed()).collect(toList());
        header.setManipulatedTranslatedPhrases(manipulatedTranslatedPhrases);
    }

    static List<String> joiningVariousWays(List<String> stringList) {

        return asList(String.join("", stringList),
                String.join("_", stringList),
                String.join("%3A", stringList),
                String.join("%20", stringList));
    }


    static Column setSplitTerms(Column column) {
        Header header = column.getHeader();
        header.setSplitTerms(asList(header.getProcessedWord().split("\\s")));
        return column;
    }

    static TableSchema setSplitTerms(TableSchema schema) {

        schema.getColumnList().forEach(OrchestratorUtils::setSplitTerms);
//                map(Column::getHeader)
//                .forEach(header -> header.setSplitTerms(asList(header.getProcessedWord().split("\\s"))));
        return schema;
    }


    static void setSameLanguageAllColumns(TableSchema schema) {
        schema.getColumnList()
                .stream()
                .map(Column::getHeader)
                .forEach(header -> header.setLanguage(schema.getLanguage()));
    }

    static void calculateAndSetLanguageWithStatistics(TableSchema schema) {
        schema.setLanguageWithStatsList(
                schema.getColumnList().stream()
                        .map(column -> column.getHeader().getLanguage())
                        .collect(groupingBy(identity(), counting()))
                        .entrySet().stream()
                        .map(OrchestratorUtils::createLanguageWithStats)
                        .sorted(comparing(LanguageWithStats::getFrequency).reversed())
                        .collect(toList())
        );
        schema.setLanguage(schema.getLanguageWithStatsList().get(0).getLanguageType());
    }
}
