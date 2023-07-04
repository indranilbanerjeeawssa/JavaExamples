package dynamodb.streams.producer;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DynamoDBStreamsProducer {
	
	public static void main(String[] args) {
		try {
			DynamoDBStreamsProducer.dynamodbUpdater(args[0], args[1].concat("-").concat(DynamoDBStreamsProducer.getTodayDate()), Integer.parseInt(args[2]));
		} catch (Exception e) {
			System.out.println("Pass three parameters: 1 - DynamoDB Table Name, 2 - A string to be used as key for this batch of messages, 3 - Number of Messages in this batch");
			e.printStackTrace();
		}
	}
	
	public static void dynamodbUpdater(String dynamodbTable, String messageKey, int numberOfMessages) {
		
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		Table dynamoTable = dynamoDB.getTable(dynamodbTable);
		List<String> people = DynamoDBStreamsProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		for (int i=1;i<= numberOfMessagesToSend; i++) {
			Person thisPerson = DynamoDBStreamsProducer.getPersonFromLine(people.get(i));
			DynamoDBStreamsProducer.sendMessage(dynamoTable, thisPerson, messageKey, i);
		}
	}
	
	public static void sendMessage(Table dynamoTable, Person thisPerson, String messageKey, int messageNumber) {
        
		System.out.println("Now going to insert a row in DynamoDB for messageID = " + messageKey + "-" + messageNumber);
		Item item = new Item();
		item.withPrimaryKey("MessageID", messageKey + "-" + messageNumber);
		item.withString("Firstname", thisPerson.getFirstname());
		item.withString("Lastname", thisPerson.getLastname());
		item.withString("Company", thisPerson.getCompany());
		item.withString("Address_Street", thisPerson.getStreet());
		item.withString("City", thisPerson.getCity());
		item.withString("County", thisPerson.getCounty());
		item.withString("State", thisPerson.getState());
		item.withString("Zip", thisPerson.getZip());
		item.withString("Home_Phone", thisPerson.getHomePhone());
		item.withString("Cell_Phone", thisPerson.getCellPhone());
		item.withString("Email", thisPerson.getEmail());
		item.withString("Website", thisPerson.getWebsite());
		long now = Instant.now().getEpochSecond();
		long ttl = 3600;
		item.withLong("TimeToLive", now + ttl);
		dynamoTable.putItem(item);
	    System.out.println("Now done inserting a row in DynamoDB for messageID = " + messageKey + "-" + messageNumber);
    }
	
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = DynamoDBStreamsProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
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
