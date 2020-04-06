package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarinRequestModel {
    @JsonProperty(value = "starid", required = true)
    private String starid;
    @JsonProperty(value = "movieid", required = true)
    private String movieid;

    @JsonCreator
    public StarinRequestModel(){}

    @JsonCreator
    public StarinRequestModel(
            @JsonProperty(value = "starid", required = true) String starid,
            @JsonProperty(value = "movieid", required = true) String movieid) {
        this.starid = starid;
        this.movieid = movieid;
    }

    @JsonProperty(value = "starid")
    public String getStarid() {
        return starid;
    }

    @JsonProperty(value = "movieid")
    public String getMovieid() {
        return movieid;
    }
}
