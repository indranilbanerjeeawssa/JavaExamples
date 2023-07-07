package s3.producer;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class JsonS3Producer {
	
	public static void main(String[] args) {
		try {
			JsonS3Producer.s3Sender(args[0], args[1], args[2].concat(JsonS3Producer.getTodayDate()), Integer.parseInt(args[3]));
		} catch (NumberFormatException e) {
			System.out.println("Pass four parameters: 1 - S3 Bucket Name, 2- S3 Bucket Prefix, 3 - A string to be used as key for this batch of objects, 4 - Number of objects in this batch");
			System.out.println("Pass empty string for 2 if you want the objects to be at the root of the bucket");
			e.printStackTrace();
		}
	}

	public static void s3Sender(String s3Bucket, String s3Prefix, String objectKey, int numberOfObjects) {
		
		DefaultAwsRegionProviderChain defaultAwsRegionProviderChain = new DefaultAwsRegionProviderChain();
		Region region = defaultAwsRegionProviderChain.getRegion();
		System.out.println("region = " + region.toString());
		S3Client s3Client = S3Client.builder().region(region).build();
		
		List<String> people = JsonS3Producer.readDataFile(); 
		 int numberOfObjectsToCreate=0; 
		 if (people.size() > numberOfObjects) { 
			 numberOfObjectsToCreate = numberOfObjects; 
		 } else { 
			 numberOfObjectsToCreate = people.size(); 
		 }
		for (int i=1;i<= numberOfObjectsToCreate; i++) {
			Person thisPerson = JsonS3Producer.getPersonFromLine(people.get(i));
			JsonS3Producer.writeFileToS3(s3Client, s3Bucket, s3Prefix, thisPerson.toJson(), objectKey, i);
		}
	}
	
	public static void writeFileToS3(S3Client s3Client, String s3Bucket, String s3Prefix, String objectData, String objectKey, int objectNumber) {
        try {
        	String s3ObjectKey = "";
        	if ("".equals(s3Prefix)) {
        		s3ObjectKey = objectKey + "-" + objectNumber;
        	} else if (s3Prefix.endsWith("/")) {
        		s3ObjectKey = s3Prefix + objectKey + "-" + objectNumber;
        	} else {
        		s3ObjectKey = s3Prefix + "/" + objectKey + "-" + objectNumber;
        	}
        	
        	PutObjectRequest objectRequest = PutObjectRequest.builder()
        			                                         .bucket(s3Bucket)
        			                                         .key(s3ObjectKey)
        			                                         .build();
            
            System.out.println("**********************************************************");
            System.out.println("Now going to send one object to S3 bucket - " + s3Bucket);
            
            s3Client.putObject(objectRequest, RequestBody.fromString(objectData));
            
            System.out.println("Now done sending one object to S3 bucket - " + s3Bucket);
            System.out.println("**********************************************************");

        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
	
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonS3Producer.class.getClassLoader().getResourceAsStream("us-500.csv");
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
