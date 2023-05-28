package Utils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.String;
import java.util.Map;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GcsConnector {

    String BucketName;
    GoogleCredentials Credentials;

    public GcsConnector(String bucketName, GoogleCredentials credentials) {
        this.BucketName = bucketName;
        this.Credentials = credentials;
    }

    public void putNewestEstimate() {

        try {
            Storage storage = StorageOptions.newBuilder().setCredentials(Credentials).build().getService();
            Bucket bucket = storage.get(this.BucketName);

            InputStream in = new URL("https://www.visajourney.com/timeline/images/uscis_estimates.gif").openStream();
            bucket.create("estimate.gif", in, "gif");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
