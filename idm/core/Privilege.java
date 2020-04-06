package edu.uci.ics.binghal.service.idm.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.core.Response.Status;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import org.apache.commons.codec.binary.Hex;
import edu.uci.ics.binghal.service.idm.IDMService;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;
import edu.uci.ics.binghal.service.idm.models.PrivilegePageRequestModel;
import edu.uci.ics.binghal.service.idm.models.PrivilegePageResponseModel;
import edu.uci.ics.binghal.service.idm.core.CheckEmailFormat;
import edu.uci.ics.binghal.service.idm.core.CheckUser;
import edu.uci.ics.binghal.service.idm.security.Crypto;

public class Privilege{
    public static PrivilegePageResponseModel process(PrivilegePageRequestModel requestModel){
        if (requestModel.getPlevel() < 1 || requestModel.getPlevel() > 5){
            ServiceLogger.LOGGER.info("Privilege level out of valid range.");
            return new PrivilegePageResponseModel(-14, "Privilege level out of valid range.");
        }
        else if (requestModel.getEmail() == null || requestModel.getEmail().length() < 1 || requestModel.getEmail().length() > 50 ){
            ServiceLogger.LOGGER.info("Email has invalid length.");
            return new PrivilegePageResponseModel(-10, "Email address has invalid length.");
        }
        else if (!CheckEmailFormat.check(requestModel.getEmail())){
            ServiceLogger.LOGGER.info("Email has invalid format.");
            return new PrivilegePageResponseModel(-11, "Email address has invalid format.");
        }
        else if (CheckUser.userNotFound(requestModel.getEmail())){
            ServiceLogger.LOGGER.info("User not found.");
            return new PrivilegePageResponseModel(14, "User not found.");
        }
        else if (!sufficientUser(requestModel.getEmail(), requestModel.getPlevel())){
            ServiceLogger.LOGGER.info("User has insufficient plevel.");
            return new PrivilegePageResponseModel(141, "User has insufficient privilege level.");
        }
        ServiceLogger.LOGGER.info("User has sufficient plevel.");
        return new PrivilegePageResponseModel(140, "User has sufficient privilege level.");
    }

    private static boolean sufficientUser(String email, int plevel){
        try{
          String query = "SELECT plevel FROM users WHERE email = ?;";
          PreparedStatement ps = IDMService.getCon().prepareStatement(query);
          ps.setString(1, email);
          ServiceLogger.LOGGER.info("Executing query " + ps.toString());
          ResultSet rs = ps.executeQuery();
          rs.next();
          if (rs.getInt("plevel") <= plevel){
              return true;
          }
          return false;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

}