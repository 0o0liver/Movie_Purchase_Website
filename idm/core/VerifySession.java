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
import java.sql.Timestamp;

import edu.uci.ics.binghal.service.idm.IDMService;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;
import edu.uci.ics.binghal.service.idm.models.SessionPageRequestModel;
import edu.uci.ics.binghal.service.idm.models.SessionPageResponseModel;
import edu.uci.ics.binghal.service.idm.security.Crypto;
import edu.uci.ics.binghal.service.idm.security.Session;
import edu.uci.ics.binghal.service.idm.security.Token;
import edu.uci.ics.binghal.service.idm.core.CheckUser;
import edu.uci.ics.binghal.service.idm.core.CheckEmailFormat;

public class VerifySession {
    public static SessionPageResponseModel verify(SessionPageRequestModel requestModel){
        if (requestModel.getSessionID().length() != 128){
            ServiceLogger.LOGGER.info("Token has invalid length.");
            return new SessionPageResponseModel(-13, "Token has invalid length.");
        }
        else if (requestModel.getEmail() == null || requestModel.getEmail().length() < 1 || requestModel.getEmail().length() > 50 ){
            ServiceLogger.LOGGER.info("Email has invalid length.");
            return new SessionPageResponseModel(-10, "Email address has invalid length.");
        }
        else if (!CheckEmailFormat.check(requestModel.getEmail())){
            ServiceLogger.LOGGER.info("Email has invalid format.");
            return new SessionPageResponseModel(-11, "Email address has invalid format.");
        }
        else if (CheckUser.userNotFound(requestModel.getEmail())) {
            ServiceLogger.LOGGER.info("User not found.");
            return new SessionPageResponseModel(14, "User not found.");
        }
        else if (sessionNotFound(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session not found.");
            return new SessionPageResponseModel(134, "Session not found.");
        }
        else if (sessionExpired(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session is expired.");
            return new SessionPageResponseModel(131, "Session is expired.");
        }
        else if (sessionClosed(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session is closed.");
            return new SessionPageResponseModel(132, "Session is closed.");
        }
        else if (sessionRevoked(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session is revoked.");
            return new SessionPageResponseModel(133, "Session is revoked.");
        }
        else if (sessionShouldBeExpired(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session should be expired, session status changed.");
            updateSessionStatus(requestModel.getSessionID(), Session.EXPIRED);
            return new SessionPageResponseModel(131, "Session is expired.");
        }
        else if (sessionShouldBeRevoked(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session should be revoked, session status changed.");
            updateSessionStatus(requestModel.getSessionID(), Session.REVOKED);
            return new SessionPageResponseModel(133, "Session is revoked.");
        }
        else if (sessionShouldBeRebuilded(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session should be rebuilded, new session generated, old session status changed.");
            updateSessionStatus(requestModel.getSessionID(), Session.REVOKED);
            Session newSession = Session.createSession(requestModel.getEmail());
            insertSessionToDB(newSession);
            return new SessionPageResponseModel(130, "Session is active.", newSession.getSessionID().toString());
        }
        else if (sessionShouldBeActive(requestModel.getSessionID())){
            ServiceLogger.LOGGER.info("Session is still active and valid, lastUsed updated.");
            updateLastUsed(requestModel.getSessionID());
            return new SessionPageResponseModel(130, "Session is active.", requestModel.getSessionID());
        }
        return new SessionPageResponseModel(999, "This model should not show up", "testSessionID");
    }

    private static void updateSessionStatus(String sessionID, int sessionStatus){
        try{
            String query = "UPDATE sessions SET status = ? WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1, sessionStatus);
            ps.setString(2, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed.");
        }
    }

    private static void updateLastUsed(String sessionID){
        try{
            String query = "UPDATE sessions SET lastUsed = ? WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            Timestamp newLastUsed = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(1, newLastUsed);
            ps.setString(2, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed.");
        }
    }

    private static boolean sessionShouldBeRebuilded(String sessionID){
        ServiceLogger.LOGGER.info("Checking if session need to be rebuilded.");
        try{
            String query = "SELECT * FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (System.currentTimeMillis() < (rs.getTimestamp("lastUsed").getTime() + Session.SESSION_TIMEOUT) && (System.currentTimeMillis() + Session.SESSION_TIMEOUT) > rs.getTimestamp("exprTime").getTime()){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean sessionShouldBeActive(String sessionID){
        ServiceLogger.LOGGER.info("Checking if session should be active.");
        try{
            String query = "SELECT * FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (System.currentTimeMillis() < (rs.getTimestamp("lastUsed").getTime() + Session.SESSION_TIMEOUT)){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean sessionShouldBeExpired(String sessionID){
        ServiceLogger.LOGGER.info("Checking if session need to be expired.");
        try{
            String query = "SELECT * FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query" + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (System.currentTimeMillis() >= rs.getTimestamp("exprTime").getTime()){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean sessionShouldBeRevoked(String sessionID){
        ServiceLogger.LOGGER.info("Checking if session need to be revoked.");
        try{
            String query = "SELECT * FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (System.currentTimeMillis() > (rs.getTimestamp("lastUsed").getTime() + Session.SESSION_TIMEOUT) && System.currentTimeMillis() < rs.getTimestamp("exprTime").getTime()){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return false;
        }
    }

    private static boolean sessionExpired(String sessionID) {
        ServiceLogger.LOGGER.info("Checking if a session is already expired.");
        try{
            String query = "SELECT status FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("status") == 3){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return true;
        }
    }

    private static boolean sessionClosed(String sessionID) {
        ServiceLogger.LOGGER.info("Checking if a session is already closed.");
        try{
            String query = "SELECT status FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("status") == 2){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return true;
        }
    }

    private static boolean sessionRevoked(String sessionID) {
        ServiceLogger.LOGGER.info("Checking if a session is already revoked.");
        try{
            String query = "SELECT status FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query" + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("status") == 4){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed");
            return true;
        }
    }

    private static boolean sessionNotFound(String sessionID) {
        ServiceLogger.LOGGER.info("Checking if session exist in the database.");
        try{
            String query = "SELECT COUNT(*) FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") == 0){
                return true;
            }
            return false;
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed.");
            return true;
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
}