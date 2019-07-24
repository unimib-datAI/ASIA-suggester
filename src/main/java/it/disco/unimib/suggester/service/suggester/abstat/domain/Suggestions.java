package it.disco.unimib.suggester.service.suggester.abstat.domain;

import it.disco.unimib.suggester.model.suggestion.Suggestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestions {
    private List<Suggestion> suggestions = new ArrayList<>();
}