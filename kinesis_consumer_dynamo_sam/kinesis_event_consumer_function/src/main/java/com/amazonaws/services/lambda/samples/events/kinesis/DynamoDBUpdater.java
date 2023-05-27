package com.amazonaws.services.lambda.samples.events.kinesis;


import java.text.DateFormat;
import java.util.Map;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
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
		//this.client = AmazonDynamoDBClientBuilder.standard().build();
		this.dynamoDB = new DynamoDB(client);
		this.dynamoTable = dynamoDB.getTable(this.dynamoDBTableName);
	}
	
	public PutItemOutcome insertIntoDynamoDB(KinesisEvent.KinesisEventRecord msg, Gson gson, LambdaLogger logger) {
		logger.log("Now inserting a row in DynamoDB for message with sequence number = " + msg.getKinesis().getSequenceNumber());
		Item item = new Item();
		KinesisEvent.Record kinesisRecord = msg.getKinesis();
		item.withPrimaryKey("PartitionKey", msg.getKinesis().getPartitionKey());
		item.withString("Sequence Number", msg.getKinesis().getSequenceNumber());
		item.withString("EventSourceARN", msg.getEventSourceARN());
		item.withString("EventSource", msg.getEventSource());
		item.withString("EventName", msg.getEventName());
		item.withString("EventVersion", msg.getEventVersion());
		item.withString("AWSRegion", msg.getAwsRegion());
		item.withString("EventID", msg.getEventID());
		item.withString("InvokeIdentityARN", msg.getInvokeIdentityArn());
		item.withString("ApproximateArrivalTimestamp", (msg.getKinesis().getApproximateArrivalTimestamp().toString()));
		String encryptionType = "null";
		if (null != msg.getKinesis().getEncryptionType()) {
			encryptionType = msg.getKinesis().getEncryptionType();
		}
		item.withString("EncryptionType", encryptionType);
		final byte[] bytes = new byte[kinesisRecord.getData().remaining()];
		kinesisRecord.getData().duplicate().get(bytes);
		String payload = new String(bytes);
		Person thisPerson = gson.fromJson(payload, Person.class);
		item.withString("Firstname", thisPerson.getFirstname());
		item.withString("Lastname", thisPerson.getLastname());
		item.withString("Company", thisPerson.getCompany());
		item.withString("Address_Street", thisPerson.getStreet());
		item.withString("City", thisPerson.getCity());
		item.withString("County", thisPerson.getCounty());
		item.withString("State", thisPerson.getState());
		item.withString("Zip", thisPerson.getZip());
		item.withString("Home_Phone", thisPerson.getHomePhone());
		item.withString("Cell_Phone", thisPerson.getCellPhone());
		item.withString("Email", thisPerson.getEmail());
		item.withString("Website", thisPerson.getWebsite());
		item.withLong("TimeOfInsertion", System.currentTimeMillis());
		logger.log("Now done inserting a row in DynamoDB for message with sequence number = " + msg.getKinesis().getSequenceNumber());
		return dynamoTable.putItem(item);
	}
	
}
