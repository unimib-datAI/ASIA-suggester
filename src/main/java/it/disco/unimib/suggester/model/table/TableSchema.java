package it.disco.unimib.suggester.model.table;


import it.disco.unimib.suggester.model.translation.LanguageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableSchema {
    private List<Column> columnList = new ArrayList<>();

    private LanguageType Language = LanguageType.UNKNOWN;

    private boolean forceSingleLanguage = true;

    private List<LanguageWithStats> languageWithStatsList;

    public void addColumn(Column column) {
        columnList.add(column);
    }


}
