package sqs.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class JsonSQSProducer {
	
	public static void main(String[] args) {
		JsonSQSProducer.sqsSender(args[0], args[1], Integer.parseInt(args[2]));
	}
	

	public static void sqsSender(String sqsQueue, String messageKey, int numberOfMessages) {
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
		messageAttributes.put("MessageKey", new MessageAttributeValue()
		.withDataType("String")
		.withStringValue(messageKey.concat(JsonSQSProducer.getTodayDate())));
		
		List<String> people = JsonSQSProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		
		for (int i=1;i<= numberOfMessagesToSend; i++) {
			Person thisPerson = JsonSQSProducer.getPersonFromLine(people.get(i));
			final SendMessageRequest sendMessageRequest = new SendMessageRequest();
			sendMessageRequest.withMessageBody(thisPerson.toJson());
			sendMessageRequest.withQueueUrl(sqsQueue);
			sendMessageRequest.withMessageAttributes(messageAttributes);
			sqs.sendMessage(sendMessageRequest);
		}
	}
	
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonSQSProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
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
