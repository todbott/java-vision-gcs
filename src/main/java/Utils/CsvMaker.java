package Utils;

import java.time.Month;

public class CsvMaker {

    private String RawString;
    private DatabaseService Dbs;

    public CsvMaker(String fullGifText) {
        this.RawString = fullGifText;
        this.Dbs = new DatabaseService();
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

    public void getI30() {

        // Add newest stats
        int backward = 1;
        String[] lines = RawString.split(System.lineSeparator());
        int safetyCatch = 0;
        for (int x = 1; x < lines.length; x++) {

            safetyCatch += 1;

            if ((lines[x-backward].contains("I-130")) && (lines[x].contains(":"))) {

                String center = lines[x].split(": ")[0];
                String monthDayYear = lines[x].split(": ")[1].replace(".", "");
                String month = monthDayYear.split(" ")[0];
                Integer monthAsInteger = Month.valueOf(month.toUpperCase()).getValue();
                String day = monthDayYear.split(", ")[0].split(" ")[1];
                String year = monthDayYear.split(" ")[2];
                String date = year + "-" + monthAsInteger.toString() + "-" + day;
                this.Dbs.addToDatabase(center, date);

                backward += 1;

            }

            if (safetyCatch > 100) {
                break;
            }
        }
        this.Dbs.closeConnection();
    }
}
