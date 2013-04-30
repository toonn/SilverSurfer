package mq.communicator;

/**
 * An interface that contains some configuration data for the examples.
 */
public interface Config {
    public static final String USER_NAME = "guest";
    public static final String PASSWORD = "guest";
    public static final String VIRTUAL_HOST = "/";
    public static final String HOST_NAME = "localhost";
    public static final int PORT = 5672;

    //The default exchange that will be used for races during the official demo's
    public static final String EXCHANGE_NAME = "ABC";
    public static final String LAUNCH_ROUTING_KEY = "race.launch";
}