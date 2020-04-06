package edu.uci.ics.binghal.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.api_gateway.models.movie.IDMovieModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieIDPageResponseModel{
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movie", required = true)
    private IDMovieModel movie;

    @JsonCreator
    public MovieIDPageResponseModel(){}

    @JsonCreator
    public MovieIDPageResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "movie", required = true) IDMovieModel movie) {
        this.resultCode = resultCode;
        this.message = message;
        this.movie = movie;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movie")
    public IDMovieModel getMovie() {
        return movie;
    }
}