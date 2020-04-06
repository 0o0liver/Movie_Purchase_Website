package edu.uci.ics.binghal.service.movies.models;

public class SearchStarRequestModel{
    private String name;
    private Integer birthYear;
    private String movieTitle;
    private Integer limit;
    private Integer offset;
    private String orderby;
    private String direction;

    public SearchStarRequestModel(String name, Integer birthYear, String movieTitle, Integer limit, Integer offset, String orderby, String direction) {
        this.name = name;
        this.birthYear = birthYear;
        this.movieTitle = movieTitle;

        if (limit == null || (limit != 10 && limit != 25 && limit != 50 && limit != 100)) {
            this.limit = 10;
        } else {
            this.limit = limit;
        }

        if (offset == null || offset <= 0 || offset % this.limit != 0){
            this.offset = 0;
        } else {
            this.offset = offset;
        }

        if (orderby == null || (!orderby.equals("name") && !orderby.equals("birthYear"))){
            this.orderby = "name";
        } else {
            this.orderby = orderby;
        }

        if (direction == null || (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc"))){
            this.direction = "asc";
        } else {
            this.direction = direction;
        }
    }

    public String getName() {
        return name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getOrderby() {
        return orderby;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "SearchStarRequestModel{" +
                "name='" + name + '\'' +
                ", birthYear=" + birthYear +
                ", movieTitle='" + movieTitle + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", orderby='" + orderby + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}