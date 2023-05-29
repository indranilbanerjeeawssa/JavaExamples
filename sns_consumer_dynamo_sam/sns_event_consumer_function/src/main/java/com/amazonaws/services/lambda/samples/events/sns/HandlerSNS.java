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

package com.amazonaws.services.lambda.samples.events.sns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

// Handler value: example.HandlerSQS
public class HandlerSNS implements RequestHandler<SNSEvent, String>{
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());
	@Override
	public String handleRequest(SNSEvent event, Context context)
	{
		LambdaLogger logger = context.getLogger();
		logger.log("Begin Event *************");
		try {
			logger.log(objectMapper.writeValueAsString(event));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger.log("End Event ***************");
		

		return new String("200-OK");
	}
	
	public void throwit(String message) throws Exception{
		throw new Exception(message);
	}
}
