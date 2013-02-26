package mq.communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * A program that monitors "race.*" messages
 * 
 * @author bart.vanbrabant@cs.kuleuven.be
 *
 */
public class SubscribeMonitor {
	
	private String monitor_key = "";
	private Connection conn = null;
	private Channel channel = null;
	
	public String getMonitor_key() {
		return monitor_key;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public Connection getConn() {
		return conn;
	}
	
	public SubscribeMonitor(String monitor_key, Channel ch, Connection co){
		
		this.monitor_key = monitor_key;
		conn = co;
		channel = ch;
		
	}
	
	/**
	 * Sets up the monitor so it listens to the queue declared in the constructor.
	 */
	public void start(){
		try {
			
			
			System.out.println(String.format("Subscribbed to topic '%s'. Exit with ENTER",
					monitor_key));
			
			// create a queue for this program
			final AMQP.Queue.DeclareOk queue = channel.queueDeclare();
			System.out.println("Create queue " + queue.getQueue());
			
			// bind the queue to all routing keys that match MONITOR_KEY
			getChannel().queueBind(queue.getQueue(), Config.EXCHANGE_NAME, getMonitor_key());
			System.out.println(String.format("Bound queue %s to all keys that match '%s' on exchange %s",
					queue.getQueue(), getMonitor_key(), Config.EXCHANGE_NAME));
			
			boolean noAck = false;
			
			// ask the server to notify us of new message and do not send ack message automatically
			// WARNING: This code is called from the thread that does the communication with the 
			// server so sufficient locking is required. Also do not use any blocking calls to
			// the server such as queueDeclare, txCommit, or basicCancel. Basicly only "basicAck"
			// should be called here!!!
			getChannel().basicConsume(queue.getQueue(), noAck, new DefaultConsumer(getChannel()) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, 
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					// get the delivery tag to ack that we processed the message successfully
					long deliveryTag = envelope.getDeliveryTag();

					// properties.getTimestamp() contains the timestamp
					// that the sender added when the message was published. This 
					// time is the time on the sender and NOT the time on the 
					// AMQP server. This implies that clients are possibly out of
					// sync!
					System.out.println(String.format("@%d: %s -> %s", 
							properties.getTimestamp().getTime(),
							envelope.getRoutingKey(),
							new String(body)));
					
					MessageCenter ms = new MessageCenter();
					ms.sendMessage(Config.EXCHANGE_NAME, "race.bla", "woowoo");
					// send an ack to the server so it can remove the message from
					// the queue.	
					getChannel().basicAck(deliveryTag, false);
				}
			});

		
			
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	
	}
	
	/**
	 * Quit listening to the queue.
	 */
	public void stop(){
		try {
			getChannel().close();
			getConn().close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
