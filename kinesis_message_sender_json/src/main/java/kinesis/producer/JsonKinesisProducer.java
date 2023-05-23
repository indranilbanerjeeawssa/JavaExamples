package kinesis.producer;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

public class JsonKinesisProducer {
	
	public static void main(String[] args) {
		JsonKinesisProducer.kinesisSender(args[0], args[1], Integer.parseInt(args[2]));
	}
		
	public static void kinesisSender(String kinesisStream, String messageKey, int numberOfMessages) {
		KinesisClient kinesisClient = KinesisClient.builder().build();
		List<String> people = JsonKinesisProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		for (int i=1;i<= numberOfMessagesToSend; i++) {
			Person thisPerson = JsonKinesisProducer.getPersonFromLine(people.get(i));
			JsonKinesisProducer.sendMessage(kinesisClient, kinesisStream, thisPerson, messageKey, i);
		}
		
	}
	
	public static void sendMessage(KinesisClient kinesisClient, String streamName, Person person, String messageKey, int messageNumber) {
		byte[] personBytes = person.toJson().getBytes();
		PutRecordRequest request = PutRecordRequest.builder()
	            .partitionKey(messageKey.concat(Integer.toString(messageNumber)))
	            .streamName(streamName)
	            .data(SdkBytes.fromByteArray(personBytes))
	            .build();
		try {
			System.out.println("**********************************************************");
            System.out.println("Now going to send one Kinesis message to stream - " + streamName);
            System.out.println("Message Key = " + messageKey + " and Message Number = " + Integer.toString(messageNumber));
            System.out.println("Message Body = " + person.toJson());
            kinesisClient.putRecord(request);
            System.out.println("Now done sending one SQS message");
            System.out.println("**********************************************************");
        } catch (KinesisException e) {
            System.err.println(e.getMessage());
        }
	}
		
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonKinesisProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
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
