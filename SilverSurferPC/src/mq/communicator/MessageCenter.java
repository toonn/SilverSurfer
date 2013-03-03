package mq.communicator;

import java.io.IOException;
import java.util.Date;

import simulator.pilot.AbstractPilot;
import simulator.pilot.SimulationPilot;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * A MessageCenter takes care of all MQ-related issues for an AbstractPilot.
 * Use the sendMessage(string,string) and subscribeTo(string) methods to do the work for you!
 */
public class MessageCenter {

    public static void main(String[] args) {

        MessageCenter mc = new MessageCenter(new SimulationPilot());

        SubscribeMonitor sm = new SubscribeMonitor("race.*", mc.getChannel(),
                mc.getConn(),mc.getClientPilot());
        sm.start();

        mc.sendMessage(Config.EXCHANGE_NAME, "race.bloop", "chatzerverzz");

    }

    private Connection conn;
    private Channel channel;
    private AbstractPilot clientPilot;

    /**
     * Creates a MessageCenter for a certain AbstractPilot that takes care of channel/connection creation
     * and the actual sending/monitoring for that AbstractPilot. 
     * 
     * @param pilot
     * 			: The Pilot incoming messages should be sent to.
     * @throws NullPointerException
     * 			: if pilot == null.
     */
    public MessageCenter(AbstractPilot pilot) throws NullPointerException{
    	
    	if(pilot == null)
    		throw new NullPointerException("null is not a valid pilot!");
    	else this.clientPilot = pilot;

        try {
            conn = MQ.createConnection();
            channel = MQ.createChannel(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The Channel this MessageSender uses to send messages across.
     */
    public Channel getChannel() {
        return channel;
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
    public AbstractPilot getClientPilot() {
		return clientPilot;
	}

    /**
     * Used to send a message to an exchange with a certain routing key.
     * 
     * @param exchange
     *            : The exchange you're sending to.
     * @param routing_Key
     *            : The routing key to be used.
     * @param message
     *            : The message you want to send.
     */
    public void sendMessage(String exchange, String routingKey, String message) {

        // Create Default sending properties.
        AMQP.BasicProperties props = new AMQP.BasicProperties();
        props.setTimestamp(new Date());
        props.setContentType("text/plain");
        props.setDeliveryMode(1);

        try {
            getChannel().basicPublish(exchange, routingKey, props,
                    message.getBytes());
        } catch (IOException e) {
            System.out
                    .println("MessageSender: There was a problem sending a message.");
            e.printStackTrace();
        }

    }

    /**
     * Subscribe to a specified queue in the exchange that's in the Config
     * class. Needs to be completed
     * 
     * @param monitor_key
     *            : The queue you want to monitor.
     */
    public void subscribeTo(String monitor_key) {

        // TODO: aanvragende Pilot meegeven?
        SubscribeMonitor sm = new SubscribeMonitor(monitor_key, getChannel(),
                getConn(),getClientPilot());
        sm.start();
        // TODO: kanaal stopzetten?

    }
}
