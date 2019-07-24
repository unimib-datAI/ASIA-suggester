package it.disco.unimib.suggester.service.suggester;

import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Datasets;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;

public interface ISuggester {
    Datasets summaries() throws IOException;

    List<Suggestion> propertySuggestions(@NonNull String keyword, boolean filter);

    List<Suggestion> typeSuggestions(@NonNull String keyword, boolean filter);

    List<Suggestion> objectSuggestions(@NonNull String keyword, boolean filter);

    List<Suggestion> objectSuggestionsMultipleKeywords(@NonNull List<String> keywords, boolean filter);

    List<Suggestion> propertySuggestionsMultipleKeywords(@NonNull List<String> keywords, boolean filter);

    List<Suggestion> typeSuggestionsMultipleKeywords(@NonNull List<String> keywords, boolean filter);

    boolean isTest();

    void setTest(boolean test);

    List<String> getPreferredSummaries();
}
