package edu.uci.ics.binghal.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;

import java.time.Year;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddStarRequestModel extends RequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "birthYear", required = false)
    private Integer birthYear;

    @JsonCreator
    public AddStarRequestModel(){}

    @JsonCreator
    public AddStarRequestModel(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "birthYear", required = false) Integer birthYear) {
        this.name = name;

        int now = Year.now().getValue();
        if (birthYear > now){
            this.birthYear = null;
        } else {
        this.birthYear = birthYear;}

    }

    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(value = "birthYear")
    public Integer getBirthYear() {
        return birthYear;
    }
}
