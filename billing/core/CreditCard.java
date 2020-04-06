package edu.uci.ics.binghal.service.billing.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;
import edu.uci.ics.binghal.service.billing.BillingService;
import java.sql.SQLIntegrityConstraintViolationException;

import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.billing.models.CreditCardInsertUpdateRequestModel;
import edu.uci.ics.binghal.service.billing.models.IdRequestModel;
import edu.uci.ics.binghal.service.billing.models.CreditCardModel;
import edu.uci.ics.binghal.service.billing.models.CreditCardRetrieveResponseModel;


public class CreditCard {
    public static GeneralResponseModel insert(CreditCardInsertUpdateRequestModel requestModel){
        ServiceLogger.LOGGER.info("Inserting credit card: " + requestModel.getId());
        java.util.Date currentDate = new java.util.Date();
        if (requestModel.getId().length() < 16 || requestModel.getId().length() > 20){
            return new GeneralResponseModel(321, "Credit card ID has invalid length.");
        }
        else if (!requestModel.getId().matches("\\d*")){
            return new GeneralResponseModel(322, "Credit card ID has invalid value.");
        }
        else if (requestModel.getExpiration().before(currentDate)){
            return new GeneralResponseModel(323, "expiration has invalid value.");
        }
        try{
            String query = "insert into creditcards (id, firstName, lastName, expiration) values (?, ?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            java.sql.Date sqlDate = new java.sql.Date(requestModel.getExpiration().getTime());
            ps.setString(1, requestModel.getId());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ps.setDate(4, sqlDate);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ps.executeUpdate();
            return new GeneralResponseModel(3200, "Credit card inserted successfully.");
        } catch (SQLIntegrityConstraintViolationException e){
            return new GeneralResponseModel(325, "Duplicate insertion.");
        } catch (SQLException e) {
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static GeneralResponseModel update(CreditCardInsertUpdateRequestModel requestModel){
        ServiceLogger.LOGGER.info("Updating credit card: " + requestModel.getId());
        java.util.Date currentDate = new java.util.Date();
        if (requestModel.getId().length() < 16 || requestModel.getId().length() > 20){
            return new GeneralResponseModel(321, "Credit card ID has invalid length.");
        }
        else if (!requestModel.getId().matches("\\d*")){
            return new GeneralResponseModel(322, "Credit card ID has invalid value.");
        }
        else if (requestModel.getExpiration().before(currentDate)){
            return new GeneralResponseModel(323, "expiration has invalid value.");
        }
        try{
            String query = "update creditcards set firstName = ?, lastName = ?, expiration = ? where id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            java.sql.Date sqlDate = new java.sql.Date(requestModel.getExpiration().getTime());
            ps.setString(1, requestModel.getFirstName());
            ps.setString(2, requestModel.getLastName());
            ps.setDate(3, sqlDate);
            ps.setString(4, requestModel.getId());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            int rowAffected = ps.executeUpdate();
            if (rowAffected == 0){
                return new GeneralResponseModel(324, "Credit card does not exist.");
            }
            return new GeneralResponseModel(3210, "Credit card updated successfully.");
        } catch (SQLException e){
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static GeneralResponseModel delete(IdRequestModel requestModel){
        ServiceLogger.LOGGER.info("Deleting credit card: " + requestModel.getId());
        if (requestModel.getId().length() < 16 || requestModel.getId().length() > 20){
            return new GeneralResponseModel(321, "Credit card ID has invalid length.");
        }
        else if (!requestModel.getId().matches("\\d*")){
            return new GeneralResponseModel(322, "Credit card ID has invalid value.");
        }
        try{
            String query = "delete from creditcards where id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            int rowAffected = ps.executeUpdate();
            if (rowAffected == 0){
                return new GeneralResponseModel(324, "Credit card does not exist.");
            }
            return new GeneralResponseModel(3220, "Credit card deleted successfully.");
        } catch (SQLException e){
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static CreditCardRetrieveResponseModel retrieve(IdRequestModel requestModel){
        ServiceLogger.LOGGER.info("Retrieving credit card: " + requestModel.getId());
        if (requestModel.getId().length() < 16 || requestModel.getId().length() > 20){
            return new CreditCardRetrieveResponseModel(321, "Credit card ID has invalid length.", null);
        }
        else if (!requestModel.getId().matches("\\d*")){
            return new CreditCardRetrieveResponseModel(322, "Credit card ID has invalid value.", null);
        }
        try{
            CreditCardModel creditCardModel;
            String query = "select * from creditcards where id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                return new CreditCardRetrieveResponseModel(324, "Credit card does not exist.", null);
            }
            creditCardModel = new CreditCardModel(
                    rs.getString("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getDate("expiration"));

            return new CreditCardRetrieveResponseModel(3230, "Credit card retrieved successfully.", creditCardModel);
        } catch (SQLException e){
            return new CreditCardRetrieveResponseModel(-1, "Internal Server Error", null);
        }
    }
}
