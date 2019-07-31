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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static it.disco.unimib.suggester.model.translation.LanguageType.*;
import static it.disco.unimib.suggester.service.OrchestratorUtils.setProcessedWords;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.isEmpty;


@Service
public class Orchestrator {


    private ITranslator translator;
    private ISuggester suggester;

    @Autowired
    public Orchestrator(ITranslator translator, ISuggester suggester) {
        this.translator = translator;
        this.suggester = suggester;
    }


    private static TableSchema setSplitTerms(TableSchema schema) {

        schema.getColumnList().stream().map(Column::getHeader)
                .forEach(header -> header.setSplitTerms(asList(header.getProcessedWord().split("\\s"))));
        return schema;
    }

    public String translate(String processedString) throws IOException {
        translator.translate(singletonList(processedString), EN);

        return null;
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

    private Column detectLanguageForColumn(Column column) {
        Header header = column.getHeader();
        if (!checkSupportedLanguage(header.getLanguage()))
            header.setLanguage(translator.detect(asList(header.getProcessedWord())).get(0).getLanguageEnum());
        return column;
    }

    public TableSchema translateTableSchema(TableSchema schema) {
        return setTranslatedWords(
                setSplitTerms(
                        detectLanguagesForTableSchema(
                                setProcessedWords(schema))));
    }

    private TableSchema setTranslatedWords(TableSchema schema) {

        if (schema.getLanguage() != EN) {
            List<Pair<List<ILookedupTerm>, Header>> listPairTranslatedTermsHeader =
                    schema.getColumnList()
                            .stream()
                            .map(Column::getHeader)
                            .map(header -> Pair.of(header.getSplitTerms(), header))
                            .map(p -> Pair.of(
                                    translator.lookup(p.getFirst(), p.getSecond().getLanguage(), EN),
                                    p.getSecond()))
                            .collect(toList());

            listPairTranslatedTermsHeader.forEach(OrchestratorUtils::setTranslatedWords);

            listPairTranslatedTermsHeader.forEach(OrchestratorUtils::setTranslatedPhrases);

            schema.getColumnList()
                    .stream()
                    .map(Column::getHeader)
                    .forEach(OrchestratorUtils::generateAndSetPhrasesCombinatorially);
        } else {
            // TODO: 2019-07-31 fix for english header
        }
        return schema;
    }




    private void suggestPredicates(TableSchema schema, List<String> preferredSummaries) {
        schema.getColumnList().stream().forEach(column -> updateColumnWithSuggestedPredicate(column, preferredSummaries));
    }

    private void updateColumnWithSuggestedPredicate(Column column, List<String> preferredSummaries) {

        List<String> words = column.getHeader().getTranslatedPhrases().stream().map(TranslatedWord::getTranslatedWord).collect(toList());
        suggester.setPreferredSummaries(preferredSummaries);
        List<Suggestion> suggestions = suggester.propertySuggestionsMultipleKeywords(words).stream()
                .filter(suggestion -> !isEmpty(suggestion))
                .flatMap(Collection::stream)
                .distinct()
                .sorted(comparing(Suggestion::getOccurrence)).collect(toList());
        suggester.setPreferredSummaries(null);
        column.getHeader().setSuggestions(suggestions);

    }

    private Header setTranslatedWords(Header header) {
        LanguageType lang = header.getLanguage();
        if (!checkSupportedLanguage(lang))
            header.setLanguage(translator.detect(asList(header.getOriginalWord())).get(0).getLanguageEnum());
        return header;
    }

    TableSchema detectLanguagesForTableSchema(TableSchema schema) {

        if (schema.getLanguage() == UNKNOWN) {
            schema.getColumnList().stream().forEach(this::detectLanguageForColumn);

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
            if (schema.isForceSingleLanguage())
                schema.getColumnList()
                        .stream()
                        .map(column -> column.getHeader())
                        .forEach(header -> header.setLanguage(schema.getLanguage()));
        }
        return schema;
    }

    List<IDetectedLanguage> detectLanguage(String processedString) {
        return translator.detect(singletonList(processedString));

    }

    // TODO: 2019-07-31 fix the italian language used as 'from' language.
    List<TranslatedWord> lookup(String processedString) {
        return translator
                .lookup(singletonList(processedString), IT, EN)
                .get(0).getTranslations()
                .stream()
                .map(t -> TranslatedWord.of(
                        t.getTarget(),
                        t.getConfidence(),
                        t.getNumWords()))
                .collect(toList());

    }
}
