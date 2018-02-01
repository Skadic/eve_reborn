package skadic.commands.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class CommandLog {

    private static final Logger LOGGER = LoggerFactory.getLogger("Commands");
    private static final Marker TAG = MarkerFactory.getMarker("-COMMANDS-");


    public static void d(String message){
        LOGGER.debug(TAG, message);
    }

    public static void e(String message){
        LOGGER.error(TAG, message);
    }

    public static void e(String message, Throwable t){
        LOGGER.error(TAG, message, t);
    }
    public static void e(Throwable t){
        LOGGER.error(TAG, "", t);
    }

    public static void i(String message){
        LOGGER.info(TAG, message);
    }

    public static void t(String message){
        LOGGER.trace(TAG, message);
    }

    public static void w(String message){
        LOGGER.warn(TAG, message);
    }
}
