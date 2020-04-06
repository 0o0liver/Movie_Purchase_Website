package edu.uci.ics.binghal.service.billing.core;

import java.util.regex.Pattern;
import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;

public class CheckEmail {
    public static boolean checkFormat(String email){
        ServiceLogger.LOGGER.info("Checking if given email: \"" + email + "\" has a valid format.");
        String emailFormat = "^(.+)@(.+)\\.(.+)$";
        Pattern pat = Pattern.compile(emailFormat);
        return pat.matcher(email).matches();
    }

    public static boolean checkLength(String email){
        ServiceLogger.LOGGER.info("Checking if given email: \"" + email + "\" has valid length.");
        if (email == null || email.length() < 1 || email.length() > 50){
            return false;
        }
        return true;
    }
}
