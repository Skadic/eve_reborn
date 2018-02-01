package skadic.eve.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import skadic.eve.Eve;

public class EveLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(Eve.class);
    private static final Marker TAG = MarkerFactory.getMarker("-EVE-");


    public static void debug(String message){
        LOGGER.debug(TAG, message);
    }

    public static void error(String message){
        LOGGER.error(TAG, message);
    }

    public static void error(String message, Throwable t){
        LOGGER.error(TAG, message, t);
    }

    public static void error(Throwable t){
        LOGGER.error(TAG, "", t);
    }

    public static void info(String message){
        LOGGER.info(TAG, message);
    }

    public static void trace(String message){
        LOGGER.trace(TAG, message);
    }

    public static void warn(String message){
        LOGGER.warn(TAG, message);
    }

}
