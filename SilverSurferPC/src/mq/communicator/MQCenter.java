package mq.communicator;

import java.io.IOException;

import peno.htttp.Callback;
import peno.htttp.PlayerClient;
import simulator.pilot.AbstractPilot;
import simulator.viewport.SimulatorPanel;

import com.rabbitmq.client.Connection;

/**
 * A MessageCenter takes care of some MQ-related issues for an AbstractPilot.
 */
public class MQCenter {

    private Connection connection;
    private AbstractPilot pilot;
    private APHandler handler;
    private PlayerClient client;

    private String gameID = "ABC";
    private String playerID;

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
    public MQCenter(AbstractPilot pilot, String playerID, SimulatorPanel panel) throws IllegalArgumentException {
        if (pilot == null)
            throw new IllegalArgumentException("Null is not a valid pilot!");
        this.playerID = playerID;
        this.pilot = pilot;
        handler = new APHandler(pilot, panel);
        try {
        	connection = MQ.createConnection();
        	client = new PlayerClient(connection, handler, gameID, playerID);
        } catch (IOException e) {
        	System.out.println("There was a problem setting up the connection or the htttp-client.");
        	e.printStackTrace();
        }
    }

    public PlayerClient getClient() {
        return client;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getGameID() {
        return gameID;
    }

    public APHandler getHandler() {
        return handler;
    }
    
    public AbstractPilot getPilot() {
        return pilot;
    }

    public String getPlayerID() {
        return playerID;
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
        client.join(stdCallback());
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
    public void updatePosition(int x, int y, int angle) throws IllegalStateException, IOException {
        client.updatePosition(x, y, angle);
    }

    /**
     * @return The standard Callback used to join a game. For usage: see the
     *         join() void.
     */
    private Callback<Void> stdCallback() {
        return new Callback<Void>() {
            @Override
            public void onFailure(Throwable t) {
                System.err.println("[HTTTP] Error connecting: " + t.getMessage());
                System.err.println("[HTTTP] Retry ...");
                try {
                    client.join(stdCallback());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(Void result) {
                System.out.println("[HTTTP] Succesfully connected. Your player ID is " + getPlayerID() + ".");
            }
        };
    }
}