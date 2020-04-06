package edu.uci.ics.binghal.service.movies.core;

import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Star {
    private static int CURRENT_STAR_ID = 0;
    private static int IDLength = 7;

    public static SearchStarResponseModel search(SearchStarRequestModel requestModel){
        String[] starIdList;
        StarModel[] starModelList;

        if (requestModel.getMovieTitle() == null){
            ServiceLogger.LOGGER.info("Does not need to join movies table.");
            starIdList = noTitle(requestModel);
        } else {
            ServiceLogger.LOGGER.info("Need to join movies table.");
            starIdList = withTitle(requestModel);
        }

        if (starIdList.length == 0){
            ServiceLogger.LOGGER.info("No stars found with search parameters.");
            return new SearchStarResponseModel(213, "No stars found with search parameters.", null);
        }

        starModelList = generateStarModelFromIdList(starIdList);

        return new SearchStarResponseModel(212, "Found stars with search parameters.", starModelList);
    }

    public static StarSearchIDResponseModel searchByID(String id){
        try{
            String query = "select * from stars where id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() == false){
                return new StarSearchIDResponseModel(213, "No stars found with search parameters.", null);
            }
            StarModel ret = new StarModel(rs.getString("id"), rs.getString("name"), (Integer)rs.getObject("birthYear"));
            return new StarSearchIDResponseModel(212, "Found stars with search parameters.", ret);
        } catch (Exception e){
            e.printStackTrace();
            return new StarSearchIDResponseModel(-1, "Internal Server Error", null);
        }
    }

    public static VerifyPrivilegeResponseModel addStar(AddStarRequestModel requestModel){
        try{
            if (starAlreadyExist(requestModel)){
                return new VerifyPrivilegeResponseModel(222, "Star already exists.");
            }
            String query = "insert into stars (id, name, birthYear) values (?, ?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            String id = getStarID();
            ps.setString(1, id);
            ps.setString(2, requestModel.getName());
            ps.setObject(3, requestModel.getBirthYear());
            ps.executeUpdate();
            return new VerifyPrivilegeResponseModel(220, "Star successfully added.");
        } catch (Exception e){
            e.printStackTrace();
            return new VerifyPrivilegeResponseModel(221, "Could not add star.");
        }
    }

    public static VerifyPrivilegeResponseModel starin (StarinRequestModel requestModel){
        if (!SearchMovieById.movieExist(requestModel.getMovieid())){
            return new VerifyPrivilegeResponseModel(211, "No movies found with search parameters.");
        }
        if (starAlreadyInMovie(requestModel)){
            return new VerifyPrivilegeResponseModel(232, "Star already exists in movie.");
        }
        try{
            String query = "insert into stars_in_movies (starId, movieId) values (?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getStarid());
            ps.setString(2, requestModel.getMovieid());
            ps.executeUpdate();
            return new VerifyPrivilegeResponseModel(230, "Star successfully added to movie.");
        } catch (Exception e){
            e.printStackTrace();
            return new VerifyPrivilegeResponseModel(231, "Could not add star to movie.");
        }
    }

    private static boolean starAlreadyInMovie(StarinRequestModel requestModel){
        try {
            String query = "select COUNT(*) from stars_in_movies where movieId = ? and starId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getMovieid());
            ps.setString(2, requestModel.getStarid());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") != 0){
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    private static boolean starAlreadyExist(AddStarRequestModel requestModel){
        try{
            String query = "select COUNT(*) from stars where name = ? and birthYear = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getName());
            ps.setObject(2, requestModel.getBirthYear());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") != 0){
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    private static String getStarID(){
        ServiceLogger.LOGGER.info("Generating star id.");
        CURRENT_STAR_ID++;
        String id = "ss";
        int currentIdDigit = String.valueOf(CURRENT_STAR_ID).length();
        for (int i = 0; i < (IDLength-currentIdDigit) ; ++i){
            id = id + "0";
        }
        id = id + Integer.toString(CURRENT_STAR_ID);
        return id;
    }

    private static StarModel[] generateStarModelFromIdList(String[] starIdList){
        ServiceLogger.LOGGER.info("Generating list of star model from list of star ids.");
        List<StarModel> starModelList = new ArrayList<StarModel>();
        for (int i = 0; i < starIdList.length; ++i){
            starModelList.add(generateStarModelFromId(starIdList[i]));
        }
        StarModel[] returnArray = new StarModel[starModelList.size()];
        returnArray = starModelList.toArray(returnArray);
        return returnArray;
    }

    private static StarModel generateStarModelFromId(String id){
        ServiceLogger.LOGGER.info("Generating single StarModel for star with id: " + id);
        try{
            String query = "SELECT * FROM stars WHERE id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            StarModel returnModel = new StarModel(id, rs.getString("name"), (Integer)rs.getObject("birthYear"));
            return returnModel;
        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty StarModel.");
            return new StarModel();
        }
    }

    private static String[] withTitle(SearchStarRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating list of star id according to search parameters.");
        try {
            List<String> returnIdList = new ArrayList<String>();
            String query = "SELECT DISTINCT stars.id FROM stars JOIN stars_in_movies on stars.id = stars_in_movies.starId " +
                    "JOIN movies on stars_in_movies.movieId = movies.id WHERE name LIKE ? AND title LIKE ?";
            if (requestModel.getBirthYear() != null) {
                query += " AND birthYear = ?";
            }
            query += " ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection();

            if (requestModel.getOrderby().equalsIgnoreCase("name")){
                query += ", birthYear asc";
            }
            if (requestModel.getOrderby().equalsIgnoreCase("birthYear")){
                query += ", name asc";
            }

            query += " LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset() + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            if (requestModel.getName() == null) {
                ps.setString(1, "%");
            } else {
                ps.setString(1, "%" + requestModel.getName() + "%");
            }

            if (requestModel.getMovieTitle() == null) {
                ps.setString(2, "%");
            } else {
                ps.setString(2, "%" + requestModel.getMovieTitle() + "%");
            }

            if (requestModel.getBirthYear() != null) {
                ps.setInt(3, requestModel.getBirthYear());
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
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty array.");
            String[] returnArray = new String[1];
            return returnArray;
        }
    }

    private static String[] noTitle(SearchStarRequestModel requestModel){
        ServiceLogger.LOGGER.info("Generating list of star id according to search parameters.");
        try {
            List<String> returnIdList = new ArrayList<String>();
            String query = "SELECT DISTINCT id FROM stars WHERE name LIKE ?";
            if (requestModel.getBirthYear() != null) {
                query += " AND birthYear = ?";
            }
            query += " ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection();

            if (requestModel.getOrderby().equalsIgnoreCase("name")){
                query += ", birthYear asc";
            }
            if (requestModel.getOrderby().equalsIgnoreCase("birthYear")){
                query += ", name asc";
            }

            query += " LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset() + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            if (requestModel.getName() == null) {
                ps.setString(1, "%");
            } else {
                ps.setString(1, "%" + requestModel.getName() + "%");
            }

            if (requestModel.getBirthYear() != null) {
                ps.setInt(2, requestModel.getBirthYear());
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
            ServiceLogger.LOGGER.warning("Something went wrong. Returning empty array.");
            String[] returnArray = new String[1];
            return returnArray;
        }
    }
}
