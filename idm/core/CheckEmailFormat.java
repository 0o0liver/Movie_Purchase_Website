package edu.uci.ics.binghal.service.idm.core;
//import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;


public class CheckEmailFormat{
    public static boolean check(String email){
        ServiceLogger.LOGGER.info("Checking if given email: " + email + " has a valid format.");
        String emailFormat = "^(.+)@(.+)\\.(.+)$";
        Pattern pat = Pattern.compile(emailFormat);
        return pat.matcher(email).matches();
    }
}