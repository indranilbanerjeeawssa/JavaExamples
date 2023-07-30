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

package com.amazonaws.services.lambda.samples.events.sqs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Handler value: example.HandlerSQS
public class HandlerSQS implements RequestHandler<SQSEvent, SQSBatchResponse>{
	//Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public SQSBatchResponse handleRequest(SQSEvent event, Context context)
	{
		List<SQSBatchResponse.BatchItemFailure> batchItemFailures = new ArrayList<SQSBatchResponse.BatchItemFailure>();
		LambdaLogger logger = context.getLogger();
		logger.log("Begin Event *************");
		try {
			logger.log(objectMapper.writeValueAsString(event));
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		logger.log("End Event ***************");
		for(SQSMessage msg : event.getRecords()){
			try {
				addToDynamoDB = true;
				logger.log("Begin Message *************");
				logger.log(objectMapper.writeValueAsString(msg));
				logger.log("End Message ***************");
				logger.log("Begin Message Body *************");
				logger.log(msg.getBody());
				logger.log("End Message Body ***************");
				//Person thisPerson = gson.fromJson(msg.getBody(), Person.class);
				Person thisPerson = objectMapper.readValue(msg.getBody(), Person.class);
				logger.log("This person = " + thisPerson.toJson());
				logger.log("Message ID = " + msg.getMessageId());
				logger.log("Receipt Handle = " + msg.getReceiptHandle());
				logger.log("Event Source ARN = " + msg.getEventSourceArn());
				logger.log("Event Source = " + msg.getEventSource());
				logger.log("AWS Region = " + msg.getAwsRegion());
				logger.log("MD5 Of Body = " + msg.getMd5OfBody());
				logger.log("MD5 Of Message Attributes = " + msg.getMd5OfMessageAttributes());
				Map<String, String> attributes = msg.getAttributes();
				attributes.forEach((k,v) -> {
					logger.log("Attribute: " + k + ", Value: " + v);
					if (k.equalsIgnoreCase("ApproximateFirstReceiveTimestamp")) {
						long timeNow = System.currentTimeMillis();
						long receiveTime = Long.parseLong(v);
						if ((thisPerson.getState().equalsIgnoreCase("CA")) && (timeNow - receiveTime <= 10000)) {
							try {
								throwit("Deliberately induced exception for CA persons");
							} catch (Exception e) {
								logger.log("An exception occurred while processing this SQS message - " + e.getMessage());
								batchItemFailures.add(new SQSBatchResponse.BatchItemFailure(msg.getMessageId()));
								logger.log("Added message with messageID = " + msg.getMessageId() + " to batchItemFailures list");
								addToDynamoDB = false;
							}
						}
					}
				});
				Map<String, MessageAttribute> messageAttributes = msg.getMessageAttributes();
				messageAttributes.forEach((k,v) -> {
					logger.log("Message Attribute: " + k + ", Value: " + v.getStringValue());
				});
				String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
				if ((null == AWS_SAM_LOCAL) && (addToDynamoDB)) {
					ddbUpdater.insertIntoDynamoDB(msg, thisPerson, logger);
				}
			} catch (Exception e) {
				logger.log("An exception occurred while processing this SQS message - " + e.getMessage());
				batchItemFailures.add(new SQSBatchResponse.BatchItemFailure(msg.getMessageId()));
				logger.log("Added message with messageID = " + msg.getMessageId() + " to batchItemFailures list");
			}
		}
		return new SQSBatchResponse(batchItemFailures);
	}
	
	public void throwit(String message) throws Exception{
		throw new Exception(message);
	}
}
