package org.java_vision_gcp;

import Utils.CsvMaker;
import Utils.DetectTextGcs;
import Utils.GcsConnector;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.

        String credentialsPath = "agxactly-app-backend-2802f81a43ad.json";
        // The credentials could be loaded as well as this.getClass().getResourceAsStream(), for example
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));


        GcsConnector gcs = new GcsConnector("uscis_images", credentials);
        //gcs.putNewestEstimate();

        DetectTextGcs dtg = new DetectTextGcs();
        try {
            String fullGifText = dtg.detectTextGcs("gs://uscis_images/estimate.gif", credentials);
            CsvMaker csv = new CsvMaker();
            csv.getI30()
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
        for (int i = 1; i <= 5; i++) {

            // Press Shift+F9 to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Ctrl+F8.
            System.out.println("i = " + i);
        }
    }
}