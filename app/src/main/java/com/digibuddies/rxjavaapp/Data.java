
package com.digibuddies.rxjavaapp;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("worldpopulation")
    @Expose
    public List<Worldpopulation> worldpopulation = null;

    public class Worldpopulation {

        @SerializedName("rank")
        @Expose
        public Integer rank;
        @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("population")
        @Expose
        public String population;
        @SerializedName("flag")
        @Expose
        public String flag;

    }
}
