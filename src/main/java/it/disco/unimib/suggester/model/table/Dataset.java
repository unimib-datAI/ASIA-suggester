package it.disco.unimib.suggester.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
    private String URI = "";

    public String getDatasetName() {
        try {
            return new File(new URL(URI).getPath()).getName();
        } catch (MalformedURLException e) {
            return "";
        }
    }

}
