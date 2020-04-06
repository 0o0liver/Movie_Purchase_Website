package edu.uci.ics.binghal.service.movies.models;

public class SearchPageRequestModel {
    private String title;
    private String genre;
    private Integer year;
    private String director;
    private Boolean hidden;
    private Integer limit;
    private Integer offset;
    private String orderby;
    private String direction;
    private String email;

    public SearchPageRequestModel(
            String title, String genre, Integer year, 
            String director, Boolean hidden, Integer limit, 
            Integer offset, String orderby, String direction, String email) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.director = director;

        if (hidden == null){
            this.hidden = false;
        } else {
            this.hidden = hidden;
        }

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

        if (orderby == null || (!orderby.equals("title") && !orderby.equals("rating"))){
            this.orderby = "rating";
        } else {
            this.orderby = orderby;
        }

        if (direction == null || (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc"))){
            this.direction = "desc";
        } else {
            this.direction = direction;
        }

        this.email = email;
    }

    @Override
    public String toString() {
        return "SearchPageRequestModel{" +
                "title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", hidden=" + hidden +
                ", limit=" + limit +
                ", offset=" + offset +
                ", orderby='" + orderby + '\'' +
                ", direction='" + direction + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public Boolean getHidden() {
        return hidden;
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

    public String getEmail() {
        return email;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
