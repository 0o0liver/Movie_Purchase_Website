package edu.uci.ics.binghal.service.movies.core;

import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.RatingRequestModel;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;

import java.sql.PreparedStatement;

public class Rating {
    public static VerifyPrivilegeResponseModel rate(RatingRequestModel requestModel){
        if (!SearchMovieById.movieExist(requestModel.getId())){
            ServiceLogger.LOGGER.info("No movie found with search parameters.");
            return new VerifyPrivilegeResponseModel(211, "No movies found with search parameters.");
        }
        try{
            String query = "update ratings set rating = (numVotes*rating + ?)/(numVotes+1) , numVotes = numVotes + 1 where movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setObject(1, requestModel.getRating());
            ps.setString(2, requestModel.getId());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ps.executeUpdate();
            return new VerifyPrivilegeResponseModel(250, "Rating successfully updated.");
        } catch (Exception e){
            e.printStackTrace();
            return new VerifyPrivilegeResponseModel(251, "Could not update rating.");
        }
    }
}
