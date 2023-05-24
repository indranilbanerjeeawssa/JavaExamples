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

package com.amazonaws.services.lambda.samples.events.kinesis;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.events.StreamsEventResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HandlerKinesis implements RequestHandler<KinesisEvent, StreamsEventResponse>{
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public StreamsEventResponse handleRequest(KinesisEvent event, Context context)
	{
		List<StreamsEventResponse.BatchItemFailure> batchItemFailures = new ArrayList<StreamsEventResponse.BatchItemFailure>();
		LambdaLogger logger = context.getLogger();
		logger.log("Begin Event *************");
		try {
			logger.log(objectMapper.writeValueAsString(event));
		} catch (JsonProcessingException e1) {
			logger.log(e1.getMessage());
		}
		logger.log("End Event ***************");
		for(KinesisEvent.KinesisEventRecord msg : event.getRecords()){
			try {
				addToDynamoDB = true;
				logger.log("Begin Message *************");
				logger.log(objectMapper.writeValueAsString(msg));
				logger.log("End Message ***************");
				logger.log("Begin Kinesis Record *************");
				KinesisEvent.Record kinesisRecord = msg.getKinesis();
				logger.log(objectMapper.writeValueAsString(kinesisRecord));
				logger.log("End Kinesis Record ***************");
				final byte[] bytes = new byte[kinesisRecord.getData().remaining()];
				kinesisRecord.getData().duplicate().get(bytes);
				String payload = new String(bytes);
				logger.log("Begin Message Body *************");
				logger.log(payload);
				logger.log("End Message Body ***************");
				logger.log("Event Source = " + msg.getEventSource());
				logger.log("Event ID = " + msg.getEventID());
				logger.log("Event Name = " + msg.getEventName());
				logger.log("Event Version = " + msg.getEventVersion());
				logger.log("Event Source ARN = " + msg.getEventSourceARN());
				logger.log("AWS Region = " + msg.getAwsRegion());
				logger.log("Invoke Identity ARN = " + msg.getInvokeIdentityArn());
				logger.log("Sequence Number = " + msg.getKinesis().getSequenceNumber());
				logger.log("Approximate Arrival Timestamp = " + msg.getKinesis().getApproximateArrivalTimestamp());
				logger.log("Partition Key = " + msg.getKinesis().getPartitionKey());
				if (null == msg.getKinesis().getEncryptionType()) {
					logger.log("Encryption Type = null");
				} else {
					logger.log("Encryption Type = " + msg.getKinesis().getEncryptionType());
				}
				Person person = gson.fromJson(payload, Person.class);
				logger.log("Person details = " + person.toString());
			} catch (Exception e) {
				logger.log("An exception occurred while processing this Kinesis message - " + e.toString());
				batchItemFailures.add(new StreamsEventResponse.BatchItemFailure(msg.getKinesis().getSequenceNumber()));
				logger.log("Added message with sequenceNumber = " + msg.getKinesis().getSequenceNumber() + " to batchItemFailures list");
			}
		}
		return new StreamsEventResponse(batchItemFailures);
	}
	
	public void throwit(String message) throws Exception{
		throw new Exception(message);
	}
}
