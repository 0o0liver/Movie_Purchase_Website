package edu.uci.ics.binghal.service.movies.core;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Service;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.uci.ics.binghal.service.movies.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;
import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Add{
    private static int CURRENT_MOVIE_ID = 0;
    private static int IDLength = 7;

    public static AddPageResponseModel addMovie(AddPageRequestModel requestModel){
        String movieid;
        int[] genreid;

        if (movieAlreadyExist(requestModel)){
            return new AddPageResponseModel(216, "Movie already exists.");
        }

        movieid = getMovieID();
        //genreid = getGenreID(requestModel);

        if (insertGenreToDb(requestModel) && insertMovieToDb(requestModel, movieid) && updateRating(movieid)){ //&& insertGenerInMovies(movieid, genreid)) {
            genreid =  getGenreID(requestModel.getGENRES());
            insertGenerInMovies(movieid, genreid);
            return new AddPageResponseModel(214, "Movie successfully added.", movieid, genreid);
        }

        return new AddPageResponseModel(215, "Could not add movie.");
    }

    private static boolean updateRating(String movieid){
        try{
            String query = "insert into ratings (movieId, rating, numVotes) values (?, ?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);
            ps.setFloat(2, 0.0f);
            ps.setInt(3, 0);
            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean movieAlreadyExist(AddPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Checking if movie already exist in the database.");
        try{
            String query = "SELECT COUNT(*) FROM movies WHERE title = ? and director = ? and year = ?";

            if (requestModel.getBACKDROP_PATH() == null){
                query += " and backdrop_path is null";
            } else {
                query += " and backdrop_path = ";
                query += requestModel.getBACKDROP_PATH();
            }

            if (requestModel.getBUDGET() == null){
                query += " and budget is null";
            } else {
                query += " and budget = ";
                query += requestModel.getBUDGET();
            }

            if (requestModel.getOVERVIEW() == null){
                query += " and overview is null";
            } else {
                query += " and overview = ";
                query += requestModel.getOVERVIEW();
            }

            if (requestModel.getPOSTER_PATH() == null){
                query += " and poster_path is null";
            } else {
                query += " and poster_path = ";
                query += requestModel.getPOSTER_PATH();
            }

            if (requestModel.getREVENUE() ==  null){
                query += " and revenue is null";
            } else {
                query += " and revenue = ";
                query += requestModel.getREVENUE();
            }

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getTITLE());
            ps.setString(2, requestModel.getDIRECTOR());
            ps.setInt(3, requestModel.getYEAR());
            ServiceLogger.LOGGER.info(ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") != 0){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return true;
        }
    }

    private static boolean insertGenerInMovies(String movieid, int[] genreid){
        ServiceLogger.LOGGER.info("Inserting genre ids and movie to genres_in_movie.");
        try {
            String query = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            for (int i = 0; i < genreid.length; ++i){
                ps.setInt(1, genreid[i]);
                ps.setString(2, movieid);
                ServiceLogger.LOGGER.info("Execuet query: " + ps.toString());
                ps.execute();
            }
            return true;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean insertMovieToDb(AddPageRequestModel requestModel, String movieid){
        ServiceLogger.LOGGER.info("Inserting movie to database.");
        try{
            String query = "INSERT INTO movies (id, title, year, director, backdrop_path, budget, overview, poster_path, revenue) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);
            ps.setString(2, requestModel.getTITLE());
            ps.setInt(3, requestModel.getYEAR());
            ps.setString(4, requestModel.getDIRECTOR());
            ps.setString(5, requestModel.getBACKDROP_PATH());
            ps.setInt(6, requestModel.getBUDGET());
            ps.setString(7, requestModel.getOVERVIEW());
            ps.setString(8, requestModel.getPOSTER_PATH());
            ps.setInt(9, requestModel.getREVENUE());
            ServiceLogger.LOGGER.info("Execuet query: " + ps.toString());
            ps.execute();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean insertGenreToDb(AddPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Inserting genre to database (if not already in database).");
        try{
            String query = "INSERT IGNORE INTO genres (name) VALUE (?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            for (int i = 0; i < requestModel.getGENRES().length; ++i){
                if (!genreExist(requestModel.getGENRES()[i].getName())) {
                    ps.setString(1, requestModel.getGENRES()[i].getName());
                    ServiceLogger.LOGGER.info("Execuet query: " + ps.toString());
                    ps.execute();
                }
            }
            return true;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean genreExist(String name){
        try{
            String query = "select COUNT(*) from genres where name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") != 0){
                return true;
            }
            return false;
        } catch (Exception e){
            return true;
        }
    }

    private static String getMovieID(){
        ServiceLogger.LOGGER.info("Generating movie id.");
        CURRENT_MOVIE_ID++;
        String id = "cs";
        int currentIdDigit = String.valueOf(CURRENT_MOVIE_ID).length();
        for (int i = 0; i < (IDLength-currentIdDigit) ; ++i){
            id = id + "0";
        }
        id = id + Integer.toString(CURRENT_MOVIE_ID);
        return id;
    }

    private static int[] getGenreID(GenreModel[] genres){
        ServiceLogger.LOGGER.info("Generating genre id array");
        int arrayLength = genres.length;
        int[] ret = new int[arrayLength];
        try{
            String query = "select id from genres where name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            for (int i = 0; i < arrayLength; ++i){
                ps.setString(1, genres[i].getName());
                ResultSet rs = ps.executeQuery();
                rs.next();
                ret[i] = rs.getInt("id");
            }
            return ret;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*private static int[] getGenreID(AddPageRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating genre id array");
        int arrayLength = requestModel.getGENRES().length;
        int[] returnArray = new int[arrayLength];
        for (int i = 0; i < arrayLength; ++i){
            returnArray[i] = requestModel.getGENRES()[i].getId();
        }
        return returnArray;
    }
     */
}