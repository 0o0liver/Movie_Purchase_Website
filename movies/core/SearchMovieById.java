package edu.uci.ics.binghal.service.movies.core;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import javafx.util.Pair;
import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.models.MovieIDPageResponseModel;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;
import edu.uci.ics.binghal.service.movies.models.IDMovieModel;
import edu.uci.ics.binghal.service.movies.models.GenreModel;
import edu.uci.ics.binghal.service.movies.models.StarModel;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;

public class SearchMovieById{
    public static boolean movieExist(String id){
        ServiceLogger.LOGGER.info("Checking if movie exist.");
        try{
            String query = "SELECT COUNT(*) FROM movies WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") > 0){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Something went wrong. Return false.");
            return false;
        }
    }

    public static MovieIDPageResponseModel search(String id){
        ServiceLogger.LOGGER.info("Received id.");
        GenreModel[] movieGenres = generateGenres(id);
        StarModel[] movieStars = generateStars(id);
        Pair<Float, Integer> ratingVotePair = generateRatingVote(id);
        try{
            String query = "SELECT * FROM movies WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " +  ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            IDMovieModel returnModel = new IDMovieModel(id, rs.getString("title"), rs.getString("director"),
                    (Integer) rs.getObject("year"), rs.getString("backdrop_path"), (Integer)rs.getObject("budget"), rs.getString("overview"),
                    rs.getString("poster_path"), (Integer)rs.getObject("revenue"), ratingVotePair.getKey(), ratingVotePair.getValue(),
                    (Boolean)rs.getBoolean("hidden"), movieGenres, movieStars);
            return new MovieIDPageResponseModel(210, "Found movies with search parameters.", returnModel);
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty model.");
        }
        return new MovieIDPageResponseModel();
    }

    private static Pair<Float, Integer> generateRatingVote(String id){
        ServiceLogger.LOGGER.info("Generating rating and numVote for movie with id: " + id);
        try{
            String query = "SELECT rating, numVotes FROM ratings WHERE movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new Pair<Float, Integer>((Float)rs.getObject("rating"), (Integer)rs.getObject("numVotes"));
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Something went wrong. Return null rating and null numVote.");
            return new Pair<Float, Integer>(null, null);
        }
    }

    private static StarModel[] generateStars(String id){
        ServiceLogger.LOGGER.info("Generating StarModel list for movie with id: " + id);
        try{
            List<String> starIdList = new ArrayList<String>();
            List<StarModel> starModelList = new ArrayList<StarModel>();
            String query = "SELECT starId FROM stars_in_movies WHERE movieId = ?";
            String query1 = "SELECT name, birthYear FROM stars WHERE id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            PreparedStatement ps1 = MovieService.getCon().prepareStatement(query1);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                starIdList.add(rs.getString("starId"));
            }
            for (int i = 0; i < starIdList.size(); ++i){
                ps1.setString(1, starIdList.get(i));
                ServiceLogger.LOGGER.info("Executing: " + ps1.toString());
                ResultSet rs1 = ps1.executeQuery();
                rs1.next();
                starModelList.add(new StarModel(starIdList.get(i), rs1.getString("name"), (Integer)rs1.getObject("birthYear")));
            }
            StarModel[] returnArray = new StarModel[starModelList.size()];
            returnArray = starModelList.toArray(returnArray);
            return returnArray;
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong. Returning empty list.");
            StarModel[] returnArray = new StarModel[1];
            return returnArray;
        }
    }

    private static GenreModel[] generateGenres(String id){
        ServiceLogger.LOGGER.info("Generating GenreModel list for movie with id: " + id);
        try{
            List<Integer> genreIdList = new ArrayList<Integer>();
            List<GenreModel> genreModelList = new ArrayList<GenreModel>();
            String query = "SELECT genreId FROM genres_in_movies WHERE movieId = ?;";
            String query1 = "SELECT name FROM genres WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            PreparedStatement ps1 = MovieService.getCon().prepareStatement(query1);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                genreIdList.add(rs.getInt("genreId"));
            }
            for (int i = 0; i < genreIdList.size(); ++i){
                ps1.setInt(1, genreIdList.get(i));
                ServiceLogger.LOGGER.info("Executing: " + ps1.toString());
                ResultSet rs1 = ps1.executeQuery();
                rs1.next();
                genreModelList.add(new GenreModel(genreIdList.get(i), rs1.getString("name")));
            }
            GenreModel[] returnArray = new GenreModel[genreModelList.size()];
            returnArray =  genreModelList.toArray(returnArray);
            return returnArray;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty list.");
            GenreModel[] returnArray = new GenreModel[1];
            return returnArray;
        }
    }
}
