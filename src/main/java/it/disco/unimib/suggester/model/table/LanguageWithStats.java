package it.disco.unimib.suggester.model.table;

import it.disco.unimib.suggester.model.translation.LanguageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageWithStats {
    private LanguageType languageType;
    private Double frequency;
}
