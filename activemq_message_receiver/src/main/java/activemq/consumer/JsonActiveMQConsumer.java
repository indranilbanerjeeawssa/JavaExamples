package activemq.consumer;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;

public class JsonActiveMQConsumer {

	Properties prop;

	public static void main(String[] args) {
		
		String activeMQEndpoint = args[0];
		String activeMQUsername = SecretsManagerDecoder.getUsernameAndPassword().getUsername();
		String activeMQPassword = SecretsManagerDecoder.getUsernameAndPassword().getPassword();
		String activeMQQueue = args[1];
		
		
		try {
			JsonActiveMQConsumer.activeMQQueueReceiver(activeMQEndpoint, activeMQUsername, activeMQPassword, activeMQQueue);
			
		} catch (Exception e) {
			System.out.println("Exception occurred");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void activeMQQueueReceiver(String activeMQEndpoint, String activeMQUsername, String activeMQPassword, String activeMQQueue) throws Exception {
		
		// Create a connection factory.
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQEndpoint);

		// Pass the username and password.
		connectionFactory.setUserName(activeMQUsername);
		connectionFactory.setPassword(activeMQPassword);

		// Establish a connection for the consumer.
		final Connection consumerConnection = connectionFactory.createConnection();
		consumerConnection.start();
		
		// Create a session.
		final Session consumerSession = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create a queue named "MyQueue".
		final Destination consumerDestination = consumerSession.createQueue(activeMQQueue);

		// Create a message consumer from the session to the queue.
		final MessageConsumer consumer = consumerSession.createConsumer(consumerDestination);
		
		while (true) {
			// Begin to wait for messages.
			final Message consumerMessage = consumer.receive();
			long currentTime = System.currentTimeMillis();
			// Receive the message when it arrives.
			final TextMessage consumerTextMessage = (TextMessage) consumerMessage;
			System.out.println("*****Starting to print details of new message*****");
			System.out.println("Received message with CorrelationID = " + consumerTextMessage.getJMSCorrelationID() + " at time = " + currentTime);
			System.out.println("Delivery Mode = " + consumerTextMessage.getJMSDeliveryMode());
			System.out.println("CorrelationID = " + consumerTextMessage.getJMSCorrelationID());
			System.out.println("Expiration = " + consumerTextMessage.getJMSExpiration());
			System.out.println("MessageID = " + consumerTextMessage.getJMSMessageID());
			System.out.println("Priority = " + consumerTextMessage.getJMSPriority());
			System.out.println("TimeStamp = " + consumerTextMessage.getJMSTimestamp());
			System.out.println("Type = " + consumerTextMessage.getJMSType());
			System.out.println("Destination = " + consumerTextMessage.getJMSDestination());
			System.out.println("Redelivered = " + consumerTextMessage.getJMSRedelivered());
			System.out.println("ReplyTo = " + consumerTextMessage.getJMSReplyTo());
			System.out.println("Person = " + consumerTextMessage.getText());
			Enumeration propertyNames = consumerTextMessage.getPropertyNames();
			while (propertyNames.hasMoreElements()) {
				Object thisPropertyObject = propertyNames.nextElement();
				System.out.println("Attribute = " + thisPropertyObject.toString() + " and Value = " + consumerTextMessage.getObjectProperty(thisPropertyObject.toString()).toString());
			}
			System.out.println("*****Finishing printing details of new message*****");
		}
		
	}

}
