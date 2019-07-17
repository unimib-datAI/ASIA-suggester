package it.disco.unimib.suggester.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private String originalWord;
    private List<String> splitTerms;
    private LanguageType language;
}
