package edu.uci.ics.binghal.service.movies.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;

public class Remove{
    public static VerifyPrivilegeResponseModel removeMovie(String movieid){
        if (isAlreadyRemoved(movieid)) {
            return new VerifyPrivilegeResponseModel(242, "Movie has been already removed.");
        }
        else if (movieRemoved(movieid)) {
            return new VerifyPrivilegeResponseModel(240, "Movie successfully removed.");
        }
        else{
            return new VerifyPrivilegeResponseModel(241, "Could not remove movie.");
        }
    }

    private static  boolean isAlreadyRemoved(String movieid){
        ServiceLogger.LOGGER.info("Checking is movie: " + movieid + " is already removed.");
        try{
            String query = "select hidden from movies where id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getBoolean("hidden");
        } catch (Exception e){
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean movieRemoved(String movieid){
        ServiceLogger.LOGGER.info("Removing movie: " +  movieid + ".");
        try{
            String query = "UPDATE movies SET hidden = TRUE WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            int ret = ps.executeUpdate();
            if (ret == 0){
                return false;
            }
            return true;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            e.printStackTrace();
            return false;
        }
    }
}