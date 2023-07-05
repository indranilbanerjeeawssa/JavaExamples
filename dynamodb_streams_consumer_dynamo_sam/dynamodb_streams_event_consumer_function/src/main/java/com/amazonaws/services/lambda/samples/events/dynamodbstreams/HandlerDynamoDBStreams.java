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

package com.amazonaws.services.lambda.samples.events.dynamodbstreams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Handler value: example.HandlerSQS
public class HandlerDynamoDBStreams implements RequestHandler<DynamodbEvent, String>{
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public String handleRequest(DynamodbEvent event, Context context)
	{
		LambdaLogger logger = context.getLogger();
		logger.log("Begin Event *************");
		try {
			logger.log(objectMapper.writeValueAsString(event));
		} catch (JsonProcessingException e1) {
			logger.log(e1.getMessage());
		}
		logger.log("End Event ***************");
		try {
			List<DynamodbStreamRecord> records = event.getRecords();
			for (DynamodbStreamRecord record : records){
				logger.log("Now going to log a new message");
				logger.log("EventID = " + record.getEventID());
			    logger.log("EventName = " + record.getEventName());
			    logger.log("AWSRegion = " + record.getAwsRegion());
			    logger.log("EventSource = " + record.getEventSource());
			    logger.log("EventVersion = " + record.getEventVersion());
			    if (null!=record.getUserIdentity()) {
			    	logger.log("UserIdentityPrincipal = " + record.getUserIdentity().getPrincipalId());
			        logger.log("UserIdentityType = " + record.getUserIdentity().getType());
			    } else {
			    	logger.log("UserIdentityPrincipal = null");
			    	logger.log("UserIdentityType = null");
			    }
			    logger.log("DynamoDBSequenceNumber = " + record.getDynamodb().getSequenceNumber());
			    logger.log("StreamViewType = " + record.getDynamodb().getStreamViewType());
			    logger.log("CreationDateTime = " + record.getDynamodb().getApproximateCreationDateTime().toString());
			    logger.log("SizeBytes = " + record.getDynamodb().getSizeBytes());
			    if (null == record.getDynamodb().getOldImage()) {
			    	logger.log("OldImage = null");
			    } else {
			    	Map<String, AttributeValue> oldImage = record.getDynamodb().getOldImage();
			    	logger.log("Now going to call logger for OldImage");
			    	logMapDynamoDBRecordValues(oldImage, logger);
			    }
			    if (null == record.getDynamodb().getKeys()) {
			    	logger.log("Keys = null");
			    } else {
			    	Map<String, AttributeValue> keys = record.getDynamodb().getKeys();
			    	logger.log("Now going to call logger for Keys");
				    logMapDynamoDBRecordValues(keys, logger);
			    }
			    if (null == record.getDynamodb().getNewImage()) {
			    	logger.log("NewImage = null");
			    } else {
			    	Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
			    	logger.log("Now going to call logger for NewImage");
			    	logMapDynamoDBRecordValues(newImage, logger);
			    }
			    logger.log("Now done logging a new message");
			}
		} catch (Exception e) {
			logger.log("An exception occurred - " + e.getMessage());
			return "500-ERROR";
		}
		return "200-OK";
	}
	
	public void logMapDynamoDBRecordValues(Map<String, AttributeValue> mapOfMessages, LambdaLogger logger) {
		try {
			mapOfMessages.forEach((k, v) -> {
				if (null == k){
					logger.log("Key = null");
				} else if (null == v) {
					logger.log("Value = null");
				} else if (null != v.getNULL()) {
					logger.log("Key = " + k + " and Value = null");
				} else if (null != v.getBOOL()) {
					logger.log("Key = " + k + " and Value = " + v.getBOOL().toString());
				} else if (null != v.getS()) {
					logger.log("Key = " + k + " and Value = " + v.getS());
				} else if (null != v.getN()) {
					logger.log("Key = " + k + " and Value = " + v.getN());
				} else if (null != v.getB()) {
					ByteBuffer bb = v.getB().asReadOnlyBuffer();
			        String s = "Could not decrypt binary data";
					try {
						s = uncompressString(bb);
					} catch (IOException e) {
						logger.log("Could not decrypt binary data - " + e.getMessage());
					}
			        logger.log("Key = " + k + " and Base64 Encoded Value of Binary = " + s);
				} else if (null != v.getM()) {
					logMapDynamoDBRecordValues(v.getM(), logger);
				} else if (null != v.getSS()) {
					int i=1;
					for (String s: v.getSS()) {
						logger.log("Key = " + k + "-" + i + " and value = " + s);
						i++;
					}
				} else if (null != v.getNS()) {
					int i=1;
					for (String s: v.getNS()) {
						logger.log("Key = " + k + "-" + i + " and value = " + s);
						i++;
					}
				} else if (null!= v.getL()) {
					int i=1;
					Map<String, AttributeValue> subMap = new HashMap<String, AttributeValue>();
					for (AttributeValue a: v.getL()) {
						subMap.put(k + "-" + i, a);
						i++;
					}
					logMapDynamoDBRecordValues(subMap, logger);
				} else if (null != v.getBS()) {
					int i=1;
					for (ByteBuffer bb: v.getBS()) {
						ByteBuffer bb1 = bb.asReadOnlyBuffer();
				        String s = "Could not decrypt binary data";
						try {
							s = uncompressString(bb1);
						} catch (IOException e) {
							logger.log("Could not decrypt binary data - " + e.getMessage());
						}
				        logger.log("Key = " + k + "-" + i + " and Base64 Encoded Value of Binary = " + s);
					}
				}
			});
		} catch (Exception e) {
			logger.log(e.getMessage());
			logger.log(e.toString());
		}
	}
	
	public void throwit(String message) throws Exception{
		throw new Exception(message);
	}
	
	//uncompressString code taken as-is from https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaDocumentAPIBinaryTypeExample.html
	private String uncompressString(ByteBuffer input) throws IOException {
        byte[] bytes = input.array();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream is = new GZIPInputStream(bais);
        int chunkSize = 1024;
        byte[] buffer = new byte[chunkSize];
        int length = 0;
        while ((length = is.read(buffer, 0, chunkSize)) != -1) {
            baos.write(buffer, 0, length);
        }
        String result = new String(baos.toByteArray(), "UTF-8");
        is.close();
        baos.close();
        bais.close();
        return result;
    }
}
