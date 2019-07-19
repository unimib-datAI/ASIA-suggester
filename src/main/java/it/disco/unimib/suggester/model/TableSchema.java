package it.disco.unimib.suggester.model;


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

    public void addColumn(Column column) {
        columnList.add(column);
    }

}
