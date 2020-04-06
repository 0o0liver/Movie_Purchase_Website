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
import org.apache.commons.codec.DecoderException;

import edu.uci.ics.binghal.service.idm.IDMService;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;
import edu.uci.ics.binghal.service.idm.models.LoginPageRequestModel;
import edu.uci.ics.binghal.service.idm.models.LoginPageResponseModel;
import edu.uci.ics.binghal.service.idm.security.Crypto;
import edu.uci.ics.binghal.service.idm.security.Session;
import edu.uci.ics.binghal.service.idm.security.Token;
import edu.uci.ics.binghal.service.idm.core.CheckUser;
import edu.uci.ics.binghal.service.idm.core.CheckEmailFormat;

public class Login {
    public static LoginPageResponseModel loginUser(LoginPageRequestModel requestModel) {
        if (requestModel.getPassword() == null || requestModel.getPassword().length == 0) {
            ServiceLogger.LOGGER.info("Password has invalid length.");
            return new LoginPageResponseModel(-12, "Password has invalid length.");
        } else if (requestModel.getEmail() == null || requestModel.getEmail().length() < 1 || requestModel.getEmail().length() > 50 ) {
            ServiceLogger.LOGGER.info("Email has invalid length.");
            return new LoginPageResponseModel(-10, "Email address has invalid length.");
        } else if (!CheckEmailFormat.check(requestModel.getEmail())) {
            ServiceLogger.LOGGER.info("Email has invalid format.");
            return new LoginPageResponseModel(-11, "Email address has invalid format.");
        } else if (CheckUser.userNotFound(requestModel.getEmail())) {
            ServiceLogger.LOGGER.info("User not found.");
            ServiceLogger.LOGGER.info("Zeroing out password.");
            Arrays.fill(requestModel.getPassword(), '0');
            return new LoginPageResponseModel(14, "User not found.");
        } else if (!matchedPassword(requestModel.getEmail(), requestModel.getPassword())){
            ServiceLogger.LOGGER.info("Password do not match.");
            ServiceLogger.LOGGER.info("Zeroing out password.");
            Arrays.fill(requestModel.getPassword(), '0');
            return new LoginPageResponseModel(11, "Passwords do not match.");
        } else if (checkExistActiveSession(requestModel.getEmail())){
            ServiceLogger.LOGGER.info("Login successfully but need revoke session.");
            ServiceLogger.LOGGER.info("Revoking exist session.");
            revokeExistSession(requestModel.getEmail());
            ServiceLogger.LOGGER.info("Generating new session.");
            Session newSession = Session.createSession(requestModel.getEmail());
            ServiceLogger.LOGGER.info("Inserting session to database");
            insertSessionToDB(newSession);
            ServiceLogger.LOGGER.info("Zeroing out password.");
            Arrays.fill(requestModel.getPassword(), '0');
            return new LoginPageResponseModel(120, "User logged in successfully.", newSession.getSessionID().toString());
        }
        ServiceLogger.LOGGER.info("Login successfully.");
        ServiceLogger.LOGGER.info("Generating new session.");
        Session session = Session.createSession(requestModel.getEmail());
        ServiceLogger.LOGGER.info("Inserting new session to database.");
        insertSessionToDB(session);
        ServiceLogger.LOGGER.info("Zeroing out password.");
        Arrays.fill(requestModel.getPassword(), '0');
        return new LoginPageResponseModel(120, "User logged in successfully.", session.getSessionID().toString());
    }

    private static void revokeExistSession(String email){
        try{
            String query = "UPDATE sessions SET status = 4 WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
        }
    }

    private static void insertSessionToDB(Session session){
        try{
            String query = "INSERT INTO sessions (sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, session.getSessionID().toString());
            ps.setString(2, session.getEmail());
            ps.setInt(3, 1);
            ps.setTimestamp(4, session.getTimeCreated());
            ps.setTimestamp(5, session.getLastUsed());
            ps.setTimestamp(6, session.getExprTime());
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
        }
    }

    private static boolean checkExistActiveSession(String email){
        try{
            String query = "SELECT COUNT(*) FROM sessions WHERE email=?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") > 0){
                String query1 = "SELECT status FROM sessions WHERE email=?;";
                PreparedStatement ps1 = IDMService.getCon().prepareStatement(query1);
                ps1.setString(1, email);
                ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
                ResultSet rs1 = ps1.executeQuery();
                while (rs1.next()){
                    if (rs1.getInt("status") == 1){ return true; }
                }
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean matchedPassword(String email, char[] password){
        try{
            String query = "SELECT salt, pword FROM users WHERE email =?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            String saltStr = rs.getString("salt");
            String hashedPassword = rs.getString("pword");
            byte[] salt = convert(saltStr);
            byte[] hashInsertedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
            String hashInsertedPasswordStr = Crypto.getHashedPass(hashInsertedPassword);
            return hashedPassword.equals(hashInsertedPasswordStr);
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed.");
        }
        return false;
    }

    public static Status responseModelToStatus(int resultCode) {
        if (resultCode == -12 || resultCode == -11 || resultCode == -10 || resultCode == -3 || resultCode == -2) {
            return Status.BAD_REQUEST;
        }
        return Status.OK;
    }

    private static byte[] convert(String salt) {
        int len = salt.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(salt.charAt(i), 16) << 4) + Character.digit(salt.charAt(i + 1), 16));
        }
        return data;
    }
}