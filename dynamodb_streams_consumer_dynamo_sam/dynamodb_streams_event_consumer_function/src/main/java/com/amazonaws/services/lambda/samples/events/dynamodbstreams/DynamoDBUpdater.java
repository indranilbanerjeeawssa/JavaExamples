package com.amazonaws.services.lambda.samples.events.dynamodbstreams;


import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.google.gson.Gson;

public class DynamoDBUpdater {

	String dynamoDBTableName;
	AmazonDynamoDB client;
	DynamoDB dynamoDB;
	Table dynamoTable;
	

	public DynamoDBUpdater(String dynamoDBTableName) {
		super();
		if (null == dynamoDBTableName) {
			this.dynamoDBTableName = "SQS_LAMBDA_DYNAMO_TABLE";
		} else {
			this.dynamoDBTableName = dynamoDBTableName;
		}
		String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
		if (null == AWS_SAM_LOCAL) {
			this.client = AmazonDynamoDBClientBuilder.standard().build();
		} else {
			this.client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(new EndpointConfiguration("http://127.0.0.1:8000", "")).build();
			this.dynamoDBTableName = "SQS_LAMBDA_DYNAMO_TABLE";
		}		
		this.dynamoDB = new DynamoDB(client);
		this.dynamoTable = dynamoDB.getTable(this.dynamoDBTableName);
	}
	
	public PutItemOutcome insertIntoDynamoDB(DynamodbStreamRecord record, Gson gson, LambdaLogger logger) {
		logger.log("Now inserting a row in DynamoDB for messageID = " + record.getEventID());
		Item item = new Item();
		item.withPrimaryKey("MessageID", record.getEventID());
		
		
		item.withString("EventID", record.getEventID());
		item.withString("EventName", record.getEventName());
		item.withString("AWSRegion", record.getAwsRegion());
		item.withString("EventSource", record.getEventSource());
		item.withString("EventVersion", record.getEventVersion());
	    if (null!=record.getUserIdentity()) {
	    	item.withString("UserIdentityPrincipal", record.getUserIdentity().getPrincipalId());
	    	item.withString("UserIdentityType", record.getUserIdentity().getType());
	    } else {
	    	item.withNull("UserIdentityPrincipal");
	    	item.withNull("UserIdentityType");
	    }
	    item.withString("DynamoDBSequenceNumber", record.getDynamodb().getSequenceNumber());
	    item.withString("StreamViewType", record.getDynamodb().getStreamViewType());
	    item.withString("CreationDateTime", record.getDynamodb().getApproximateCreationDateTime().toString());
	    item.withLong("SizeBytes", record.getDynamodb().getSizeBytes());
	    if (null == record.getDynamodb().getOldImage()) {
	    	item.withNull("OldImage");
	    } else {
	    	Map<String, AttributeValue> oldImage = record.getDynamodb().getOldImage();
	    	logMapDynamoDBRecordValues(oldImage, logger, item);
	    }
	    if (null == record.getDynamodb().getKeys()) {
	    	item.withNull("Keys");
	    } else {
	    	Map<String, AttributeValue> keys = record.getDynamodb().getKeys();
		    logMapDynamoDBRecordValues(keys, logger, item);
	    }
	    if (null == record.getDynamodb().getNewImage()) {
	    	item.withNull("NewImage");
	    } else {
	    	Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
	    	logMapDynamoDBRecordValues(newImage, logger, item);
	    }
	    logger.log("Now done inserting a row in DynamoDB for messageID = " + record.getEventID());
		return dynamoTable.putItem(item);
	}
	
	public void logMapDynamoDBRecordValues(Map<String, AttributeValue> mapOfMessages, LambdaLogger logger, Item item) {
		try {
			mapOfMessages.forEach((k, v) -> {
				if (null == v) {
					item.withNull(k);
				} else if (null != v.getNULL()) {
					item.withNull(k);
				} else if (null != v.getBOOL()) {
					item.withBoolean(k, v.getBOOL().booleanValue());
				} else if (null != v.getS()) {
					item.withString(k, v.getS());
				} else if (null != v.getN()) {
					item.withString(k, v.getN());
				} else if (null != v.getB()) {
					ByteBuffer bb = v.getB();
			        String s = "Could not decrypt binary data";
					try {
						byte[] thisByteArray = bb.array();
						s = Base64.getEncoder().encodeToString(thisByteArray);
					} catch (Exception e) {
						item.withString(k, "Could not decrypt binary data - " + e.getMessage());
					}
			        item.withString(k, s);
				} else if (null != v.getM()) {
					logMapDynamoDBRecordValues(v.getM(), logger, item);
				} else if (null != v.getSS()) {
					int i=1;
					for (String s: v.getSS()) {
						item.withString(k + "-" + i, s);
						i++;
					}
				} else if (null != v.getNS()) {
					int i=1;
					for (String s: v.getNS()) {
						item.withString(k + "-" + i, s);
						i++;
					}
				} else if (null!= v.getL()) {
					int i=1;
					Map<String, AttributeValue> subMap = new HashMap<String, AttributeValue>();
					for (AttributeValue a: v.getL()) {
						subMap.put(k + "-" + i, a);
						i++;
					}
					logMapDynamoDBRecordValues(subMap, logger, item);
				} else if (null != v.getBS()) {
					int i=1;
					for (ByteBuffer bb: v.getBS()) {
				        String s = "Could not decrypt binary data";
						try {
							byte[] thisByteArray = bb.array();
							s = Base64.getEncoder().encodeToString(thisByteArray);
						} catch (Exception e) {
							item.withString(k + "-" + i, "Could not decrypt binary data - " + e.getMessage());
						}
				        item.withString(k + "-" + i, s);
					}
				}
			});
		} catch (Exception e) {
			logger.log(e.getMessage());
			logger.log(e.toString());
		}
	}
	
}
