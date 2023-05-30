package eventbridge.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsRequest;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class JsonEventbridgeProducer {
	
	public static void main(String[] args) {
		try {
			JsonEventbridgeProducer.eventbridgeSender(args[0], args[1].concat("-").concat(JsonEventbridgeProducer.getTodayDate()), Integer.parseInt(args[2]));
		} catch (NumberFormatException e) {
			System.out.println("Pass three parameters in that order - 1 - The EventBridge Bus Name, 2 - A Unique Key to identity the batch of messages, 3 - The total number of messages to send in the batch");
			e.printStackTrace();
		}
	}
	
	public static void eventbridgeSender(String eventbridgeBus, String messageKey, int numberOfMessages) {
		EventBridgeClient eventbridgeClient = EventBridgeClient.builder().build();
		//String snsTopicARN = JsonEventbridgeProducer.getTopicARNFromTopicName(snsClient, snsTopic);
		List<String> people = JsonEventbridgeProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		for (int i=1;i<= numberOfMessagesToSend; i++) {
			Person thisPerson = JsonEventbridgeProducer.getPersonFromLine(people.get(i));
			PersonWithKeyAndNumber thisPersonWithKeyAndNumber = new PersonWithKeyAndNumber();
			thisPersonWithKeyAndNumber.setPerson(thisPerson);
			thisPersonWithKeyAndNumber.setMessageKey(messageKey);
			thisPersonWithKeyAndNumber.setMessageNumber(i);
			JsonEventbridgeProducer.sendMessage(eventbridgeClient, eventbridgeBus, thisPersonWithKeyAndNumber.toJson(), messageKey, i);
		}
	}
	
	public static void sendMessage(EventBridgeClient eventbridgeClient, String eventbridgeBus, String message, String messageKey, int messageNumber) {
		try {
			PutEventsRequestEntry entry = PutEventsRequestEntry.
										  builder().
										  detail(message).
										  detailType("PersonCustomEvent").
										  source("EventbridgeMessageSenderJava").
										  eventBusName(eventbridgeBus).time(Instant.now()).
										  build();
			PutEventsRequest eventsRequest = PutEventsRequest.builder()
		            .entries(entry)
		            .build();
            System.out.println("**********************************************************");
            System.out.println("Now going to send one Eventbridge message to bus - " + eventbridgeBus);
            System.out.println("Message Key = " + messageKey + " and Message Number = " + Integer.toString(messageNumber));
            System.out.println("Message Body = " + message);
            PutEventsResponse result = eventbridgeClient.putEvents(eventsRequest);
            System.out.println("Message sent. Status is " + result.sdkHttpResponse().statusCode());
            System.out.println("Now done sending one Eventbridge message");
            System.out.println("**********************************************************");

         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }
    }
	
	public static void snsSender(String snsTopic, String messageKey, int numberOfMessages) {
		SnsClient snsClient = SnsClient.builder().build();
		String snsTopicARN = JsonEventbridgeProducer.getTopicARNFromTopicName(snsClient, snsTopic);
		List<String> people = JsonEventbridgeProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		for (int i=1;i<= numberOfMessagesToSend; i++) {
			Person thisPerson = JsonEventbridgeProducer.getPersonFromLine(people.get(i));
			JsonEventbridgeProducer.sendMessage(snsClient, snsTopicARN, thisPerson.toJson(), messageKey, i);
		}
	}
	
	public static String getTopicARNFromTopicName(SnsClient snsClient, String topicName) {
		String topicARN = "";
		try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                   .build();

            ListTopicsResponse result = snsClient.listTopics(request);
            List<Topic> topics = result.topics();
            for (Topic topic: topics) {
            	String currentTopicARN = topic.topicArn();
            	if (currentTopicARN.endsWith(topicARN)) {
            		topicARN = currentTopicARN;
            		break;
            	}
            }

        } catch (SnsException e) {
        	System.err.println("Incorrect Topic Name Specified");
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
		return topicARN;
	}
	
	public static void sendMessage(SnsClient snsClient, String topicARN, String message, String messageKey, int messageNumber) {
		try {
			Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
            attributes.put("MessageKey", MessageAttributeValue.builder().dataType("String").stringValue(messageKey).build());
            attributes.put("MessageNumber", MessageAttributeValue.builder().dataType("String").stringValue(Integer.toString(messageNumber)).build());
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicARN)
                .messageAttributes(attributes)
                .subject("Sending Message with Key = " + messageKey + " and message number = " + Integer.toString(messageNumber))
                .build();
            System.out.println("**********************************************************");
            System.out.println("Now going to send one SNS message to topic - " + topicARN);
            System.out.println("Message Key = " + messageKey + " and Message Number = " + Integer.toString(messageNumber));
            System.out.println("Message Body = " + message);
            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
            System.out.println("Now done sending one SNS message");
            System.out.println("**********************************************************");

         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }
    }
	
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonEventbridgeProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
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
