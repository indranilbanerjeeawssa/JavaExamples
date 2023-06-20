package activemq.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.DeliveryMode;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonActiveMQProducer {

	Properties prop;

	public static void main(String[] args) {
		
		String activeMQEndpoint = args[0];
		String activeMQUsername = SecretsManagerDecoder.getUsernameAndPassword().getUsername();
		String activeMQPassword = SecretsManagerDecoder.getUsernameAndPassword().getPassword();
		String activeMQQueue = args[1];
		
		
		try {
			JsonActiveMQProducer.activeMQQueueReceiver(activeMQEndpoint, activeMQUsername, activeMQPassword, activeMQQueue);
			
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

			// Receive the message when it arrives.
			final TextMessage consumerTextMessage = (TextMessage) consumerMessage;
			System.out.println("Message received: " + consumerTextMessage.getText());
		}
		
	}

	public static Properties readPropertiesFile(String fileName) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		Properties prop = null;
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			throw new FileNotFoundException("Not a valid property file path");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new IOException("Problem reading property file. Check permissions");
		} finally {
			fis.close();
		}
		return prop;
	}
	
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonActiveMQProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		String thisLine = null;
		try {
			thisLine = bf.readLine();
			while (null != thisLine) {
				personList.add(thisLine);
				thisLine = bf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return personList;
	}
	
	public static Person getPersonFromLine(String line) {
		
		//String[] fields = line.split(",");
		String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		Person thisPerson = new Person();
		thisPerson.setFirstname(fields[0]);
		thisPerson.setLastname(fields[1]);
		thisPerson.setCompany(fields[2]);
		thisPerson.setStreet(fields[3]);
		thisPerson.setCity(fields[4]);
		thisPerson.setCounty(fields[5]);
		thisPerson.setState(fields[6]);
		thisPerson.setZip(fields[7]);
		thisPerson.setHomePhone(fields[8]);
		thisPerson.setCellPhone(fields[9]);
		thisPerson.setEmail(fields[10]);
		thisPerson.setWebsite(fields[11]);
		return thisPerson;
	}
	
	public static String getTodayDate() {
		
		LocalDateTime ldt = LocalDateTime.now();
        String formattedDateStr = DateTimeFormatter.ofPattern("MM-dd-YYYY-HH-MM-SS").format(ldt);
        return formattedDateStr;
	}
	


}
