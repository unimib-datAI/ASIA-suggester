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
    private String id;
    private String name;
    private String timestamp;
    private String server;
    private long numberOfTriples;
}
