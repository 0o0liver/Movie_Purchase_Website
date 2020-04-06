package edu.uci.ics.binghal.service.movies.core;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import javafx.util.Pair;
import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.SearchPageRequestModel;
import edu.uci.ics.binghal.service.movies.models.SearchPageResponseModel;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.SearchPageRequestModel;
import edu.uci.ics.binghal.service.movies.models.SearchPageResponseModel;
import edu.uci.ics.binghal.service.movies.models.MovieModel;

public class Search {
    private static boolean showHiddenField;

    public static SearchPageResponseModel searchMovie(SearchPageRequestModel requestModel){
        showHiddenField = true;
        String[] movieIdList;
        MovieModel[] movieModelList;

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(requestModel.getEmail(), 3)){
            ServiceLogger.LOGGER.info("User plevel is below 4. Only show non-hidden movies. Hide \"hidden\" field.");
            requestModel.setHidden(false);
            showHiddenField = false;
        }
        if (requestModel.getGenre() == null && requestModel.getOrderby().equals("title")){
            ServiceLogger.LOGGER.info("Does not need join genre table and rating table.");
            movieIdList = noGenreSortByTitle(requestModel);
        }
        else if (requestModel.getGenre() == null && requestModel.getOrderby().equals("rating")){
            ServiceLogger.LOGGER.info("Does not need join genre table. Need to join rating table.");
            movieIdList = noGenreSortByRating(requestModel);
        }
        else if (requestModel.getGenre() != null && requestModel.getOrderby().equals("title")){
            ServiceLogger.LOGGER.info("Need to join genre table. Does not need to join rating table.");
            movieIdList = genreSortByTitle(requestModel);
        }
        else {
            ServiceLogger.LOGGER.info("Need to join genre table and rating table.");
            movieIdList = genreSortByRating(requestModel);
        }

        if (movieIdList.length == 0){
            return new SearchPageResponseModel(211, "No movies found with search parameters.", null);
        }

        movieModelList = generateMovieModelFromIdList(movieIdList);

        return new SearchPageResponseModel(210, "Found movies with search parameters.", movieModelList);

    }

    private static MovieModel[] generateMovieModelFromIdList(String[] movieIdList){
        ServiceLogger.LOGGER.info("Generating list of movie models from list of movie ids.");
        List<MovieModel> movieModelList = new ArrayList<MovieModel>();
        for (int i = 0; i < movieIdList.length; ++i){
            movieModelList.add(generateMovieModelFromId(movieIdList[i]));
        }
        MovieModel[] returnArray = new MovieModel[movieModelList.size()];
        returnArray = movieModelList.toArray(returnArray);
        return returnArray;
    }

    private static MovieModel generateMovieModelFromId(String id){
        ServiceLogger.LOGGER.info("Generating single movie model from movie with id: " + id);
        Pair<Float, Integer> ratingVotePair = generateRatingVote(id);
        try{
            String query = "SELECT * FROM movies WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            MovieModel returnModel = new MovieModel(id, rs.getString("title"), rs.getString("director"),
                    (Integer)rs.getObject("year"), ratingVotePair.getKey(), ratingVotePair.getValue(),
                    (Boolean)rs.getBoolean("hidden"));
            if (showHiddenField == false){
                returnModel.setHidden(null);
            }
            return returnModel;
        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty MovieModel.");
        }
        return new MovieModel();
    }

    private static Pair<Float, Integer> generateRatingVote(String id){
        ServiceLogger.LOGGER.info("Generating rating and numVoets for movie with id: " + id);
        try{
            String query = "SELECT rating, numVotes FROM ratings WHERE movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new Pair<Float, Integer>((Float)rs.getObject("rating"), (Integer)rs.getObject("numVotes"));
        } catch (Exception e){
            ServiceLogger.LOGGER.warning("Something went wrong. Returning negative values.");
            return new Pair<Float, Integer>(null, null);
        }
    }

    private static String[] genreSortByRating(SearchPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating list of movieIds according to parameters.");
        try{
            List<String> returnIdList = new ArrayList<String>();
            String genreIDQuery = "SELECT id FROM genres WHERE name = ?;";
            PreparedStatement genreIDps = MovieService.getCon().prepareStatement(genreIDQuery);
            genreIDps.setString(1, requestModel.getGenre());
            ResultSet genreIDrs = genreIDps.executeQuery();
            genreIDrs.next();
            int genreId = genreIDrs.getInt("id");
            String query = "SELECT DISTINCT ratings.movieId FROM ratings JOIN movies on ratings.movieId = movies.id " +
                    "JOIN genres_in_movies on movies.Id = genres_in_movies.movieId WHERE genreId = ? AND " +
                    "title LIKE ? AND director LIKE ?";
            if (requestModel.getYear() != null) {
                query += " AND year = ?";
            }
            if (!(showHiddenField == true && requestModel.getHidden() == true)) {
                query += " AND hidden = ?";
            }
            query += " ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection() +
                    ", title asc LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset() + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ps.setInt(1, genreId);
            if (requestModel.getTitle() == null) {
                ps.setString(2, "%");
            } else {
                ps.setString(2, "%" + requestModel.getTitle() + "%");
            }
            if (requestModel.getDirector() == null) {
                ps.setString(3, "%");
            } else {
                ps.setString(3, "%" + requestModel.getDirector() + "%");
            }

            if (requestModel.getYear() != null) {
                ps.setInt(4, requestModel.getYear());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() != null){
                ps.setBoolean(5, requestModel.getHidden());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() == null){
                ps.setBoolean(4, requestModel.getHidden());
            }

            ServiceLogger.LOGGER.info("Executing: " + ps.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                returnIdList.add(rs.getString("movieId"));
            }
            String[] returnArray = new String[(returnIdList.size())];
            returnArray = returnIdList.toArray(returnArray);
            return returnArray;

        } catch (Exception e){
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty list.");
            String[] returnArray = new String[1];
            return returnArray;
        }
    }

    private static String[] genreSortByTitle(SearchPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating list of movieIds according to parameters.");
        try{
            List<String> returnIdList = new ArrayList<String>();
            String genreIDQuery = "SELECT id FROM genres WHERE name = ?;";
            PreparedStatement genreIDps = MovieService.getCon().prepareStatement(genreIDQuery);
            genreIDps.setString(1, requestModel.getGenre());
            ResultSet genreIDrs = genreIDps.executeQuery();
            genreIDrs.next();
            int genreId = genreIDrs.getInt("id");
            String query = "SELECT DISTINCT id FROM movies JOIN genres_in_movies gim on movies.id = gim.movieId " +
                    "WHERE genreId = ? AND title LIKE ? AND director LIKE ?";
            if (requestModel.getYear() != null) {
                query += " AND year = ?";
            }
            if (!(showHiddenField == true && requestModel.getHidden() == true)) {
                query += " AND hidden = ?";
            }
            query += " ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection() +
                    " LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset() + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ps.setInt(1, genreId);
            if (requestModel.getTitle() == null) {
                ps.setString(2, "%");
            } else {
                ps.setString(2, "%" + requestModel.getTitle() + "%");
            }
            if (requestModel.getDirector() == null) {
                ps.setString(3, "%");
            } else {
                ps.setString(3, "%" + requestModel.getDirector() + "%");
            }

            if (requestModel.getYear() != null) {
                ps.setInt(4, requestModel.getYear());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() != null){
                ps.setBoolean(5, requestModel.getHidden());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() == null){
                ps.setBoolean(4, requestModel.getHidden());
            }

            ServiceLogger.LOGGER.info("Executing: " + ps.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                returnIdList.add(rs.getString("id"));
            }
            String[] returnArray = new String[(returnIdList.size())];
            returnArray = returnIdList.toArray(returnArray);
            return returnArray;

        } catch (Exception e){
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty list.");
            String[] returnArray = new String[1];
            return returnArray;
        }
    }

    private static String[] noGenreSortByRating(SearchPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating list of movieIds according to parameters.");
        try {
            List<String> returnIdList = new ArrayList<String>();
            String query = "SELECT DISTINCT movies.id FROM movies join ratings on movies.id = ratings.movieId " +
                    "WHERE title LIKE ? AND director LIKE ?";
            if (requestModel.getYear() != null) {
                query += " AND year = ?";
            }
            if (!(showHiddenField == true && requestModel.getHidden() == true)) {
                query += " AND hidden = ?";
            }
            query += " ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection() +
                    ", title asc LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset() + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            if (requestModel.getTitle() == null) {
                ps.setString(1, "%");
            } else {
                ps.setString(1, "%" + requestModel.getTitle() + "%");
            }

            if (requestModel.getDirector() == null) {
                ps.setString(2, "%");
            } else {
                ps.setString(2, "%" + requestModel.getDirector() + "%");
            }

            if (requestModel.getYear() != null) {
                ps.setInt(3, requestModel.getYear());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() != null){
                ps.setBoolean(4, requestModel.getHidden());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() == null){
                ps.setBoolean(3, requestModel.getHidden());
            }

            ServiceLogger.LOGGER.info("Executing: " + ps.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                returnIdList.add(rs.getString("id"));
            }
            String[] returnArray = new String[(returnIdList.size())];
            returnArray = returnIdList.toArray(returnArray);
            return returnArray;
        } catch (Exception e){
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty list.");
            String[] returnArray = new String[1];
            return returnArray;
        }
    }

    private static String[] noGenreSortByTitle(SearchPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating list of movieIds according to parameters.");
        try{
            List<String> returnIdList = new ArrayList<String>();
            String query = "SELECT DISTINCT id FROM movies WHERE title LIKE ? AND director LIKE ?";

            if (requestModel.getYear() != null){
                query += " AND year = ?";
            }
            if (!(showHiddenField == true && requestModel.getHidden() == true)) {
                query += " AND hidden = ?";
            }
            query += " ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection() +
                    " LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset() + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            if (requestModel.getTitle() == null){
                ps.setString(1, "%");
            } else {
                ps.setString(1, "%" + requestModel.getTitle() + "%");
            }

            if (requestModel.getDirector() == null){
                ps.setString(2, "%");
            } else {
                ps.setString(2, "%" + requestModel.getDirector() + "%");
            }

            if (requestModel.getYear() != null) {
                ps.setInt(3, requestModel.getYear());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() != null){
                ps.setBoolean(4, requestModel.getHidden());
            }
            if ((!(showHiddenField == true && requestModel.getHidden() == true)) && requestModel.getYear() == null){
                ps.setBoolean(3, requestModel.getHidden());
            }

            ServiceLogger.LOGGER.info("Executing: " + ps.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                returnIdList.add(rs.getString("id"));
            }

            String[] returnArray = new String[(returnIdList.size())];
            returnArray = returnIdList.toArray(returnArray);
            return returnArray;
        } catch (Exception e){
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty list.");
            String[] returnArray = new String[1];
            return returnArray;
        }
    }

}
