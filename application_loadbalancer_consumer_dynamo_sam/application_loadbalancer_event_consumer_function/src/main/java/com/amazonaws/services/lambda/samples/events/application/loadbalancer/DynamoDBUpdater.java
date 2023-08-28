package com.amazonaws.services.lambda.samples.events.application.loadbalancer;

import java.util.Map;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;

public class DynamoDBUpdater {

	String dynamoDBTableName;
	AmazonDynamoDB client;
	DynamoDB dynamoDB;
	Table dynamoTable;
	

	public DynamoDBUpdater(String dynamoDBTableName) {
		super();
		if (null == dynamoDBTableName) {
			this.dynamoDBTableName = "APIGATEWAY_LAMBDA_DYNAMO_TABLE";
		} else {
			this.dynamoDBTableName = dynamoDBTableName;
		}
		String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
		if (null == AWS_SAM_LOCAL) {
			this.client = AmazonDynamoDBClientBuilder.standard().build();
		} else {
			this.client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(new EndpointConfiguration("http://127.0.0.1:8000", "")).build();
			this.dynamoDBTableName = "APIGATEWAY_LAMBDA_DYNAMO_TABLE";
		}
		this.dynamoDB = new DynamoDB(client);
		this.dynamoTable = dynamoDB.getTable(this.dynamoDBTableName);
	}
	
	public PutItemOutcome insertIntoDynamoDB(ApplicationLoadBalancerRequestEvent input, String customerId, PersonWithID thisPerson, LambdaLogger logger) {
		logger.log("Now inserting a row in DynamoDB for CustomerID = " + customerId);
		Item item = new Item();
		item.withPrimaryKey("CustomerID", customerId);
		item.withString("TargetGroupArn = ", input.getRequestContext().getElb().getTargetGroupArn());
		item.withString("HttpMethod", input.getHttpMethod());
		item.withString("Path", input.getPath());
    	Map<String, String> queryStringParameters = input.getQueryStringParameters();
    	queryStringParameters.forEach((k,v) -> {
    		item.withString("QueryStringParameter_" + k , v);
    	});
    	Map<String, String> inputHeaders = input.getHeaders();
    	inputHeaders.forEach((k, v) -> {
    		item.withString("Headers_" + k, v);
    	});
    	item.withString("PersonID", thisPerson.getId());
    	item.withString("Firstname", thisPerson.getPerson().getFirstname());
    	item.withString("Lastname", thisPerson.getPerson().getLastname());
    	item.withString("Street", thisPerson.getPerson().getStreet());
    	item.withString("City", thisPerson.getPerson().getCity());
    	item.withString("County", thisPerson.getPerson().getCounty());
    	item.withString("State", thisPerson.getPerson().getState());
    	item.withString("Zip", thisPerson.getPerson().getZip());
    	item.withString("CellPhone", thisPerson.getPerson().getCellPhone());
    	item.withString("HomePhone", thisPerson.getPerson().getHomePhone());
    	item.withString("Email", thisPerson.getPerson().getEmail());
    	item.withString("Company", thisPerson.getPerson().getCompany());
    	item.withString("Website", thisPerson.getPerson().getWebsite());
	    logger.log("Now done inserting a row in DynamoDB for CustomerID = " + customerId);
		return dynamoTable.putItem(item);
	}
}
