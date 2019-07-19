package it.disco.unimib.suggester.service;


import it.disco.unimib.suggester.model.Column;
import it.disco.unimib.suggester.model.Header;
import it.disco.unimib.suggester.model.LanguageType;
import it.disco.unimib.suggester.model.TableSchema;
import it.disco.unimib.suggester.translator.ITranslator;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;


@Service
public class Orchestrator {


    private ITranslator translator;

    @Autowired
    public Orchestrator(ITranslator translator) {
        this.translator = translator;
    }


    static String headerPreprocessing(String rawHeader) {

        String[] splitWords = rawHeader.replaceAll("[^a-zA-Z0-9]", " ")
                .trim()
                .replaceAll(" +", " ").split("(?=\\p{Lu})");
        return String.join(" ", splitWords).replaceAll(" +", " ").toLowerCase();

    }

    public static void main(String[] args) {
        String rowHeader = "yo-dude: like, ... []{}this?is_a string";
        System.out.println(headerPreprocessing(rowHeader));

    }


    private TableSchema detectLanguage(TableSchema schema) {
        List<IDetectedLanguage> detectedLanguages =
                translator.detect(
                        schema
                                .getColumnList().stream()
                                .map(column -> column.getHeader()
                                        .getOriginalWord())
                                .collect(Collectors.toList())
                );

        List<Column> columnList = schema.getColumnList();

        IntStream.range(0, detectedLanguages.size())
                .forEach(i -> columnList.get(i).getHeader().setLanguage(detectedLanguages.get(i).getLanguageEnum()));
        return schema;
    }


    private TableSchema setProcessedWord(TableSchema schema) {
        List<Column> columnList = schema.getColumnList();
        columnList.stream().map(Column::getHeader)
                .forEach(header -> header.setProcessedWord(headerPreprocessing(header.getOriginalWord())));
        return schema;
    }


    List<IDetectedLanguage> detectLanguage(String processedString) {
        return translator.detect(singletonList(processedString));

    }


    public String translate(String processedString) throws IOException {
        translator.translate(singletonList(processedString), LanguageType.EN);

        return null;
    }


    java.util.List<Pair<String, Double>> lookup(String processedString) {
        Optional<List<Pair<String, Double>>> optList = translator.lookup(singletonList(processedString), LanguageType.IT, LanguageType.EN)
                .map(l -> l.get(0).getTranslations().stream()
                        .map(t -> Pair.of(
                                t.getTarget(),
                                t.getConfidence()))
                        .collect(Collectors.toList()));

        return optList.orElseGet((Supplier<? extends List<Pair<String, Double>>>) new ArrayList<Pair<String, Double>>());

    }

    private TableSchema setSplitTerms(TableSchema schema) {

        schema.getColumnList().stream().map(Column::getHeader)
                .forEach(header -> header.setSplitTerms(Arrays.asList(header.getProcessedWord().split("\\s"))));
        return schema;
    }

    private TableSchema setTranslatedWords(TableSchema schema) {
        Stream<Pair<Optional<List<ILookedupTerm>>, Header>> optionalStream = schema.getColumnList()
                .stream().map(Column::getHeader)
                .map(header -> Pair.of(header.getSplitTerms(), header)) // Pair List<String, LanguageType> per header
                .map(p -> Pair.of(translator.lookup(p.getFirst(), p.getSecond().getLanguage(), LanguageType.EN), p.getSecond()));


        optionalStream
                .forEach(h -> h.getFirst()
                        .ifPresent(l -> {
                            Optional<List<Pair<String, Pair<Double, Integer>>>> reduced =
                                    l.stream()
                                            .map(lu -> lu.getTranslations().stream()
                                                    .map(t -> Pair.of(t.getTarget(),
                                                            Pair.of(t.getConfidence(), 1)))
                                                    .collect(Collectors.toList()))
                                            .peek(System.out::println)
                                            .reduce(this::combineLists);
                            reduced.ifPresent(r -> h.getSecond().setTranslatedWord(r.stream()
                                    .map(p -> Pair.of(p.getFirst(), p.getSecond().getFirst() / p.getSecond().getSecond()))
                                    .sorted((p1, p2) -> p2.getSecond().compareTo(p1.getSecond())).collect(Collectors.toList())));
                            System.out.println(reduced);
                        }));
        return schema;
    }

    private List<Pair<String, Pair<Double, Integer>>> combineLists(List<Pair<String, Pair<Double, Integer>>> list1,
                                                                   List<Pair<String, Pair<Double, Integer>>> list2) {

        return list1.stream().map(pair -> combine(pair, list2)).flatMap(List::stream).collect(Collectors.toList());


    }


    private List<Pair<String, Pair<Double, Integer>>> combine(Pair<String, Pair<Double, Integer>> p1, List<Pair<String, Pair<Double, Integer>>> list) {

        return list.stream().map(p -> Pair.of(cleanJoinCapitalize(p1.getFirst()) + cleanJoinCapitalize(p.getFirst()),
                Pair.of(p1.getSecond().getFirst() + p.getSecond().getFirst(),
                        p1.getSecond().getSecond() + p.getSecond().getSecond())))
                .collect(Collectors.toList());
    }

    private String cleanJoinCapitalize(String s) {

        return Stream.of(headerPreprocessing(s)
                .split("\\s")).map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }

    public TableSchema translateSchema(TableSchema schema) {

        return setTranslatedWords(setSplitTerms(setProcessedWord(detectLanguage(schema))));
    }
}
