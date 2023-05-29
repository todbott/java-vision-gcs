package Utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;

import java.io.*;
import java.net.URL;
import java.lang.String;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;

public class GcsConnector {

    String BucketName;
    GoogleCredentials Credentials;

    Storage Storage;

    Bucket Bucket;

    public GcsConnector(String bucketName, GoogleCredentials credentials) {
        this.BucketName = bucketName;
        this.Credentials = credentials;
        this.Storage = StorageOptions.newBuilder().setCredentials(Credentials).build().getService();
        this.Bucket = this.Storage.get(this.BucketName);
    }

    public void putNewestEstimate() {

        try {
            InputStream in = new URL("https://www.visajourney.com/timeline/images/uscis_estimates.gif").openStream();
            this.Bucket.create("estimate.gif", in, "gif");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
