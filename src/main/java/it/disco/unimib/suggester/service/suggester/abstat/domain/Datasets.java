package it.disco.unimib.suggester.service.suggester.abstat.domain;

import it.disco.unimib.suggester.model.table.Dataset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public
class Datasets {
    private List<Dataset> datasets;

    public List<String> getDatasetsNames() {
        return isEmpty(datasets) ?
                Collections.emptyList() : datasets.stream().map(Dataset::getName).collect(Collectors.toList());
    }
}
