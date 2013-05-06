package mq.communicator;

import java.io.IOException;

import mapping.MapGraph;

import peno.htttp.Callback;
import peno.htttp.PlayerClient;
import peno.htttp.PlayerDetails;
import peno.htttp.PlayerType;
import peno.htttp.SpectatorClient;
import simulator.pilot.AbstractPilot;
import simulator.pilot.RobotPilot;
import simulator.viewport.SimulatorPanel;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * A MessageCenter takes care of some MQ-related issues for an AbstractPilot.
 */
public class MQCenter {

    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String VIRTUAL_HOST = "/";
    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String GAMEID = "ABCDEFGHIJKLMNOPQ";

    private Connection connection;
    private AbstractPilot pilot;
    private APHandler handler;
    private SHandler sHandler;
    private PlayerClient client;
    private SpectatorClient sClient;
    private PlayerDetails playerDetails;

    /**
     * Creates a MQcenter for a certain AbstractPilot that takes care of
     * channel/connection creation and the actual sending/monitoring for that
     * AbstractPilot.
     * 
     * @param pilot
     *            : The Pilot incoming messages should be sent to.
     * @param playerID
     *            : The playerID that will be used in the game.
     * 
     * @note A random number between 0 and 99999 will be added to the playerID
     *       to ensure unique names.
     * 
     * @throws NullPointerException
     *             : if pilot == null.
     */
    public MQCenter(AbstractPilot pilot, String playerID, SimulatorPanel panel)
            throws IllegalArgumentException {
        if (pilot == null)
            throw new IllegalArgumentException("Null is not a valid pilot!");

        PlayerType type = PlayerType.VIRTUAL;
        if (pilot instanceof RobotPilot)
            type = PlayerType.PHYSICAL;

        this.playerDetails = new PlayerDetails(playerID, type, 15, 20);
        this.pilot = pilot;
        handler = new APHandler(pilot, panel);
        try {
            connection = createConnection();
            client = new PlayerClient(connection, handler, GAMEID,
                    playerDetails);
        } catch (IOException e) {
            System.out
                    .println("There was a problem setting up the connection or the htttp-client.");
            e.printStackTrace();
        }
    }

    public MQCenter(MapGraph mapGraphLoaded) throws IllegalArgumentException {
        sHandler = new SHandler(mapGraphLoaded);
        try {
            connection = createConnection();
            sClient = new SpectatorClient(connection, sHandler, GAMEID);
            sClient.start();
        } catch (IOException e) {
            System.out
                    .println("There was a problem setting up the connection or the htttp-client.");
            e.printStackTrace();
        }
    }

    private static Connection createConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUAL_HOST);
        factory.setRequestedHeartbeat(0);
        factory.setHost(HOST);
        factory.setPort(PORT);

        Connection conn = factory.newConnection();

        return conn;
    }

    public PlayerClient getPlayerClient() {
        return client;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getGameID() {
        return GAMEID;
    }

    public APHandler getHandler() {
        return handler;
    }

    public AbstractPilot getPilot() {
        return pilot;
    }

    public PlayerDetails getPlayerDetails() {
        return playerDetails;
    }

    /**
     * Use to join the game. Be aware: joining the game is not enough. Numbers
     * will be rolled, and you'll have to do some work before being able to
     * join. check https://github.com/tgoossens/htttp-peno/pull/15
     * 
     * @throws IllegalStateException
     *             : When this client is already connected to a game.
     * @throws IOException
     *             : Problems connecting.
     */
    public void join() throws IllegalStateException, IOException {
        client.join(new Callback<Void>() {
            @Override
            public void onFailure(Throwable t) {
                System.err.println("[HTTTP] Error connecting: "
                        + t.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                System.out
                        .println("[HTTTP] Succesfully connected. Your player ID is "
                                + getPlayerDetails().getPlayerID() + ".");
            }
        });
    }

    /**
     * Notify the client that you're ready to play.
     * 
     * @param ready
     *            : the readiness-state you're in.
     * @throws IOException
     *             : When
     */
    public void setReady(boolean ready) throws IOException {
        client.setReady(true);
    }

    /**
     * Signal the fact that you found your object.
     * 
     * @throws IOException
     *             : connection error.
     * @throws IllegalStateException
     *             : when no game is started etc.
     */
    public void foundObject() throws IllegalStateException, IOException {
        client.foundObject();
    }

    /**
     * Notify other players of your changed position.
     * 
     * @throws IOException
     *             : connection error.
     * @throws IllegalStateException
     *             : when no game is started etc.
     */
    public void updatePosition(int x, int y, int angle)
            throws IllegalStateException, IOException {
        client.updatePosition(x, y, angle);
    }
}