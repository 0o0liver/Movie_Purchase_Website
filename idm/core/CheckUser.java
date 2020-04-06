package edu.uci.ics.binghal.service.idm.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.uci.ics.binghal.service.idm.IDMService;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;

public class CheckUser{
    public static boolean userNotFound(String email){
        ServiceLogger.LOGGER.info("Checking if user exist in the database with given email " + email);
        try{
            String query = "SELECT COUNT(*) FROM users WHERE email=?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("COUNT(*)") == 0;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Query failed.");
            return true;
        }
    }
}