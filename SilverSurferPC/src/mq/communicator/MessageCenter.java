package mq.communicator;

import java.io.IOException;
import java.util.Date;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class MessageCenter {

    public static void main(String[] args) {

        MessageCenter ms = new MessageCenter();

        SubscribeMonitor sm = new SubscribeMonitor("race.*", ms.getChannel(),
                ms.getConn());
        sm.start();

        ms.sendMessage(Config.EXCHANGE_NAME, "race.bloop", "chatzerverzz");

    }

    private Connection conn = null;

    private Channel channel = null;

    public MessageCenter() {

        try {
            conn = MQ.createConnection();
            channel = MQ.createChannel(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Channel this MessageSender uses to send messages across.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * The Connection this MessageSender uses to send messages across.
     */
    public Connection getConn() {
        return conn;
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
                getConn());
        sm.start();
        // TODO: kanaal stopzetten?

    }
}
