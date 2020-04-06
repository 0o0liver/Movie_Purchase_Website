package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.movies.models.GenreModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPageRequestModel{
    @JsonProperty(value = "title", required = true)
    private String TITLE;
    @JsonProperty(value = "director", required = true)
    private String DIRECTOR;
    @JsonProperty(value = "year", required = true)
    private Integer YEAR;
    @JsonProperty(value = "backdrop_path", required = false)
    private String BACKDROP_PATH;
    @JsonProperty(value = "budget", required = false)
    private Integer BUDGET;
    @JsonProperty(value = "overview", required = false)
    private String OVERVIEW;
    @JsonProperty(value = "poster_path", required = false)
    private String POSTER_PATH;
    @JsonProperty(value = "revenue", required = false)
    private Integer REVENUE;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] GENRES;

    @JsonCreator
    public AddPageRequestModel(){}

    @JsonCreator
    public AddPageRequestModel(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year", required = true) Integer year,
            @JsonProperty(value = "backdrop_path", required = false) String backdrop_path,
            @JsonProperty(value = "budget", required = false) Integer budget,
            @JsonProperty(value = "overview", required = false) String overview,
            @JsonProperty(value = "poster_path", required = false) String poster_path,
            @JsonProperty(value = "revenue", required = false) Integer revenue,
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
    public Integer getYEAR() {
        return YEAR;
    }

    @JsonProperty("backdrop_path")
    public String getBACKDROP_PATH() {
        return BACKDROP_PATH;
    }

    @JsonProperty("budget")
    public Integer getBUDGET() {
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
    public Integer getREVENUE() {
        return REVENUE;
    }

    @JsonProperty("genres")
    public GenreModel[] getGENRES() {
        return GENRES;
    }
}