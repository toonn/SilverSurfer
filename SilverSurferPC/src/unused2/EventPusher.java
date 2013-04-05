package unused2;
/*package mq.intrfce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class EventPusher {
    public static void main(String[] args) {
        EventPusher ep = new EventPusher();
        ep.run();
    }

    private Timer timer = new Timer();

    private Connection conn = null;

    private Channel channel = null;
    private String teamName = null;
    private String routingKey = null;
    private boolean racing = false;
    // this task generates events and sends them to the server
    private TimerTask task = new TimerTask() {
        private Random rng = new Random(System.currentTimeMillis());

        @Override
        public void run() {
            if (racing) {
                int event = rng.nextInt();
                String message = String.format("event %010d", event);
                sendMessage(message);

                System.out.println(String.format(
                        "Send message '%s' to exchange '%s' with key '%s'",
                        message, Config.EXCHANGE_NAME, routingKey));
            }
        }
    };

    public EventPusher() {
        run();
    }


    private void listenForExit() throws IOException {
        // wait for ENTER on newline to cleanup and exit
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));
        stdin.readLine();

        if (racing) {
            sendMessage("withdraw");
            System.out.println("Leaving the race.");
        }
        timer.cancel();
        channel.close();
        conn.close();
        System.exit(0);
    }

    private void listenForLaunch() throws IOException {
        // create a queue for this program. Because the queue is only used by
        // this
        // program we just ask a queue. The server will generate a random queue
        // name for us.
        //
        // If you provide a name for the queue and other clients use it as well
        // message are delived in a round robin fashion.
        final AMQP.Queue.DeclareOk queue = channel.queueDeclare();
        System.out.println("Create queue " + queue.getQueue());

        // bind the queue to all routing keys that match MONITOR_KEY. The
        // exchange will place
        // all matching message in our queue
        channel.queueBind(queue.getQueue(), Config.EXCHANGE_NAME,
                Config.LAUNCH_ROUTING_KEY);
        System.out.println(String.format(
                "Bound queue %s to launch key '%s' on exchange %s",
                queue.getQueue(), Config.LAUNCH_ROUTING_KEY,
                Config.EXCHANGE_NAME));

        boolean noAck = false;
        // ask the server to notify us of new message and do not send ack
        // message automatically
        // WARNING: This code is called from the thread that does the
        // communication with the
        // server so sufficient locking is required. Also do not use any
        // blocking calls to
        // the server such as queueDeclare, txCommit, or basicCancel. Basicly
        // only "basicAck"
        // should be called here!!!
        channel.basicConsume(queue.getQueue(), noAck, new DefaultConsumer(
                channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                // get the delivery tag to ack that we processed the message
                // successfully
                long deliveryTag = envelope.getDeliveryTag();

                // process the message
                String data = new String(body);

                if (data.equals("start")) {
                    startRace();
                } else if (data.equals("stop")) {
                    stopRace();
                }

                // send an ack to the server so it can remove the message from
                // the queue.
                channel.basicAck(deliveryTag, false);
            }
        });
    }

    public void run() {
        try {
            setup();
        } catch (IOException e) {
            System.err
                    .println("Unable to setup program and connect to AMQP server");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        try {
            listenForLaunch();
        } catch (IOException e) {
            System.err
                    .println("An error occurred while listening for launch events.");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        try {
            listenForExit();
        } catch (IOException e) {
            System.err.println("An error occurred cleaning up.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private void sendMessage(String message) {
        // set some properties of the message
        AMQP.BasicProperties props = new AMQP.BasicProperties();
        props.setTimestamp(new Date()); // set the time of the message,
                                        // otherwise the
                                        // receivers do not know when the
                                        // message is sent
        props.setContentType("text/plain"); // the body of the message is plain
                                            // text
        props.setDeliveryMode(1); // do not make the message persistant

        try {
            // publish the message to the exchange with the race.$teamname
            // routing key
            channel.basicPublish(Config.EXCHANGE_NAME, routingKey, props,
                    message.getBytes());
        } catch (IOException e) {
            System.err.println("Unable to send message to AMQP server");
            e.printStackTrace();
        }
    }

    public void setup() throws IOException {
        // create connection to the AMQP server and create a channel to the
        // exchange (See Config.EXCHANGE_NAME)
        conn = MQ.createConnection();
        channel = MQ.createChannel(conn);

        // read the team name from stdin
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));

        System.out.println("Please provide a team name:");
        System.out.print("> ");

        teamName = stdin.readLine();

        if (teamName == null) {
            System.exit(1);
        }

        // use the team name in the routing key
        teamName = teamName.trim();
        routingKey = "race." + teamName;

        System.out
                .println(String
                        .format("Sending events for team %s with routing key '%s' to exchange %s",
                                teamName, routingKey, Config.EXCHANGE_NAME));
        System.out.println("Press ENTER to exit");

        // schedule a task every 2s
        timer.schedule(task, 0, 2000);
    }

    private void startRace() {
        sendMessage("started");
        System.out.println("Race started");
        racing = true;
    }

    private void stopRace() {
        sendMessage("stopped");
        System.out.println("Race stopped");
        racing = false;
    }
}
*/