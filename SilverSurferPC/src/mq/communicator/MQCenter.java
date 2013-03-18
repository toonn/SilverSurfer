package mq.communicator;

import java.io.IOException;
import java.util.Random;

import peno.htttp.Callback;
import peno.htttp.Client;

import simulator.pilot.AbstractPilot;

import com.rabbitmq.client.Connection;

/**
 * A MessageCenter takes care of some MQ-related issues for an AbstractPilot.
 */
public class MQCenter {

   
    private Connection conn;
    private AbstractPilot pilot;
    private RobotHandler handler;
    private Client client;
    
    private String gameID = "ZilverTreasureTrek";
    private String playerID;
    
    /**
     * Creates a MQcenter for a certain AbstractPilot that takes care of channel/connection creation
     * and the actual sending/monitoring for that AbstractPilot. 
     * 
     * @param pilot
     * 			: The Pilot incoming messages should be sent to.
     * @param playerID
     * 			: The playerID that will be used in the game.
     * 
     * @note  A random number between 0 and 99999 will be added to the playerID to ensure unique names.
     * 
     * @throws NullPointerException
     * 			: if pilot == null.
     */
    public MQCenter(AbstractPilot pilot, String playerID) throws NullPointerException{
    	
    	Random random = new Random();
    	this.playerID = playerID+random.nextInt(99999);
    	
    	if(pilot == null)
    		throw new NullPointerException("null is not a valid pilot!");
    	else{
    		
    		this.pilot = pilot;
    		handler = new RobotHandler(pilot);

	        try {
	            conn = MQ.createConnection();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        try {
				client = new Client(conn, handler, gameID, playerID);
			} catch (IOException e) {
				System.out.println("There was a problem setting up the htttp-client.");
				e.printStackTrace();
			}
    	}
    }


    /**
     * @return The Connection this MessageCenter uses to send messages across.
     */
    public Connection getConn() {
        return conn;
    }
    
    /**
     * @return	The abstract Pilot this MessageCenter is working for.
     */
    public AbstractPilot getPilot() {
		return pilot;
	}
    
    public String getPlayerID() {
		return playerID;
	}
    
    public RobotHandler getHandler() {
		return handler;
	}
    
    public String getGameID() {
		return gameID;
	}
    
    /**
     * Use to join the game. Be aware: joining the game is not enough. Numbers will be rolled,
     * and you'll have to do some work before being able to join.
     * check https://github.com/tgoossens/htttp-peno/pull/15
     * 
     * @throws IllegalStateException
     * 				:	When this client is already connected to a game.
     * @throws IOException	
     * 				:	Problems connecting.
     */
    public void join() throws IllegalStateException, IOException{
    	client.join(stdCallback());	
	}
    
    /**
     * This void starts the game. 
     * 
     * @throws IOException 
     * 				:	when a connection-error occurs.
     * @throws IllegalStateException 
     * 				:	when the pre-conditions haven't been met.
     * 
     * @pre	: use join() first
     * 		: wait for your player number before starting.
     *		: position your robot before starting.
     *		: setReady(true)
     *
     */
    public void start() throws IllegalStateException, IOException{
    	if (client.canStart())
			client.start();
    }
    
    /**
     * Notify the client that you're ready to play.
     * @param ready
     * 			: the readiness-state you're in.
     * @throws IOException
     * 				: When
     */
    public void setReady(boolean ready) throws IOException{
    	if(client.canStart())
    		client.setReady(true);
    }
    
    /**
     * Signal the fact that you found your object.
     * 
     * @throws IOException 
     * 				: connection error.
     * @throws IllegalStateException 
     * 				: when no game is started etc.
     */
    public void foundObject() throws IllegalStateException, IOException{
    	client.foundObject();
    }
    
    /**
     * Notify other players of your changed position.
     * 
     * @throws IOException 
     * 				: connection error.
     * @throws IllegalStateException 
     * 				: when no game is started etc.
     */   
    public void updatePosition(int x, int y, int angle) throws IllegalStateException, IOException{
    	client.updatePosition(x, y, angle);
    }
    
    /**
     * @return	The standard Callback used to join a game.
     * 			For usage: see the join() void.
     */
    private Callback<Void> stdCallback(){
    	return new Callback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// TODO Succesvolle deelname
				System.out.println("Succesvolle deelname door "+getPlayerID());
			}

			@Override
			public void onFailure(Throwable t) {
				// TODO Oeps, er liep iets fout
				// Laat de gebruiker iets weten
				System.err.println("Fout bij deelname: " + t.getMessage());
			}
		};
    }
    
    


}
