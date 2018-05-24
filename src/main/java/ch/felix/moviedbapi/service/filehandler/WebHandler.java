package ch.felix.moviedbapi.service.filehandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Felix
 * @project Menthe2.0
 * @with IntelliJ IDEA
 **/
public class WebHandler {

    private URL url;

    /**
     * This class is good to use for XML or Json Rest services. Rest results cant be handled here
     *
     * @param url of the website
     */
    public WebHandler(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @returns the Text content on the website
     */
    public String getContent() {
        try {
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuilder sb = new StringBuilder();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getContent(String auth) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            AtomicReference<String> basicAuth = new AtomicReference<>("Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
            conn.setRequestProperty("Authorization", basicAuth.get());

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuilder end = new StringBuilder();
            while ((output = br.readLine()) != null) {
                end.append(output);
            }
            conn.disconnect();
            return end.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}