package edu.uci.ics.binghal.service.movies.core;
import javax.ws.rs.core.Response.Status;

public class ResultCodeToStatus{
    public static Status convert(int resultCode){
        if (resultCode < 0){
            return Status.BAD_REQUEST;
        }
        return Status.OK;
    }
}