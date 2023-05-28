package Models;
import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1

public class Stats {

    @JsonProperty("CSC")
    public ArrayList<String> cSC;
    @JsonProperty("VSC")
    public ArrayList<String> vSC;
    @JsonProperty("NSC")
    public ArrayList<String> nSC;
    @JsonProperty("TSC")
    public ArrayList<String> tSC;
    @JsonProperty("NBC")
    public ArrayList<String> nBC;

}
