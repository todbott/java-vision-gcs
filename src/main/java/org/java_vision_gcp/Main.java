package org.java_vision_gcp;

import Utils.CsvMaker;
import Utils.DetectTextGcs;
import Utils.GcsConnector;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.IOException;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main implements HttpFunction{
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.


        // The credentials could be loaded as well as this.getClass().getResourceAsStream(), for example
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

            GcsConnector gcs = new GcsConnector("uscis_images", credentials);
            gcs.putNewestEstimate();

            DetectTextGcs dtg = new DetectTextGcs();
            try {
                String fullGifText = dtg.detectTextGcs("gs://uscis_images/estimate.gif", credentials);

                CsvMaker csv = new CsvMaker(fullGifText);
                csv.getI30();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}