package apigateway.rest.nonproxy.producer;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonAPIGatewayRESTProducer {

	public static void main(String[] args) {
		try {
			JsonAPIGatewayRESTProducer.restAPISender(args[0], args[1].concat(JsonAPIGatewayRESTProducer.getTodayDate()),
					Integer.parseInt(args[2]));
		} catch (NumberFormatException e) {
			System.out.println(
					"Pass three parameters: 1 - API URL, 2 - A string to be used as key for this batch of messages, 3 - Number of Messages in this batch");
			e.printStackTrace();
		}
	}

	public static void restAPISender(String apiGatewayURL, String messageKey, int numberOfMessages) {

		try {

			List<String> people = JsonAPIGatewayRESTProducer.readDataFile();
			int numberOfMessagesToSend = 0;
			if (people.size() > numberOfMessages) {
				numberOfMessagesToSend = numberOfMessages;
			} else {
				numberOfMessagesToSend = people.size();
			}
			for (int i = 1; i <= numberOfMessagesToSend; i++) {
				Person thisPerson = JsonAPIGatewayRESTProducer.getPersonFromLine(people.get(i));
				PersonWithID thisPersonWithID = new PersonWithID(thisPerson, messageKey + "-" + i);
				JsonAPIGatewayRESTProducer.sendRestPostMessage(apiGatewayURL, thisPersonWithID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void sendRestPostMessage(String apiGatewayURL, PersonWithID personWithID) {

		URL url = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			url = new URL(apiGatewayURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			os = conn.getOutputStream();

			String input = personWithID.toJson();
			System.out.println("**********************************************************");
			System.out.println("Now going to send one POST message");
			System.out.println("Message Body = " + input);
			os.write(input.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server = \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			System.out.println("Now done sending one POST message");
			System.out.println("**********************************************************");
			os.close();
			conn.disconnect();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonAPIGatewayRESTProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
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
