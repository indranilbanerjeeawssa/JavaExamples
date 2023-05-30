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
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public class JsonSQSProducer {
	
	public static void main(String[] args) {
		try {
			JsonSQSProducer.sqsSender(args[0], args[1].concat(JsonSQSProducer.getTodayDate()), Integer.parseInt(args[2]));
		} catch (NumberFormatException e) {
			System.out.println("Pass three parameters: 1 - Queue Name, 2 - A string to be used as key for this batch of messages, 3 - Number of Messages in this batch");
			e.printStackTrace();
		}
	}
	

	public static void sqsSender(String sqsQueue, String messageKey, int numberOfMessages) {
		SqsClient sqsClient = SqsClient.builder().build();
		
		List<String> people = JsonSQSProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		for (int i=1;i<= numberOfMessagesToSend; i++) {
			Person thisPerson = JsonSQSProducer.getPersonFromLine(people.get(i));
			JsonSQSProducer.sendMessage(sqsClient, sqsQueue, thisPerson.toJson(), messageKey, i);
		}
	}
	
	public static void sendMessage(SqsClient sqsClient, String queueName, String message, String messageKey, int messageNumber) {
        try {
           GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
            attributes.put("MessageKey", MessageAttributeValue.builder().dataType("String").stringValue(messageKey).build());
            attributes.put("MessageNumber", MessageAttributeValue.builder().dataType("String").stringValue(Integer.toString(messageNumber)).build());
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .messageAttributes(attributes)
                .build();
            System.out.println("**********************************************************");
            System.out.println("Now going to send one SQS message to queue - " + queueName);
            System.out.println("Message Key = " + messageKey + " and Message Number = " + Integer.toString(messageNumber));
            System.out.println("Message Body = " + message);
            sqsClient.sendMessage(sendMsgRequest);
            System.out.println("Now done sending one SQS message");
            System.out.println("**********************************************************");

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
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
			e.printStackTrace();
		}
		return personList;
	}
	
	public static Person getPersonFromLine(String line) {
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
