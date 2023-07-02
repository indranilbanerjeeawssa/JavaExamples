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

package com.amazonaws.services.lambda.samples.events.rabbitmq;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.events.ActiveMQEvent;
import com.amazonaws.services.lambda.runtime.events.RabbitMQEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.amazonaws.services.lambda.runtime.events.RabbitMQEvent.BasicProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

// Handler value: example.HandlerSQS
public class HandlerRabbitMQ implements RequestHandler<RabbitMQEvent, String>{
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public String handleRequest(RabbitMQEvent event, Context context)
	{
		addToDynamoDB = true;
		LambdaLogger logger = context.getLogger();
		try {
			logger.log("Begin Event *************");
			logger.log(objectMapper.writeValueAsString(event));
			logger.log("End Event ***************");
			logger.log("EventSource = " + event.getEventSource());
			logger.log("EventSourceARN = " + event.getEventSourceArn());
			logger.log("Now iterating through Map of all queues");
			Map<String, List<RabbitMQEvent.RabbitMessage>> mapOfMessages = event.getRmqMessagesByQueue();
			mapOfMessages.forEach((k, v) -> {
				String currentQueueName = k.substring(0, k.indexOf("::/"));
				logger.log("Current Queue Name = " + currentQueueName);
				logger.log("Now iterating through each message in this queue - " + currentQueueName);
				for (RabbitMQEvent.RabbitMessage thisMessage: v) {
					logger.log("Now logging a new message");
					String encodedData = thisMessage.getData();
					logger.log("EncodedData = " + encodedData);
					String decodedData = new String(Base64.getDecoder().decode(encodedData));
					logger.log("DecodedData = " + decodedData);
					Person thisPerson = gson.fromJson(decodedData, Person.class);
					logger.log("This person = " + thisPerson.toString());
					logger.log("Whether Redelivered = " + thisMessage.getRedelivered());
					BasicProperties thisMessageProperties = thisMessage.getBasicProperties();
					logger.log("AppID = " + thisMessageProperties.getAppId());
					logger.log("BodySize = " + thisMessageProperties.getBodySize());
					logger.log("ClusterId = " + thisMessageProperties.getClusterId());
					logger.log("ContentEncoding = " + thisMessageProperties.getContentEncoding());
					logger.log("ContentType = " + thisMessageProperties.getContentType());
					logger.log("CorrelationId = " + thisMessageProperties.getCorrelationId());
					logger.log("DeliveryMode = " + thisMessageProperties.getDeliveryMode());
					logger.log("Expiration = " + thisMessageProperties.getExpiration());
					logger.log("MessageId = " + thisMessageProperties.getMessageId());
					logger.log("Priority = " + thisMessageProperties.getPriority());
					logger.log("ReplyTo = " + thisMessageProperties.getReplyTo());
					logger.log("Timestamp = " + thisMessageProperties.getTimestamp());
					logger.log("Type = " + thisMessageProperties.getType());
					logger.log("UserId = " + thisMessageProperties.getUserId());
					logger.log("Now iterating through the headers in this message");
					Map<String, Object> thisMessageHeaders = thisMessageProperties.getHeaders();
					thisMessageHeaders.forEach((headerName, headerValue) -> {
						if (headerValue.getClass().getName().equalsIgnoreCase("java.util.LinkedHashMap")) {
							LinkedHashMap<String, Object> headerValueLinkedHashMap = (LinkedHashMap<String, Object>)headerValue;
							ArrayList<Integer> headerValueArrayList = (ArrayList<Integer>)headerValueLinkedHashMap.get("bytes");
							byte[] headerValueByteArray = new byte[headerValueArrayList.size()];
							int i=0;
							for (Integer thisInteger: headerValueArrayList) {
								headerValueByteArray[i] = thisInteger.byteValue();
								i++;
							}
							String headerValueString = new String(headerValueByteArray);
							logger.log("Header Name = " + headerName + " and Header Value = " + headerValueString);
						} else {
							logger.log("Header Name = " + headerName + " and Header Value = " + headerValue.toString());
						}
					});
					logger.log("Now done iterating through the headers in this message");
					logger.log("Now done logging a new message");
					String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
					if ((null == AWS_SAM_LOCAL) && (addToDynamoDB)) {
						ddbUpdater.insertIntoDynamoDB(thisMessage, gson, logger, System.currentTimeMillis(), currentQueueName, event.getEventSource(), event.getEventSourceArn());
					}
				}
				logger.log("Now done iterating through each message in this queue");	
			});
			logger.log("Done iterating through Map of all queues");
			return "200";
		} catch (Exception e) {
			logger.log("An exception occurred - " + e.getMessage());
			return "500";
		}
		
	}
}
