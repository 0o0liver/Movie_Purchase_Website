package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieModel{
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = true)
    private String director;
    @JsonProperty(value = "year", required = true)
    private Integer year;
    @JsonProperty(value = "rating", required = true)
    private Float rating;
    @JsonProperty(value = "numVotes", required = true)
    private Integer numVotes;
    @JsonProperty(value = "hidden", required = false)
    private Boolean hidden;


    @JsonCreator
    public MovieModel(){}

    @JsonCreator

    public MovieModel(
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year", required = true) Integer year,
            @JsonProperty(value = "rating", required = true) Float rating,
            @JsonProperty(value = "numVotes", required = true) Integer numVotes,
            @JsonProperty(value = "hidden", required = false) Boolean hidden){
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.numVotes = numVotes;
        this.hidden = hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @JsonProperty("movieId")
    public String getMovieId() {
        return movieId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("director")
    public String getDirector() {
        return director;
    }

    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @JsonProperty("rating")
    public Float getRating() {
        return rating;
    }

    @JsonProperty("numVotes")
    public Integer getNumVotes() {
        return numVotes;
    }

    @JsonProperty("hidden")
    public Boolean isHidden() {
        return hidden;
    }


}