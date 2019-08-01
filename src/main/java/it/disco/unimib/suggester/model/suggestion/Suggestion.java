package it.disco.unimib.suggester.model.suggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {
    private String prefix;
    private String suggestion;
    private String namespace;
    private String entityName;
    private Long occurrence;
    private String dataset;
    private Integer positionDataset;
    private String searchedKeyword;
    private Double ratioIndex = Double.NaN;
    private Double calculatedIndex = Double.NaN;
    private List<Double> distances = asList(Double.NaN);

    public Integer getSearchedKeywordLength() {
        return searchedKeyword.length();
    }


    public static class SuggestionComparatorByDistanceVector implements Comparator<Suggestion> {
        @Override
        public int compare(Suggestion s1, Suggestion s2) {//
            List<Double> o1 = s1.getDistances();
            List<Double> o2 = s2.getDistances();
            int min = Math.min(o1.size(), o2.size());

            for (int i = 0; i < min; i++) {
                int res = o1.get(i).compareTo(o2.get(i));
                if (res != 0) return res;
            }
            return 0;
        }
    }

}

