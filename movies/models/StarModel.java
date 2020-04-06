package edu.uci.ics.binghal.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarModel{
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "birthYear", required = false)
    private Integer birthYear;


    @JsonCreator
    public StarModel(){}

    @JsonCreator
    public StarModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "birthbirthYear", required = false) Integer birthYear
    ){
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("birthYear")
    public Integer getbirthYear() {
        return birthYear;
    }
}