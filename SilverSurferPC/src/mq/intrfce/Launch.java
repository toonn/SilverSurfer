package mq.intrfce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * A program that uses a AMQP server to send a start signal for a race.
 * 
 * @author bart.vanbrabant@cs.kuleuven.be
 * 
 */
public class Launch {
    public static void main(String[] args) {
        try {
            Connection conn = MQ.createConnection();
            Channel channel = MQ.createChannel(conn);

            BufferedReader stdin = new BufferedReader(new InputStreamReader(
                    System.in));

            System.out
                    .println("Connected to server. Commands: exit, start, stop");

            boolean running = true;
            while (running) {
                System.out.print("> ");
                String line = stdin.readLine();

                if (line == null) {
                    System.out.println("");
                    System.exit(1);
                }

                line = line.trim();

                String message = null;
                if (line.equals("exit")) {
                    running = false;
                } else if (line.equals("start")) {
                    message = "start";
                } else if (line.equals("stop")) {
                    message = "stop";
                } else {
                    System.err.println("Only start or stop are valid messages");
                }

                if (message != null) {
                    AMQP.BasicProperties props = new AMQP.BasicProperties();
                    props.setTimestamp(new Date());
                    props.setContentType("text/plain");
                    props.setDeliveryMode(1);

                    channel.basicPublish(Config.EXCHANGE_NAME,
                            Config.LAUNCH_ROUTING_KEY, props,
                            message.getBytes());
                    System.out.println(String.format(
                            "Send message '%s' to exchange '%s' with key '%s'",
                            message, Config.EXCHANGE_NAME,
                            Config.LAUNCH_ROUTING_KEY));
                }
            }

            channel.close();
            conn.close();
        } catch (IOException e) {
            System.err.println("Unable to connect to AMQP server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
