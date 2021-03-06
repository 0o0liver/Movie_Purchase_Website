package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingRequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "rating", required = true)
    private Float rating;

    @JsonCreator
    public RatingRequestModel(){}

    @JsonCreator
    public RatingRequestModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "rating", required = true) Float rating) {
        this.id = id;
        this.rating = rating;
    }

    @JsonProperty(value = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(value = "rating")
    public Float getRating() {
        return rating;
    }
}
