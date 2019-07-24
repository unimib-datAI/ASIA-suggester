package it.disco.unimib.suggester.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column {
    private Header header;
    private String dataType;
}
