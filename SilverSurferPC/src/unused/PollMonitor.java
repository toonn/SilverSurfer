package unused;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

/**
 * A program that monitors "race.*" messages
 * 
 * @author bart.vanbrabant@cs.kuleuven.be
 * 
 */
public class PollMonitor {
    public static final String MONITOR_KEY = "demo.*";

    public static void main(String[] main) {
        try {
            // create connection to the AMQP server and create a channel to the
            // exchange (See Config.EXCHANGE_NAME)
            final Connection conn = MQ.createConnection();
            final Channel channel = MQ.createChannel(conn);

            BufferedReader stdin = new BufferedReader(new InputStreamReader(
                    System.in));

            System.out.println(String.format(
                    "Polling ampq for '%s' every second. Exit with ENTER",
                    MONITOR_KEY));

            // create a queue for this program
            final AMQP.Queue.DeclareOk queue = channel.queueDeclare();
            System.out.println("Create queue " + queue.getQueue());

            // bind the queue to all routing keys that match MONITOR_KEY
            channel.queueBind(queue.getQueue(), Config.EXCHANGE_NAME,
                    MONITOR_KEY);
            System.out
                    .println(String
                            .format("Bound queue %s to all keys that match '%s' on exchange %s",
                                    queue.getQueue(), MONITOR_KEY,
                                    Config.EXCHANGE_NAME));

            // This task is scheduled by a timer to periodically get new
            // messages
            // from the server and print them out
            TimerTask monitorTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        boolean running = true;
                        // keep looping until all message that are available are
                        // retrieved
                        while (running) {
                            boolean noAck = false;
                            // get the messages in our queue and do not
                            // acknowledge the message
                            // until we processed it
                            GetResponse response = channel.basicGet(
                                    queue.getQueue(), noAck);
                            if (response == null) {
                                running = false;
                            } else {
                                byte[] body = response.getBody();

                                // get the delivery tag to ack that we processed
                                // the message successfully
                                long deliveryTag = response.getEnvelope()
                                        .getDeliveryTag();

                                // response.getProps().getTimestamp() contains
                                // the timestamp
                                // that the sender added when the message was
                                // published. This
                                // time is the time on the sender and NOT the
                                // time on the
                                // AMQP server. This implies that clients are
                                // possibly out of
                                // sync!
                                System.out.println(String.format(
                                        "@%d: %s -> %s", response.getProps()
                                                .getTimestamp().getTime(),
                                        response.getEnvelope().getRoutingKey(),
                                        new String(body)));

                                // send an ack to the server so it can remove
                                // the message from
                                // the queue.
                                channel.basicAck(deliveryTag, false);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            };

            // start monitor task
            Timer timer = new Timer();
            timer.schedule(monitorTask, 0, 2000);

            // wait here until a newline is entered
            stdin.readLine();

            // cleanup
            timer.cancel();
            channel.close();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
