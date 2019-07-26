package it.disco.unimib.suggester.service.suggester;

import it.disco.unimib.suggester.model.suggestion.Suggestion;
import lombok.NonNull;

import java.util.List;

public interface ISuggester {

    List<Suggestion> propertySuggestions(@NonNull String keyword, boolean filter);

    List<Suggestion> typeSuggestions(@NonNull String keyword, boolean filter);

    List<Suggestion> objectSuggestions(@NonNull String keyword, boolean filter);

    List<Suggestion> objectSuggestionsMultipleKeywords(@NonNull List<String> keywords, boolean filter);

    List<Suggestion> propertySuggestionsMultipleKeywords(@NonNull List<String> keywords, boolean filter);

    List<Suggestion> typeSuggestionsMultipleKeywords(@NonNull List<String> keywords, boolean filter);

    boolean isTest();

    void setTest(boolean test);

    void setPreferredSummaries(List<String> preferredSummaries);

    List<String> getSummaries();
}
