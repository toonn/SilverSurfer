package unused2;
/*package mq.communicator;

import java.io.IOException;
import java.util.Date;

import unused.MQ;

//import mq.intrfce.Config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class MessageSender {

    private Connection conn = null;

    private Channel channel = null;

    public MessageSender() {

        try {
            conn = MQ.createConnection();
            channel = MQ.createChannel(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public Connection getConn() {
        return conn;
    }
    
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
}*/
