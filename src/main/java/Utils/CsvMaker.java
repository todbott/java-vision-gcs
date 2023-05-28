package Utils;

import Models.Stats;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CsvMaker {

    private String RawString;

    public void CsvMaker(String rawString) {
        this.RawString = rawString;
    }

    // This is the content we receive in this class, as an example:

    //    Petitions with NOA1 dates as
    //    follows are currently being
    //    processed:
    //    I-129F for K-1 Visa
    //    CSC: April 5, 2022
    //    TSC: April 6, 2022
    //    VSC: May 27, 2023
    //    I-129F for K-3 Visa
    //    CSC: May 27, 2023
    //    VSC: May 27, 2023
    //    I-130 for IR1/CR1 & K-3 Visas
    //    CSC: April 27, 2022
    //    VSC: May 19, 2022
    //    NSC: May 23, 2022
    //    TSC: May 12, 2022
    //    NBC: January 4, 2023.
    //    I-765 (Employment Authorization)
    //    November 16, 2022
    //    I-131 (Advance Parole)
    //    July 6, 2022
    //    I-751 (Lifting Conditions)
    //    CSC: November 15, 2021
    //    VSC: April 20, 2022
    //    N-400 (Naturalization)
    //    December 5, 2022
    //    based on VisaJourney Member Data

    public String getI30() {



        // Read current content of stats.json from GCP into Stats object
        ObjectMapper om = new ObjectMapper();
        try {
            Stats stats = om.readValue("fo", Stats.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        // Add newest stats
        for (String line : RawString.split(System.lineSeparator())) {
            if (line.indexOf("I-130") > -1) {

            }
        }


        // resave stat.json in gcs



        return "fo";

    }
}
