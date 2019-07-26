package it.disco.unimib.suggester.service;


import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.model.table.*;
import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import it.disco.unimib.suggester.model.translation.ILookedupTerm;
import it.disco.unimib.suggester.model.translation.LanguageType;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.translator.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static it.disco.unimib.suggester.model.translation.LanguageType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;


@Service
public class Orchestrator {


    private ITranslator translator;
    private ISuggester suggester;

    @Autowired
    public Orchestrator(ITranslator translator, ISuggester suggester) {
        this.translator = translator;
        this.suggester = suggester;
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

    private static LanguageWithStats createLanguageWithStats(Map.Entry<LanguageType, Long> languageTypeLongEntry) {
        return new LanguageWithStats(languageTypeLongEntry.getKey(), (double) languageTypeLongEntry.getValue());
    }

    private static void setTranslatedWords(Pair<List<ILookedupTerm>, Header> pair) {
        pair.getSecond()
                .setTranslatedWords(pair.getFirst()
                        .stream()
                        .flatMap(lu -> lu.getTranslations()
                                .stream()
                                .map(t -> TranslatedWord.of(t.getTarget(), t.getConfidence())))
                        .sorted(comparing(TranslatedWord::getConfidence).reversed())
                        .collect(toList()));
    }


    private TableSchema detectLanguagesForTableSchema(TableSchema schema) {

        if (schema.getLanguage() == UNKNOWN) {
            schema.getColumnList().stream().forEach(this::detectLanguageForColumn);

            schema.setLanguageWithStatsList(
                    schema.getColumnList().stream()
                            .map(column -> column.getHeader().getLanguage())
                            .collect(groupingBy(identity(), counting()))
                            .entrySet().stream()
                            .map(Orchestrator::createLanguageWithStats)
                            .sorted(comparing(LanguageWithStats::getFrequency).reversed())
                            .collect(toList())
            );
            schema.setLanguage(schema.getLanguageWithStatsList().get(0).getLanguageType());
            if (schema.isForceSingleLanguage())
                schema.getColumnList()
                        .stream()
                        .map(column -> column.getHeader())
                        .forEach(header -> header.setLanguage(schema.getLanguage()));
        }
        return schema;
    }


    private Column detectLanguageForColumn(Column column) {
        Header header = column.getHeader();
        if (!checkSupportedLanguage(header.getLanguage()))
            header.setLanguage(translator.detect(asList(header.getProcessedWord())).get(0).getLanguageEnum());
        return column;
    }


    private Header setTranslatedWords(Header header) {
        LanguageType lang = header.getLanguage();
        if (!checkSupportedLanguage(lang))
            header.setLanguage(translator.detect(asList(header.getOriginalWord())).get(0).getLanguageEnum());
        return header;
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
        translator.translate(singletonList(processedString), EN);

        return null;
    }


    List<TranslatedWord> lookup(String processedString) {
        return translator
                .lookup(singletonList(processedString), IT, EN)
                .get(0).getTranslations()
                .stream()
                .map(t -> TranslatedWord.of(
                        t.getTarget(),
                        t.getConfidence()))
                .collect(toList());

    }

    private TableSchema setSplitTerms(TableSchema schema) {

        schema.getColumnList().stream().map(Column::getHeader)
                .forEach(header -> header.setSplitTerms(asList(header.getProcessedWord().split("\\s"))));
        return schema;
    }

    private TableSchema setTranslatedWords(TableSchema schema) {

        if (schema.getLanguage() != EN) {
            List<Pair<List<ILookedupTerm>, Header>> pairList = schema.getColumnList()
                    .stream()
                    .map(Column::getHeader)
                    .map(header -> Pair.of(header.getSplitTerms(), header))
                    .map(p -> Pair.of(translator.lookup(p.getFirst(), p.getSecond().getLanguage(), EN), p.getSecond())).collect(toList());


            pairList.forEach(Orchestrator::setTranslatedWords);

            pairList
                    .forEach(h -> {
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
                                        .reduce(this::combineLists);


                        reduced.ifPresent(r -> h.getSecond()
                                .setTranslatedPhrases(r.stream()
                                        .map(p -> TranslatedWord.of(p.getFirst(), p.getSecond().getFirst() / p.getSecond().getSecond()))
                                        .sorted((p1, p2) -> p2.getConfidence().compareTo(p1.getConfidence()))
                                        .collect(toList())));
                        System.out.println(reduced);

                    });
        }
        return schema;
    }

    private List<Pair<String, Pair<Double, Integer>>> combineLists(List<Pair<String, Pair<Double, Integer>>> list1,
                                                                   List<Pair<String, Pair<Double, Integer>>> list2) {

        return list1.stream().map(pair -> combine(pair, list2)).flatMap(List::stream).collect(toList());


    }


    private List<Pair<String, Pair<Double, Integer>>> combine(Pair<String, Pair<Double, Integer>> p1,
                                                              List<Pair<String, Pair<Double, Integer>>> list) {

        return list.stream().map(p -> Pair.of(cleanJoinCapitalize(p1.getFirst()) + " " + cleanJoinCapitalize(p.getFirst()),
                Pair.of(p1.getSecond().getFirst() + p.getSecond().getFirst(),
                        p1.getSecond().getSecond() + p.getSecond().getSecond())))
                .collect(toList());
    }

    private String cleanJoinCapitalize(String s) {

        return Stream.of(headerPreprocessing(s)
                .split("\\s")).map(StringUtils::capitalize)
                .collect(joining(" "));
    }

    public TableSchema translateTableSchema(TableSchema schema) {

        return setTranslatedWords(setSplitTerms(detectLanguagesForTableSchema(setProcessedWord(schema))));


    }

    public Column translateColumn(Column column) {
        return detectLanguageForColumn(column);
    }


    public List<String> getAvailableSummaries() {
        return suggester.getSummaries();
    }

    public TableSchema translateAndSuggest(TableSchema schema, List<String> preferredSummaries) {
        translateTableSchema(schema);
        suggestPredicates(schema, preferredSummaries);
        return schema;
    }

    private void suggestPredicates(TableSchema schema, List<String> preferredSummaries) {
        schema.getColumnList().stream().forEach(column -> updateColumnWithSuggestedPredicate(column, preferredSummaries));
    }

    private void updateColumnWithSuggestedPredicate(Column column, List<String> preferredSummaries) {

        List<String> words = column.getHeader().getTranslatedPhrases().stream().map(TranslatedWord::getTranslatedWord).collect(toList());
        suggester.setPreferredSummaries(preferredSummaries);
        List<Suggestion> suggestions = suggester.propertySuggestionsMultipleKeywords(words, true);
        suggester.setPreferredSummaries(null);
        column.getHeader().setSuggestions(suggestions);

    }
}
