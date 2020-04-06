package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPageResponseModel{
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movieid", required = false)
    private String movieid;
    @JsonProperty(value = "genreid", required = false)
    private int[] genreid;

    @JsonCreator
    public AddPageResponseModel(int resultCode, String message, String movieid, int[] genreid) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieid = movieid;
        this.genreid = genreid;
    }

    @JsonCreator
    public AddPageResponseModel(int resultCode, String message){
        this.resultCode = resultCode;
        this.message = message;
        this.movieid = null;
        this.genreid = null;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movieid")
    public String getMovieid() {
        return movieid;
    }

    @JsonProperty("genreid")
    public int[] getGenreid() {
        return genreid;
    }
}