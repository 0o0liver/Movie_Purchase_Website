package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


public class AddGenreRequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public AddGenreRequestModel(){}

    @JsonCreator
    public AddGenreRequestModel(
            @JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }
}
