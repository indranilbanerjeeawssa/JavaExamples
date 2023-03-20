package kafka.producer;

import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SimpleKafkaProducer {

	Properties prop;

	public static void main(String[] args) {
		String propertyFile = args[0];
		String kafkaTopic = args[1];
		String seederKeyString = args[2];
		String seederValueString = args[3];
		int numberOfMessages = Integer.parseInt(args[4]);
		Properties prop = null;
		try {
			prop = SimpleKafkaProducer.readPropertiesFile(propertyFile);
		} catch (IOException e) {
			System.out.println("Please specify a valid Kafka Properties File as the first argument");
			e.printStackTrace();
		}
		if (null != prop) {
			Properties properties = new Properties(prop);
			properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.getProperty("bootstrap.servers"));
			properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			properties.setProperty("security.protocol", "SASL_SSL");
			properties.setProperty("sasl.mechanism", "AWS_MSK_IAM");
			properties.setProperty("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;");
			properties.setProperty("sasl.client.callback.handler.class", "software.amazon.msk.auth.iam.IAMClientCallbackHandler");
			SimpleKafkaProducer.kafkaSender(properties, kafkaTopic, seederKeyString, seederValueString,
					numberOfMessages);
		}
	}

	public static void kafkaSender(Properties prop, String kafkaTopic, String seederKeyString, String seederValueString,
			int numberOfMessages) {

		Producer<String, String> producer = new KafkaProducer<String, String>(prop);
		for (int i = 1; i <= numberOfMessages; i++) {

			String thisKey = seederKeyString.concat("-" + Integer.toString(i));
			String thisValue = seederValueString.concat("-" + Integer.toString(i));
			try {
				producer.send(new ProducerRecord<String, String>(kafkaTopic, thisKey, thisValue));
				producer.flush();
			} catch (Exception e) {
				System.out.println("Encountered a problem when sending a Kafka message. Ensure topic is valid");
				e.printStackTrace();
			}
			System.out.println("Sent out one Kafka message with key = " + thisKey + " and value = " + thisValue);
		}
		producer.close();

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

}
