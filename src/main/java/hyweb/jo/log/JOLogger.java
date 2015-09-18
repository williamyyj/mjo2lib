package hyweb.jo.log;

import hyweb.jo.org.apache.log4j.Level;
import hyweb.jo.org.apache.log4j.LogManager;
import hyweb.jo.org.apache.log4j.Logger;
import hyweb.jo.org.apache.log4j.PropertyConfigurator;
import hyweb.jo.org.apache.log4j.spi.LoggerFactory;

import java.net.URL;

/**
 * @author William
 */
public class JOLogger {

    private static final String FQCN = JOLogger.class.getName();

    private static Logger log = Logger.getLogger(JOLogger.class);

    static {
        URL url = JOLogger.class.getClassLoader().getResource("jo.properties");
        if (url != null) {
            PropertyConfigurator.configure(url);
        }
    }

    public static void info(Object msg) {
        log.log(FQCN, Level.INFO, msg, null);
    }

    public static void info(Object msg, Throwable t) {
        log.log(FQCN, Level.INFO, msg, t);
    }

    public static void error(Object msg) {
        log.log(FQCN, Level.ERROR, msg, null);
    }

    public static void error(Object msg, Throwable t) {
        log.log(FQCN, Level.ERROR, msg, t);
    }

    public static void debug(Object msg) {
        log.log(FQCN, Level.DEBUG, msg, null);
    }

    public static void debug(Object msg, Throwable t) {
        log.log(FQCN, Level.DEBUG, msg, t);
    }

    public static void warn(Object msg) {
        log.log(FQCN, Level.WARN, msg, null);
    }

    public static void warn(Object msg, Throwable t) {
        log.log(FQCN, Level.WARN, msg, t);
    }

    public static void fatal(Object msg, Throwable t) {
        log.log(FQCN, Level.FATAL, msg, t);
    }

    public static void fatal(Object msg) {
        log.log(FQCN, Level.FATAL, msg, null);
    }

    public static
        Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    public static
        Logger getLogger(Class clazz) {
        return LogManager.getLogger(clazz.getName());
    }

    public static
        Logger getRootLogger() {
        return LogManager.getRootLogger();
    }

    public static
        Logger getLogger(String name, LoggerFactory factory) {
        return LogManager.getLogger(name, factory);
    }

}
