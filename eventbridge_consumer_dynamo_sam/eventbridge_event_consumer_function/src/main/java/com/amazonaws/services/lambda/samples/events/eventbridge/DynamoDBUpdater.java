package com.amazonaws.services.lambda.samples.events.eventbridge;

import java.util.List;
import java.util.Map;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;

public class DynamoDBUpdater {

	String dynamoDBTableName;
	AmazonDynamoDB client;
	DynamoDB dynamoDB;
	Table dynamoTable;
	

	public DynamoDBUpdater(String dynamoDBTableName) {
		super();
		if (null == dynamoDBTableName) {
			this.dynamoDBTableName = "SNS_LAMBDA_DYNAMO_TABLE";
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
	
	public PutItemOutcome insertIntoDynamoDB(ScheduledEvent event, Gson gson, LambdaLogger logger) {
		logger.log("Now inserting a row in DynamoDB for eventID = " + event.getId());
		Item item = new Item();
		item.withPrimaryKey("EventID", event.getId());
		item.withString("AWSAccount", event.getAccount());
		item.withString("AWSRegion", event.getRegion());
		item.withString("EventSource", event.getSource());
		item.withString("SenderDetailType", event.getDetailType());
		item.withString("EventTime", event.getTime().toString());
		List<String> resources = event.getResources();
		if ((null != resources) && (resources.size() != 0)) {
			for (int i=1;i<=resources.size();i++) {
				item.withString("ResourceNumber_" + i, resources.get(i));
			}
		}
		Map<String, Object> eventDetail = event.getDetail();
		PersonWithKeyAndNumber personWithKeyAndNumber = gson.fromJson(gson.toJson(eventDetail), PersonWithKeyAndNumber.class);
		item.withString("MessageKey", personWithKeyAndNumber.getMessageKey());
		item.withInt("MessageNumber", personWithKeyAndNumber.messageNumber);
		item.withString("Firstname", personWithKeyAndNumber.getPerson().getFirstname());
		item.withString("Lastname", personWithKeyAndNumber.getPerson().getLastname());
		item.withString("Company", personWithKeyAndNumber.getPerson().getCompany());
		item.withString("Street", personWithKeyAndNumber.getPerson().getStreet());
		item.withString("City", personWithKeyAndNumber.getPerson().getCity());
		item.withString("County", personWithKeyAndNumber.getPerson().getCounty());
		item.withString("State", personWithKeyAndNumber.getPerson().getState());
		item.withString("Zip", personWithKeyAndNumber.getPerson().getZip());
		item.withString("Cellphone", personWithKeyAndNumber.getPerson().getCellPhone());
		item.withString("Homephone", personWithKeyAndNumber.getPerson().getHomePhone());
		item.withString("Email", personWithKeyAndNumber.getPerson().getEmail());
		item.withString("Website", personWithKeyAndNumber.getPerson().getWebsite());
	    logger.log("Now finished inserting a row in DynamoDB for eventID = " + event.getId());
	    return dynamoTable.putItem(item);
	}
	
}
