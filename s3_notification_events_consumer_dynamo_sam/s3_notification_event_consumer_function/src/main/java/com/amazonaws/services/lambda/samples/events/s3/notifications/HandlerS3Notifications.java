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

package com.amazonaws.services.lambda.samples.events.s3.notifications;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

// Handler value: example.HandlerSQS
public class HandlerS3Notifications implements RequestHandler<S3Event, String>{
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());
	@Override
	public String handleRequest(S3Event event, Context context)
	{
		LambdaLogger logger = context.getLogger();
		logger.log("Begin Event *************");
		logger.log("Logging Event Using Jackson");
		try {
			logger.log(objectMapper.writeValueAsString(event));
		} catch (JsonProcessingException e1) {
			logger.log(e1.getMessage());
		}
		logger.log("Logging Event Using Gson");
		logger.log(gson.toJson(event));
		logger.log("End Event ***************");
		for(S3EventNotificationRecord msg : event.getRecords()){
			try {
				addToDynamoDB = true;
				logger.log("AWSRegion = " + msg.getAwsRegion());
				logger.log("EventName = " + msg.getEventName());
				logger.log("EventSource = " + msg.getEventSource());
				logger.log("EventVersion = " + msg.getEventVersion());
				logger.log("EventTime = " + msg.getEventTime().toString());
				logger.log("SourceIPAddress = " + msg.getRequestParameters().getSourceIPAddress());
				logger.log("xAmzId2 = " + msg.getResponseElements().getxAmzId2());
				logger.log("xAmzRequestId" + msg.getResponseElements().getxAmzRequestId());
				logger.log("ConfigurationId = " + msg.getS3().getConfigurationId());
				logger.log("S3SchemaVersion = " + msg.getS3().getS3SchemaVersion());
				logger.log("BucketARN = " + msg.getS3().getBucket().getArn());
				logger.log("BucketName = " + msg.getS3().getBucket().getName());
				logger.log("BucketOwnerPrincipalID = " + msg.getS3().getBucket().getOwnerIdentity().getPrincipalId());
				logger.log("ObjectETag = " + msg.getS3().getObject().geteTag());
				logger.log("ObjectKey = " + msg.getS3().getObject().getKey());
				logger.log("ObjectSequencer = " + msg.getS3().getObject().getSequencer());
				logger.log("ObjectUrlDecodedKey = " + msg.getS3().getObject().getUrlDecodedKey());
				logger.log("ObjectVersionID = " + msg.getS3().getObject().getVersionId());
				logger.log("ObjectSize = " + msg.getS3().getObject().getSizeAsLong().toString());
				logger.log("PrincipalID = " + msg.getUserIdentity().getPrincipalId());
				String data = getS3Object(msg.getAwsRegion(), msg.getS3().getBucket().getName(), msg.getS3().getObject().getKey());
				logger.log("ObjectData = " + data);
				Person thisPerson = gson.fromJson(data, Person.class);
				logger.log("Firstname = " + thisPerson.getFirstname());
				logger.log("Lastname = " + thisPerson.getLastname());
				logger.log("Street = " + thisPerson.getStreet());
				logger.log("City = " + thisPerson.getCity());
				logger.log("County = " + thisPerson.getCounty());
				logger.log("State = " + thisPerson.getState());
				logger.log("Zip = " + thisPerson.getZip());
				logger.log("HomePhone = " + thisPerson.getHomePhone());
				logger.log("CellPhone = " + thisPerson.getCellPhone());
				logger.log("Email = " + thisPerson.getEmail());
				logger.log("Company = " + thisPerson.getCompany());
				logger.log("Website = " + thisPerson.getWebsite());
				String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
				if ((null == AWS_SAM_LOCAL) && (addToDynamoDB)) {
					ddbUpdater.insertIntoDynamoDB(msg, thisPerson, logger);
				}
			} catch (Exception e) {
				logger.log("An exception occurred while processing this SQS message - " + e.getMessage());
				return "500-ERROR";
			}
		}
		return "200-OK";
	}
	
	public String getS3Object (String region, String bucket, String object) {
		String objectValue = "";
		S3Client s3Client = S3Client.builder().region(Region.of(region)).build();
		GetObjectRequest objectRequest = GetObjectRequest
				                         .builder()
				                         .bucket(bucket)
				                         .key(object)
				                         .build();
		ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
        byte[] data = objectBytes.asByteArray();
        objectValue = new String(data);
		return objectValue;
	}
	
	public void throwit(String message) throws Exception{
		throw new Exception(message);
	}
}
