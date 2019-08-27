package it.disco.unimib.suggester.service.suggester.abstat.domain;

import it.disco.unimib.suggester.model.table.Summary;
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
class Summaries {
    private List<Summary> summaries;

    public List<String> getDatasetsNames() {
        return isEmpty(summaries) ?
                Collections.emptyList() : summaries.stream().map(Summary::getDsName).distinct().collect(Collectors.toList());
    }
}
