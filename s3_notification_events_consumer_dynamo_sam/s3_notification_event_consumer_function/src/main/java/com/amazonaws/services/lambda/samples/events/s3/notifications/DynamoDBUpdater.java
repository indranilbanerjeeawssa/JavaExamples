package com.amazonaws.services.lambda.samples.events.s3.notifications;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

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
	
	public PutItemOutcome insertIntoDynamoDB(S3EventNotificationRecord msg, Person thisPerson, LambdaLogger logger) {
		logger.log("Now inserting a row in DynamoDB for messageID = " + msg.getS3().getObject().getKey());
		Item item = new Item();
		item.withPrimaryKey("MessageID", msg.getS3().getObject().getKey());
		item.withString("AWSRegion", msg.getAwsRegion());
		item.withString("EventName", msg.getEventName());
		item.withString("EventSource", msg.getEventSource());
		item.withString("EventVersion", msg.getEventVersion());
		item.withString("EventTime", msg.getEventTime().toString());
		item.withString("SourceIPAddress", msg.getRequestParameters().getSourceIPAddress());
		item.withString("xAmzId2", msg.getResponseElements().getxAmzId2());
		item.withString("xAmzRequestId", msg.getResponseElements().getxAmzRequestId());
		item.withString("ConfigurationId", msg.getS3().getConfigurationId());
		item.withString("S3SchemaVersion", msg.getS3().getS3SchemaVersion());
		item.withString("BucketARN", msg.getS3().getBucket().getArn());
		item.withString("BucketName", msg.getS3().getBucket().getName());
		item.withString("BucketOwnerPrincipalID", msg.getS3().getBucket().getOwnerIdentity().getPrincipalId());
		item.withString("ObjectETag", msg.getS3().getObject().geteTag());
		item.withString("ObjectKey", msg.getS3().getObject().getKey());
		item.withString("ObjectSequencer", msg.getS3().getObject().getSequencer());
		item.withString("ObjectUrlDecodedKey", msg.getS3().getObject().getUrlDecodedKey());
		item.withString("ObjectVersionID", msg.getS3().getObject().getVersionId());
		item.withString("ObjectSize", msg.getS3().getObject().getSizeAsLong().toString());
		item.withString("PrincipalID", msg.getUserIdentity().getPrincipalId());
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
	    logger.log("Now done inserting a row in DynamoDB for messageID = " + msg.getS3().getObject().getKey());
		return dynamoTable.putItem(item);
	}
	
}
