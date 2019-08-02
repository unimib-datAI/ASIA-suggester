package it.disco.unimib.suggester.service;


import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.Header;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.model.table.TranslatedWord;
import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import it.disco.unimib.suggester.model.translation.ILookedupTerm;
import it.disco.unimib.suggester.model.translation.LanguageType;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.translator.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static it.disco.unimib.suggester.model.translation.LanguageType.*;
import static it.disco.unimib.suggester.service.OrchestratorUtils.*;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.toList;


@Service
public class Orchestrator {


    private static final long LIMIT_SUGGESTIONS = 10;
    private final Comparator<Suggestion> comparator1 =
            comparing(Suggestion::getPositionDataset)
                    .thenComparing(Suggestion::getCalculatedIndex)
                    .thenComparing(new Suggestion.SuggestionComparatorByDistanceVector())
                    .thenComparing(Suggestion::getSearchedKeywordLength, reverseOrder())
                    .thenComparing(Suggestion::getRatioIndex, reverseOrder())
                    .thenComparing(Suggestion::getOccurrence, reverseOrder());


    private final ITranslator translator;
    private final ISuggester suggester;

    @Autowired
    public Orchestrator(ITranslator translator, ISuggester suggester) {
        this.translator = translator;
        this.suggester = suggester;
    }


    public Column translateAndSuggest(Column column, List<String> preferredSummaries) {

        translateColumn(column);
        suggestPredicates(column, preferredSummaries);
        suggestSubjects(column, preferredSummaries);
        suggestObjects(column, preferredSummaries);
        return column;
    }

    public List<String> getAvailableSummaries() {
        return suggester.getSummaries();
    }

    public TableSchema translateAndSuggest(TableSchema schema, List<String> preferredSummaries) {
        translateTableSchema(schema);
        suggestPredicates(schema, preferredSummaries);
        suggestSubjects(schema, preferredSummaries);
        suggestObjects(schema, preferredSummaries);
        return schema;
    }

    public TableSchema translateTableSchema(TableSchema schema) {
        return setTranslatedWords(
                setSplitTerms(
                        detectLanguagesForTableSchema(
                                setProcessedWords(schema))));
    }

    private void translateColumn(Column column) {
        setTranslatedWordsColumn(
                setSplitTerms(
                        detectLanguageForColumn(
                                setProcessedWords(column))));
    }

    private void suggestObjects(Column column, List<String> preferredSummaries) {
        updateColumnWithSuggestions(
                column,
                preferredSummaries,
                suggester::objectSuggestionsMultipleKeywords,
                column.getHeader()::setObjectSuggestions);
    }

    private void suggestObjects(TableSchema schema, List<String> preferredSummaries) {
        schema.getColumnList()
                .forEach(column -> suggestObjects(column, preferredSummaries));
    }


    private void suggestSubjects(Column column, List<String> preferredSummaries) {
        updateColumnWithSuggestions(
                column,
                preferredSummaries,
                suggester::typeSuggestionsMultipleKeywords,
                column.getHeader()::setSubjectSuggestions
        );
    }

    private void suggestSubjects(TableSchema schema, List<String> preferredSummaries) {
        schema.getColumnList()
                .forEach(column -> suggestSubjects(column, preferredSummaries));
    }


    private Column detectLanguageForColumn(Column column) {
        Header header = column.getHeader();
        if (!checkSupportedLanguage(header.getLanguage()))
            header.setLanguage(translator.detect(singletonList(header.getProcessedWord())).get(0).getLanguageEnum());
        return column;
    }

    private void setTranslatedWordsColumn(Column column) {


        Header header = column.getHeader();
        if (header.getLanguage() != EN) {
            Pair<List<ILookedupTerm>, Header> pair =
                    Pair.of(
                            translator.lookup(header.getSplitTerms(), header.getLanguage(), EN),
                            header);

            OrchestratorUtils.setTranslatedWords(pair);
            OrchestratorUtils.setTranslatedPhrases(pair);
            OrchestratorUtils.generateAndSetPhrasesCombinatorially(header);
        } else {
            List<TranslatedWord> translatedWords = singletonList(TranslatedWord.of(
                    joiningVariousWays(header.getSplitTerms()),
                    Double.NaN,
                    header.getSplitTerms().size()));
            header.setManipulatedTranslatedPhrases(translatedWords);
        }
    }


    private TableSchema setTranslatedWords(TableSchema schema) {

        if (schema.getLanguage() != EN) {

            schema.getColumnList().forEach(this::setTranslatedWordsColumn);

        } else {

            schema.getColumnList().stream()
                    .map(Column::getHeader).
                    map(header -> Pair.of(header.getSplitTerms(), header))
                    .map(pair ->
                            Pair.of(singletonList(TranslatedWord.of(joiningVariousWays(pair.getFirst()),
                                    Double.NaN,
                                    pair.getFirst().size())),
                                    pair.getSecond()))
                    .forEach(p -> p.getSecond().setManipulatedTranslatedPhrases(p.getFirst()));

        }
        return schema;
    }


    private void suggestPredicates(Column column, List<String> preferredSummaries) {
        updateColumnWithSuggestions(
                column,
                preferredSummaries,
                suggester::propertySuggestionsMultipleKeywords,
                column.getHeader()::setPropertySuggestions);
    }


    private void suggestPredicates(TableSchema schema, List<String> preferredSummaries) {
        schema.getColumnList()
                .forEach(column -> suggestPredicates(column, preferredSummaries));
    }


    private void updateColumnWithSuggestions(Column column,
                                             List<String> preferredSummaries,
                                             Function<List<String>, List<List<Suggestion>>> suggestMethod,
                                             Consumer<List<Suggestion>> suggestionsConsumer) {

        List<String> keywords = column.getHeader()
                .getManipulatedTranslatedPhrases()
                .stream()
                .map(TranslatedWord::getTranslatedWord)
                .flatMap(Collection::stream)
                .collect(toList());

        suggester.setPreferredSummaries(preferredSummaries);
        List<Suggestion> suggestions =
                suggestMethod.apply(keywords)
//                suggester
//                .propertySuggestionsMultipleKeywords(keywords)
                        .stream()
                        //  .filter(suggestion -> !isEmpty(suggestion))
                        .map(suggestions1 -> suggestions1.stream().limit(LIMIT_SUGGESTIONS).collect(toList()))
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(groupingByConcurrent(Suggestion::getSuggestion))
                        .values().stream()
                        .map(suggestionList -> suggestionList
                                .stream().min(comparator1)
                                .orElse(null))
                        //.filter(suggestion -> !isEmpty(suggestion))
                        .sorted(comparator1)
                        .limit(LIMIT_SUGGESTIONS)
                        .collect(toList());


        suggester.setPreferredSummaries(null);
        suggestionsConsumer.accept(suggestions);
        //       column.getHeader().setPropertySuggestions(suggestions);

    }


    private TableSchema detectLanguagesForTableSchema(TableSchema schema) {

        if (schema.getLanguage() == UNKNOWN) {
            schema.getColumnList().forEach(this::detectLanguageForColumn);

            calculateAndSetLanguageWithStatistics(schema);

            if (schema.isForceSingleLanguage()) setSameLanguageAllColumns(schema);
        } else setSameLanguageAllColumns(schema);
        return schema;
    }

    List<IDetectedLanguage> detectLanguage(String processedString) {
        return translator.detect(singletonList(processedString));

    }

    List<TranslatedWord> lookup(String processedString, LanguageType languageType) {
        return translator
                .lookup(singletonList(processedString), languageType, EN)
                .get(0).getTranslations()
                .stream()
                .map(t -> TranslatedWord.of(
                        singletonList(t.getTarget()),
                        t.getConfidence(),
                        t.getNumWords()))
                .collect(toList());

    }

}
