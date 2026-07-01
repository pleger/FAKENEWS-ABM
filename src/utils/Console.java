package utils;

import inputManager.Configuration;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Console {
    private static Logger logger = null;
    private static final String FILE_NAME = "output.log";

    private static void initLoggerRequired() {
        if (logger == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
            logger = Logger.getAnonymousLogger();
            logger.setLevel(Level.INFO);
            setLogFile();
        }
    }

    private static void setLogFile() {
        if (Configuration.OUTPUT_DIRECTORY == null || Configuration.OUTPUT_DIRECTORY.trim().isEmpty()) {
            return;
        }

        try {
            FileHandler fh = new FileHandler(Configuration.OUTPUT_DIRECTORY+"/"+ FILE_NAME);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (Exception e) {
            System.err.println("ERROR: Console.setLogFile: output.log could not be created: " + e);
        }
    }

    public static void resetLogFile() {
        if (logger != null) {
            for (java.util.logging.Handler handler : logger.getHandlers()) {
                handler.close();
                logger.removeHandler(handler);
            }
            logger = null;
        }
    }

    public static void end(Object msg) {
        info(msg);
        logger.getHandlers()[0].close();
    }

    public static void debug(Object msg) {
        initLoggerRequired();
        logger.fine(msg.toString());
    }

    public static void info(Object msg) {
        initLoggerRequired();
        logger.info(msg.toString());
    }

    public static void error(Object msg) {
        initLoggerRequired();
        logger.severe(msg.toString());
    }

    public static void error(Object msg, Throwable throwable) {
        initLoggerRequired();
        logger.log(Level.SEVERE, msg.toString(), throwable);
    }

    public static void warn(Object msg) {
        initLoggerRequired();
        logger.warning(msg.toString());
    }

    public static void setAssert(boolean assertion, Object msg) {
        initLoggerRequired();
        if (!assertion) error(msg.toString());
    }
}
