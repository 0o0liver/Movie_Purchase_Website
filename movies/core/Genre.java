package edu.uci.ics.binghal.service.movies.core;

import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.models.AddGenreRequestModel;
import edu.uci.ics.binghal.service.movies.models.GenreModel;
import edu.uci.ics.binghal.service.movies.models.GetGenreResponseModel;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Genre {
    public static GetGenreResponseModel retrieve(){
        GenreModel[] genreModelList = generateGenreModelList();
        return new GetGenreResponseModel(219, "Genres successfully retrieved.", genreModelList);
    }

    public static VerifyPrivilegeResponseModel add(AddGenreRequestModel requestModel){
        if (addGenre(requestModel.getName())){
            return new VerifyPrivilegeResponseModel(217, "Genre successfully added.");
        }
        return new VerifyPrivilegeResponseModel(218, "Genre could not be added.");
    }

    public static GetGenreResponseModel getMovieGenre(String movieid){
        GenreModel[] genreModelList = generateGenreModelFromID(movieid);
        return new GetGenreResponseModel(219, "Genres successfully retrieved.", genreModelList);
    }

    private static GenreModel[] generateGenreModelFromID(String movieid){
        try{
            List<GenreModel> arrayList = new ArrayList<GenreModel>();
            String query = "select id, name from genres_in_movies join genres on genres_in_movies.genreId = genres.id where movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                arrayList.add(new GenreModel(rs.getInt("id"), rs.getString("name")));
            }
            GenreModel[] ret = new GenreModel[arrayList.size()];
            ret = arrayList.toArray(ret);
            return ret;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static boolean addGenre(String name){
        try{
            if (genreAlreadyExist(name)){
                return false;
            }
            String query = "insert into genres (name) value (?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean genreAlreadyExist(String name){
        try{
            String query = "select COUNT(*) from genres where name = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);
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

    private static GenreModel[] generateGenreModelList(){
        try{
            List<GenreModel> retArraylList = new ArrayList<GenreModel>();
            String query = "Select * from genres;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                retArraylList.add(new GenreModel(rs.getInt("id"), rs.getString("name")));
            }
            GenreModel[] ret = new GenreModel[(retArraylList.size())];
            ret = retArraylList.toArray(ret);
            return ret;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
