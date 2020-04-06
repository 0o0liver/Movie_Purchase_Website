package edu.uci.ics.binghal.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.binghal.service.api_gateway.models.movie.StarModel;
import edu.uci.ics.binghal.service.api_gateway.models.movie.GenreModel;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDMovieModel{
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = false)
    private String director;
    @JsonProperty(value = "year", required = false)
    private Integer year;
    @JsonProperty(value = "backdrop_path", required = false)
    private String backdrop_path;
    @JsonProperty(value = "budget", required = false)
    private Integer budget;
    @JsonProperty(value = "overview", required = false)
    private String overview;
    @JsonProperty(value = "poster_path", required = false)
    private String poster_path;
    @JsonProperty(value = "revenue", required = false)
    private Integer revenue;
    @JsonProperty(value = "rating", required = true)
    private Float rating;
    @JsonProperty(value = "numVotes", required = false)
    private Integer numVotes;
    @JsonProperty(value = "hidden", required = false)
    private Boolean hidden;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] Genres;
    @JsonProperty(value = "stars", required = true)
    private StarModel[] Stars;

    @JsonCreator
    public IDMovieModel(){}

    @JsonCreator

    public IDMovieModel(
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = false) String director,
            @JsonProperty(value = "year", required = false) Integer year,
            @JsonProperty(value = "backdrop_path", required = false) String backdrop_path,
            @JsonProperty(value = "budget", required = false) Integer budget,
            @JsonProperty(value = "overview", required = false) String overview,
            @JsonProperty(value = "poster_path", required = false) String poster_path,
            @JsonProperty(value = "revenue", required = false) Integer revenue,
            @JsonProperty(value = "rating", required = true) Float rating,
            @JsonProperty(value = "numVotes", required = false) Integer numVotes,
            @JsonProperty(value = "hidden", required = false) Boolean hidden,
            @JsonProperty(value = "genres", required = true) GenreModel[] Genres,
            @JsonProperty(value = "stars", required = true) StarModel[] Stars) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        if (budget == null || budget == 0) { this.budget = null; } else{ this.budget = budget; }
        this.overview = overview;
        this.poster_path = poster_path;
        if (revenue == null || revenue == 0){ this.revenue = null; } else { this.revenue = revenue; }
        this.rating = rating;
        this.numVotes = numVotes;
        this.hidden = hidden;
        this.Genres = Genres;
        this.Stars = Stars;
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

    @JsonProperty("backdrop_path")
    public String getBackdrop_path() {
        return backdrop_path;
    }

    @JsonProperty("budget")
    public Integer getBudget() {
        return budget;
    }

    @JsonProperty("overview")
    public String getOverview() {
        return overview;
    }

    @JsonProperty("poster_path")
    public String getPoster_path() {
        return poster_path;
    }

    @JsonProperty("revenue")
    public Integer getRevenue() {
        return revenue;
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

    @JsonProperty("genres")
    public GenreModel[] getGenres() {
        return Genres;
    }

    @JsonProperty("stars")
    public StarModel[] getStars() {
        return Stars;
    }
}