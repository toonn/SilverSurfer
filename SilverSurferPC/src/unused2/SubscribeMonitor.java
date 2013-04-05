package unused2;
/*package mq.intrfce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class SubscribeMonitor {
    public static final String MONITOR_KEY = "demo.silver";

    public static void main(String[] main) {
        try {
            final Connection conn = MQ.createConnection();
            final Channel channel = MQ.createChannel(conn);
            BufferedReader stdin = new BufferedReader(new InputStreamReader(
                    System.in));

            System.out.println(String.format(
                    "Subscribbed to topic '%s'. Exit with ENTER", MONITOR_KEY));

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

            boolean noAck = false;

            // ask the server to notify us of new message and do not send ack
            // message automatically
            // WARNING: This code is called from the thread that does the
            // communication with the
            // server so sufficient locking is required. Also do not use any
            // blocking calls to
            // the server such as queueDeclare, txCommit, or basicCancel.
            // Basicly only "basicAck"
            // should be called here!!!
            channel.basicConsume(queue.getQueue(), noAck, new DefaultConsumer(
                    channel) {
                @Override
                public void handleDelivery(String consumerTag,
                        Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {
                    // get the delivery tag to ack that we processed the message
                    // successfully
                    long deliveryTag = envelope.getDeliveryTag();

                    // properties.getTimestamp() contains the timestamp
                    // that the sender added when the message was published.
                    // This
                    // time is the time on the sender and NOT the time on the
                    // AMQP server. This implies that clients are possibly out
                    // of
                    // sync!
                    System.out.println(String.format("@%d: %s -> %s",
                            properties.getTimestamp().getTime(),
                            envelope.getRoutingKey(), new String(body)));

                    // send an ack to the server so it can remove the message
                    // from
                    // the queue.
                    channel.basicAck(deliveryTag, false);
                }
            });

            // wait here until a newline is entered
            stdin.readLine();

            channel.close();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}*/
