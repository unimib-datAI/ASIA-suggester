package it.disco.unimib.suggester.service.suggester.lov.domain;


import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LOVSearchResult {

    private String dataset;
    private String total_results;
    private Integer page;
    private Integer page_size;
    private String queryString;
    private Aggregation aggregations;
    private List<Result> results;

    public LOVSearchResult setDataset(String dataset) {
        this.dataset = dataset;
        results.forEach(result -> result.setDataset(dataset));
        return this;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Vocabs {
        private Integer doc_count_error_upper_bound;
        private Integer sum_other_doc_count;
        private List<Bucket> buckets;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class Bucket {
            private String key;
            private Integer doc_count;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class Aggregation {
        private Vocabs vocabs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Result {
        private String dataset;
        private String[] prefixedName;
        @SerializedName("vocabulary.prefix")
        private String[] vocabularyPrefix;
        private String[] uri;
        @SerializedName("metrics.occurrencesInDatasets")
        private Integer[] occurrencesInDatasets;
        private String type;
        private Double score;
        private String searchedKeyword;


    }
}
