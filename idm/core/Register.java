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
import edu.uci.ics.binghal.service.idm.models.RegisterPageRequestModel;
import edu.uci.ics.binghal.service.idm.models.RegisterPageResponseModel;
import edu.uci.ics.binghal.service.idm.core.CheckEmailFormat;
import edu.uci.ics.binghal.service.idm.security.Crypto;

public class Register{
    public static RegisterPageResponseModel registerUser(RegisterPageRequestModel requestModel){
        if (requestModel.getPassword() == null || requestModel.getPassword().length == 0){
            ServiceLogger.LOGGER.info("Password has invalid length.");
            return new RegisterPageResponseModel(-12, "Password has invalid length.");
        }
        else if (requestModel.getEmail() == null || requestModel.getEmail().length() < 1 || requestModel.getEmail().length() > 50 ){
            ServiceLogger.LOGGER.info("Email address has invalid length.");
            return new RegisterPageResponseModel(-10, "Email address has invalid length.");
        }
        else if (requestModel.getPassword().length < 7 || requestModel.getPassword().length > 16){
            ServiceLogger.LOGGER.info("Password does not meet length requirements.");
            return new RegisterPageResponseModel(12, "Password does not meet length requirements.");
        }
        else if (!CheckEmailFormat.check(requestModel.getEmail())){
            ServiceLogger.LOGGER.info("Email address has invalid format.");
            return new RegisterPageResponseModel(-11, "Email address has invalid format.");
        }
        else if (!checkPasswordRequirement(requestModel.getPassword())){
            ServiceLogger.LOGGER.info("Password does not meet character requirements.");
            return new RegisterPageResponseModel(13, "Password does not meet character requirements.");
        }
        else if(checkEmailInUse(requestModel.getEmail())){
            ServiceLogger.LOGGER.info("Email already in use.");
            return new RegisterPageResponseModel(16, "Email already in use.");
        }
        ServiceLogger.LOGGER.info("Inserting user to database.");
        insertUserToDB(requestModel.getEmail(), requestModel.getPassword());
        ServiceLogger.LOGGER.info("Inserted successfully.");
        ServiceLogger.LOGGER.info("Zeroing out passwords.");
        Arrays.fill(requestModel.getPassword(), '0');
        return new RegisterPageResponseModel(110, "User registered successfully.");
    }

    public static Status responseModelToStatus(int resultCode){
        if (resultCode == -12 || resultCode == -11 || resultCode == -10 || resultCode == -3 || resultCode == -2){
            return Status.BAD_REQUEST;
        }
        return Status.OK;
    }

    private static boolean insertUserToDB(String email, char[] password){
        ServiceLogger.LOGGER.info("Generating salt.");
        byte[] salt = Crypto.genSalt();
        ServiceLogger.LOGGER.info("Generating hashed passwords.");
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
        String hashedPasswordStr = Crypto.getHashedPass(hashedPassword);
        String saltStr = Crypto.getHashedPass(salt);

        try{
            String query = "INSERT INTO users (email, status, plevel, salt, pword) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setInt(2, 1);
            ps.setInt(3, 5);
            ps.setString(4, saltStr);
            ps.setString(5, hashedPasswordStr);
            ServiceLogger.LOGGER.info("Execeuting query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Executed successfully");
            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed.");
        }
        return false;
    }

    private static boolean checkEmailInUse(String email){
        try{
            String query = "SELECT COUNT(*) FROM users WHERE email=?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Executed successfully.");
            rs.next();
            return rs.getInt("COUNT(*)") > 0;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean checkPasswordRequirement(char[] password){
        boolean containLowerCase = false;
        boolean containUpperCase = false;
        boolean containNumber = false;
        boolean containSpecialChar = false;

        ServiceLogger.LOGGER.info("Checking if password contain lower case.");
        for (int i = 0; i < password.length; ++i){
            if ((int)password[i] >= 97 && (int)password[i] <=122){ containLowerCase = true; }
        }
        ServiceLogger.LOGGER.info("Checking if password contain upper case.");
        for (int i = 0; i < password.length; ++i){
            if ((int)password[i] >= 65 && (int)password[i] <= 90){ containUpperCase = true; }
        }
        ServiceLogger.LOGGER.info("Checking if password contain number.");
        for (int i = 0; i < password.length; ++i){
            if ((int)password[i] >=48 && (int)password[i] <= 57){ containNumber = true; }
        }
        ServiceLogger.LOGGER.info("Checking if password contain special chars.");
        for (int i = 0; i < password.length; ++i){
            if (((int)password[i]>=32&&(int)password[i]<=47)|| ((int)password[i]>=91&&(int)password[i]<=96) || ((int)password[i]>=123&&(int)password[i]<=126) || ((int)password[i]>=58&&(int)password[i]<=64)){
                containSpecialChar = true; }
        }
        if (containLowerCase && containUpperCase && containNumber && containSpecialChar){
            return true;
        }
        return false;
    }

}