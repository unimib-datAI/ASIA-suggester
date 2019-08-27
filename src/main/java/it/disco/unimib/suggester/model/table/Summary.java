package it.disco.unimib.suggester.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Summary {
    private String id;
    private String dsId;
    private String dsName;
    private List<String> listOntId;
    private List<String> listOntNames;
    private String timestamp;
    private String server;
    private boolean tipoMinimo;
    private boolean inferences;
    private boolean cardinalita;
    private boolean richCardinalities;
    private boolean propertyMinimaliz;
    private boolean shaclValidation;
    private long numberOfTriples;
    private long numberOfPatterns;
    private boolean loadedMongoDB;
    private boolean indexedSolr;
}
