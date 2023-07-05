package dynamodb.streams.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
		//Entering random numbers of different types
		Random rand = new Random();
		item.withInt("RandomInteger", rand.nextInt());
		item.withDouble("RandomDouble", rand.nextDouble());
		item.withFloat("RandomFloat", rand.nextFloat());
		item.withLong("RandomLong", rand.nextLong());
		//BigDecimal bigDecimal = new BigDecimal(Math.random());
		//item.with withBigDecimal("RandomBigDecimal", bigDecimal);
		BigInteger bigInteger = new BigInteger(32, rand);
		item.withBigInteger("RandomBigInteger", bigInteger);
		if (thisPerson.getState().equalsIgnoreCase("CA")) {
			item.withBoolean("IsFromCalifornia", true);
		} else {
			item.withBoolean("IsFromCalifornia", false);
		}
		item.withNull("NullAttribute");
		item.withNumber("RandomNumber1", rand.nextInt());
		item.withNumber("RandomNumber2", rand.nextDouble());
		item.withNumber("RandomNumber3", rand.nextFloat());
		item.withNumber("RandomNumber4", rand.nextLong());
		Map<String, Object> personMap = new HashMap<String, Object>();
		personMap.put("FirstnameAsMapKey", thisPerson.getFirstname());
		personMap.put("LastnameAsMapKey", thisPerson.getLastname());
		personMap.put("StreetAsMapKey", thisPerson.getStreet());
		personMap.put("CityAsMapKey", thisPerson.getCity());
		personMap.put("StateAsMapKey", thisPerson.getState());
		personMap.put("CountyAsMapKey", thisPerson.getCounty());
		personMap.put("ZipAsMapKey", thisPerson.getZip());
		personMap.put("CurrentTimeAsMapKey", System.currentTimeMillis());
		personMap.put("MessageNumberAsMapKey", Integer.valueOf(messageNumber));
		item.withMap("PersonAsMap", personMap);
		List<Object> personAsList = new ArrayList<Object>();
		personAsList.add(thisPerson.getFirstname());
		personAsList.add(thisPerson.getLastname());
		personAsList.add(thisPerson.getStreet());
		personAsList.add(thisPerson.getCity());
		personAsList.add(thisPerson.getState());
		personAsList.add(thisPerson.getCounty());
		personAsList.add(thisPerson.getZip());
		personAsList.add(System.currentTimeMillis());
		personAsList.add(Integer.valueOf(messageNumber));
		item.withList("PersonAsList", personAsList);
		Set<String> personAsStringSet = new HashSet<String>();
		personAsStringSet.add(thisPerson.getFirstname());
		personAsStringSet.add(thisPerson.getLastname());
		personAsStringSet.add(thisPerson.getStreet());
		personAsStringSet.add(thisPerson.getCity());
		personAsStringSet.add(thisPerson.getState());
		personAsStringSet.add(thisPerson.getCounty());
		personAsStringSet.add(thisPerson.getZip());
		item.withStringSet("PersonAsStringSet", personAsStringSet);
		Set<Number> numberSet = new HashSet<Number>();
		numberSet.add(rand.nextInt());
		numberSet.add(rand.nextDouble());
		numberSet.add(rand.nextFloat());
		numberSet.add(rand.nextLong());
		numberSet.add(new BigDecimal(Math.random()));
		numberSet.add(new BigInteger(64, rand));
		item.withNumberSet("NumberSet", numberSet);
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
