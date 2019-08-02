package it.disco.unimib.suggester.configuration;


import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.apache.commons.text.similarity.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;

@Configuration
public class SuggesterConfiguration {

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Bean
    public DistanceCalculator distanceCalculator() {
        return new DistanceCalculator();
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Data
    @NoArgsConstructor
    public static class DistanceCalculator {
        private final JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        private final CosineDistance cosineDistance = new CosineDistance();
        private final HammingDistance hammingDistance = new HammingDistance();
        private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        private final LongestCommonSubsequenceDistance longestCommonSubsequenceDistance = new LongestCommonSubsequenceDistance();


        public List<Double> apply(String stringA, String stringB) {
            String stringALow = stringA.toLowerCase();
            String stringBLow = stringB.toLowerCase();
            return asList(
                    1 / jaroWinkler.apply(stringALow, stringBLow),
                    cosineDistance.apply(stringALow, stringBLow),
                    stringALow.length() == stringBLow.length() ? hammingDistance.apply(stringALow, stringBLow).doubleValue() : 0,
                    levenshteinDistance.apply(stringALow, stringBLow).doubleValue(),
                    longestCommonSubsequenceDistance.apply(stringALow, stringBLow).doubleValue()
            );
        }
    }


}
