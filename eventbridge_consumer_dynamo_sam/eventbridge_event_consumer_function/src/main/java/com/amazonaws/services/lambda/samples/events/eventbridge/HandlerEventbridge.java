//Lambda Runtime delivers a batch of messages to the lambda function
//Each batch of messages has two fields EventSource and EventSourceARN
//Each batch of messages also has a field called Records
//The Records is a map with multiple keys and values
//Each key is a combination of the Topic Name and the Partition Number
//One batch of messages can contain messages from multiple partitions

/*
To simplify representing a batch of Kafka messages as a list of messages
We have created a Java class called KafkaMessage under the models package
Here we are mapping the structure of an incoming Kafka event to a list of
objects of the KafkaMessage class
 */

package com.amazonaws.services.lambda.samples.events.eventbridge;

import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Handler value: example.HandlerSQS
public class HandlerEventbridge implements RequestHandler<ScheduledEvent, String>{
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB=true;
	//ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());
	@Override
	public String handleRequest(ScheduledEvent event, Context context)
	{
		LambdaLogger logger = context.getLogger();
		
		try {
			logger.log("Begin Event *************");
			//logger.log(objectMapper.writeValueAsString(event));
			logger.log(gson.toJson(event));
			logger.log("End Event ***************");
			logger.log("AWS Account = " + event.getAccount());
			logger.log("AWS Region = " + event.getRegion());
			logger.log("Event Source = " + event.getSource());
			logger.log("Sender Detail Type = " + event.getDetailType());
			logger.log("Event ID = " + event.getId());
			logger.log("Event Time = " + event.getTime().toString());
			List<String> resources = event.getResources();
			if ((null == resources) || (resources.size() == 0)) {
				logger.log("This event did not have any resources as it is not an AWS event");
			} else {
				for (int i=1;i<=resources.size();i++) {
					logger.log("Resource Number - " + i + " = " + resources.get(i));
				}
			}
			Map<String, Object> eventDetail = event.getDetail();
			PersonWithKeyAndNumber personWithKeyAndNumber = gson.fromJson(gson.toJson(eventDetail), PersonWithKeyAndNumber.class);
			logger.log("PersonWithKeyAndNumber = " + personWithKeyAndNumber.toString());
			logger.log("Message Key = " + personWithKeyAndNumber.getMessageKey());
			logger.log("Message Number = " + personWithKeyAndNumber.messageNumber);
			logger.log("Firstname = " + personWithKeyAndNumber.getPerson().getFirstname());
			logger.log("Lastname = " + personWithKeyAndNumber.getPerson().getLastname());
			logger.log("Company = " + personWithKeyAndNumber.getPerson().getCompany());
			logger.log("Street = " + personWithKeyAndNumber.getPerson().getStreet());
			logger.log("City = " + personWithKeyAndNumber.getPerson().getCity());
			logger.log("County = " + personWithKeyAndNumber.getPerson().getCounty());
			logger.log("State = " + personWithKeyAndNumber.getPerson().getState());
			logger.log("Zip = " + personWithKeyAndNumber.getPerson().getZip());
			logger.log("Cellphone = " + personWithKeyAndNumber.getPerson().getCellPhone());
			logger.log("Homephone = " + personWithKeyAndNumber.getPerson().getHomePhone());
			logger.log("Email = " + personWithKeyAndNumber.getPerson().getEmail());
			logger.log("Website = " + personWithKeyAndNumber.getPerson().getWebsite());
			String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
			if ((null == AWS_SAM_LOCAL) && (addToDynamoDB)) {
				ddbUpdater.insertIntoDynamoDB(event, gson, logger);
			}
		} catch (Exception e1) {
			logger.log("An exception occurred " + e1.getMessage());
			throw new RuntimeException(e1);
		}
		
		return new String("200-OK");
	}
}
