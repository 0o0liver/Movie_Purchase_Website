package edu.uci.ics.binghal.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;
import edu.uci.ics.binghal.service.api_gateway.models.movie.GenreModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPageRequestModel extends RequestModel {
    @JsonProperty(value = "title", required = true)
    private String TITLE;
    @JsonProperty(value = "director", required = true)
    private String DIRECTOR;
    @JsonProperty(value = "year", required = true)
    private int YEAR;
    @JsonProperty(value = "backdrop_path", required = false)
    private String BACKDROP_PATH;
    @JsonProperty(value = "budget", required = false)
    private int BUDGET;
    @JsonProperty(value = "overview", required = false)
    private String OVERVIEW;
    @JsonProperty(value = "poster_path", required = false)
    private String POSTER_PATH;
    @JsonProperty(value = "revenue", required = false)
    private int REVENUE;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] GENRES;

    @JsonCreator
    public AddPageRequestModel(){}

    @JsonCreator
    public AddPageRequestModel(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year", required = true) int year,
            @JsonProperty(value = "backdrop_path", required = false) String backdrop_path,
            @JsonProperty(value = "budget", required = false) int budget,
            @JsonProperty(value = "overview", required = false) String overview,
            @JsonProperty(value = "poster_path", required = false) String poster_path,
            @JsonProperty(value = "revenue", required = false) int revenue,
            @JsonProperty(value = "genres", required = true) GenreModel[] genres){
        this.TITLE = title;
        this.DIRECTOR = director;
        this.YEAR = year;
        this.BACKDROP_PATH = backdrop_path;
        this.BUDGET = budget;
        this.OVERVIEW = overview;
        this.POSTER_PATH = poster_path;
        this.REVENUE = revenue;
        this.GENRES = genres;
    }

    @JsonProperty("title")
    public String getTITLE() {
        return TITLE;
    }

    @JsonProperty("director")
    public String getDIRECTOR() {
        return DIRECTOR;
    }

    @JsonProperty("year")
    public int getYEAR() {
        return YEAR;
    }

    @JsonProperty("backdrop_path")
    public String getBACKDROP_PATH() {
        return BACKDROP_PATH;
    }

    @JsonProperty("budget")
    public int getBUDGET() {
        return BUDGET;
    }

    @JsonProperty("overview")
    public String getOVERVIEW() {
        return OVERVIEW;
    }

    @JsonProperty("poster_path")
    public String getPOSTER_PATH() {
        return POSTER_PATH;
    }

    @JsonProperty("revenue")
    public int getREVENUE() {
        return REVENUE;
    }

    @JsonProperty("genres")
    public GenreModel[] getGENRES() {
        return GENRES;
    }
}