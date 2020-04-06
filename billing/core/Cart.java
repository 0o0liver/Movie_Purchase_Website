package edu.uci.ics.binghal.service.billing.core;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.CartDeleteRequestModel;
import edu.uci.ics.binghal.service.billing.BillingService;
import edu.uci.ics.binghal.service.billing.models.CartInsertUpdateRequestModel;
import edu.uci.ics.binghal.service.billing.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.billing.models.CartRetrieveResponseModel;
import edu.uci.ics.binghal.service.billing.models.CartItemModel;
import edu.uci.ics.binghal.service.billing.models.EmailRequestModel;

public class Cart {
    public static GeneralResponseModel insert(CartInsertUpdateRequestModel requestModel){
        ServiceLogger.LOGGER.info("Inserting cart for "+ requestModel.getEmail());
        if (requestModel.getQuantity() <= 0){
            return new GeneralResponseModel(33, "Quantity has invalid value.");
        }
        try {
            String query = "insert into carts (email, movieId, quantity) values (?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ps.setInt(3, requestModel.getQuantity());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ps.executeUpdate();
            return new GeneralResponseModel(3100, "Shopping cart item inserted successfully.");
        } catch (SQLIntegrityConstraintViolationException e){
            return new GeneralResponseModel(311, "Duplicate insertion.");
        } catch (SQLException e){
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static GeneralResponseModel update(CartInsertUpdateRequestModel requestModel){
        ServiceLogger.LOGGER.info("Updating cart for "+ requestModel.getEmail());
        if (requestModel.getQuantity() <= 0){
            return new GeneralResponseModel(33, "Quantity has invalid value.");
        }
        try{
            String query = "update carts set quantity = ? where email = ? and movieId = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setInt(1, requestModel.getQuantity());
            ps.setString(2, requestModel.getEmail());
            ps.setString(3, requestModel.getMovieId());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            int affectedRow = ps.executeUpdate();
            if (affectedRow == 0){
                return new GeneralResponseModel(312, "Shopping item does not exist.");
            }
            return new GeneralResponseModel(3110, "Shopping cart item updated successfully.");
        } catch (SQLException e){
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static GeneralResponseModel delete(CartDeleteRequestModel requestModel){
        ServiceLogger.LOGGER.info("Deleting cart for "+ requestModel.getEmail());
        try{
            String query = "delete from carts where email = ? and movieId = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            int affectedRow = ps.executeUpdate();
            if (affectedRow == 0){
                return new GeneralResponseModel(312, "Shopping item does not exist.");
            }
            return new GeneralResponseModel(3120, "Shopping cart item deleted successfully.");
        } catch (SQLException e){
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static GeneralResponseModel clear(EmailRequestModel requestModel){
        ServiceLogger.LOGGER.info("Clearing cart for "+ requestModel.getEmail());
        try{
            String query = "delete from carts where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ps.executeUpdate();
            return new GeneralResponseModel(3140, "Shopping cart cleared successfully.");
        } catch (SQLException e){
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static CartRetrieveResponseModel retrieve(EmailRequestModel requestModel){
        ServiceLogger.LOGGER.info("Retrieving cart for "+ requestModel.getEmail());
        try{
            List<CartItemModel> itemModelList = new ArrayList<CartItemModel>();
            String query = "select * from carts where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                itemModelList.add(new CartItemModel(
                        rs.getString("email"),
                        rs.getString("movieId"),
                        rs.getInt("quantity")));
            }
            if (itemModelList.size() == 0){
                return new CartRetrieveResponseModel(312, "Shopping item does not exist.", null);
            }
            CartItemModel[] returnArray = new CartItemModel[itemModelList.size()];
            returnArray = itemModelList.toArray(returnArray);
            return new CartRetrieveResponseModel(3130, "Shopping cart retrieved successfully.", returnArray);
        } catch (SQLException e){
            return new CartRetrieveResponseModel(-1,"Internal Server Error.", null);
        }
    }
}
