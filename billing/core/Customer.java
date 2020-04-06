package edu.uci.ics.binghal.service.billing.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.uci.ics.binghal.service.billing.BillingService;
import java.sql.SQLIntegrityConstraintViolationException;

import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.billing.models.CustomerInsertUpdateRequestModel;
import edu.uci.ics.binghal.service.billing.models.CustomerModel;
import edu.uci.ics.binghal.service.billing.models.CustomerRetrieveResponseModel;
import edu.uci.ics.binghal.service.billing.models.EmailRequestModel;

public class Customer {
    public static GeneralResponseModel insert(CustomerInsertUpdateRequestModel requestModel){
        ServiceLogger.LOGGER.info("Inserting customer: " +  requestModel.getEmail());
        if (requestModel.getCcId().length() < 16 || requestModel.getCcId().length() > 20){
            return new GeneralResponseModel(321, "Credit card ID has invalid length.");
        }
        else if (!requestModel.getCcId().matches("\\d*")){
            return new GeneralResponseModel(322, "Credit card ID has invalid value.");
        }
        else if (ccIdNotFound(requestModel.getCcId())){
            return new GeneralResponseModel(331, "Credit card ID not found.");
        }
        try{
            String query = "insert into customers (email, firstName, lastName, ccId, address) values (?, ?, ?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ps.setString(4, requestModel.getCcId());
            ps.setString(5, requestModel.getAddress());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ps.executeUpdate();
            return new GeneralResponseModel(3300, "Customer inserted successfully.");
        } catch (SQLIntegrityConstraintViolationException e){
            return new GeneralResponseModel(333, "Duplicate insertion.");
        } catch (SQLException e) {
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static GeneralResponseModel update(CustomerInsertUpdateRequestModel requestModel){
        ServiceLogger.LOGGER.info("Updating customer: " +  requestModel.getEmail());
        if (requestModel.getCcId().length() < 16 || requestModel.getCcId().length() > 20){
            return new GeneralResponseModel(321, "Credit card ID has invalid length.");
        }
        else if (!requestModel.getCcId().matches("\\d*")){
            return new GeneralResponseModel(322, "Credit card ID has invalid value.");
        }
        else if (ccIdNotFound(requestModel.getCcId())){
            return new GeneralResponseModel(331, "Credit card ID not found.");
        }
        try{
            String query = "update customers set firstName = ?, lastName = ?, ccId = ?, address = ? where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getFirstName());
            ps.setString(2, requestModel.getLastName());
            ps.setString(3, requestModel.getCcId());
            ps.setString(4, requestModel.getAddress());
            ps.setString(5, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            int rowAffect = ps.executeUpdate();
            if (rowAffect == 0){
                return new GeneralResponseModel(332, "Customer does not exist.");
            }
            return new GeneralResponseModel(3310, "Customer updated successfully.");
        } catch (SQLException e) {
            return new GeneralResponseModel(-1, "Internal Server Error.");
        }
    }

    public static CustomerRetrieveResponseModel retrieve(EmailRequestModel requestModel){
        ServiceLogger.LOGGER.info("Retrieving customer: " +  requestModel.getEmail());
        try{
            CustomerModel customerModel;
            String query = "select * from customers where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                return new CustomerRetrieveResponseModel(332, "Customer does not exist.", null);
            }
            customerModel = new CustomerModel(
                    rs.getString("email"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("ccId"),
                    rs.getString("address"));
            return new CustomerRetrieveResponseModel(3320, "Customer retrieved successfully.", customerModel);
        } catch (SQLException e){
            return new CustomerRetrieveResponseModel(-1, "Internal Server Error", null);
        }
    }

    private static boolean ccIdNotFound(String ccId){
        ServiceLogger.LOGGER.info("Checking if " + ccId + "exist in database.");
        try{
            String query = "select COUNT(*) from creditcards where id = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, ccId);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") == 0){
                return true;
            }
            return false;
        } catch (SQLException e) {
            return true;
        }
    }
}
