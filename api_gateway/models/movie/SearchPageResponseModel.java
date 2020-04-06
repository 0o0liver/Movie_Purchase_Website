package edu.uci.ics.binghal.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.api_gateway.models.movie.MovieModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPageResponseModel{
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movies", required = true)
    private MovieModel[] movies;

    @JsonCreator
    public SearchPageResponseModel(){}

    @JsonCreator
    public SearchPageResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "movies", required = true) MovieModel[] movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.movies = movies;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movies")
    public MovieModel[] getMovies() {
        return movies;
    }
}