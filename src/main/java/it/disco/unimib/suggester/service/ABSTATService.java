package it.disco.unimib.suggester.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ABSTATService {


    private String abstatBasePath;
    private String[] preferredSummaries;
    private static Pattern notAlphanumeric = Pattern.compile("[^a-z0-9]");
    private static Pattern spaces = Pattern.compile("\\s+");

    public ABSTATService(String abstatBasePath, String[] preferredSummaries) {
        this.abstatBasePath = abstatBasePath;
        this.preferredSummaries = preferredSummaries.clone();
    }

    public static String stringPreprocessing(String str) {
        Matcher m = notAlphanumeric.matcher(str);
        str = m.replaceAll(" ");
        m = spaces.matcher(str);
        str = m.replaceAll(" ");
        String[] words = str.split(" ");
        for (int i = 1; i < words.length; i++)
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        return str;
    }

    public static String filterURI(String URI) {
        if (URI.startsWith("http")) {
            int slashIdx = URI.lastIndexOf('/');
            int hashIdx = URI.lastIndexOf('#');
            int colonIdx = URI.lastIndexOf(':');
            // Che brutta cosa!
            URI = URI.substring(Math.max(slashIdx, Math.max(hashIdx, colonIdx)));
        }
        return URI;
    }

    //Direttamente da https://stackoverflow.com/a/21657510
    private static byte[] getParamsString(Map<String, Object> params)
            throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes("UTF-8");
    }

    //Dovrebbe tornare una lista, ma non riuscendo a testare l'endpoint
    //ritorno una stringa
    private String abstatSuggestions(String keyword, String position)
            throws MalformedURLException, UnsupportedEncodingException, IOException {
        URL url = new URL(this.abstatBasePath + "/api/v1/SolrSuggestions");
        Map<String, Object> params = new LinkedHashMap<>();
        if (keyword != "" && position != "") {
            params.put("qString", keyword);
            params.put("qPosition", position);
            params.put("rows", 15);
            params.put("start", 0);

            byte[] getData = getParamsString(params).toString().getBytes("UTF-8");
            HttpURLConnection connection = (HttpURLConnection) (url.openConnection());
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(getData.length));
            connection.setDoOutput(true);
            connection.getOutputStream().write(getData);

            Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            return sb.toString();
        }
        return "";
    }

    //Dovrebbe tornare una lista, ma non riuscendo a testare l'endpoint
    //ritorno una stringa
    public String propertySuggestions(String keyword, boolean filter) throws IOException {
        keyword = filterURI(keyword);
        if (filter)
            keyword = stringPreprocessing(keyword);
        return this.abstatSuggestions(keyword, "pred");
    }

    //TODO
    public String listSummaries() {
        return "";
    }
}
