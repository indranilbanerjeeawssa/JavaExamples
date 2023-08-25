package com.amazonaws.services.lambda.samples.events.apigateway.rest.nonproxy;

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
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

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
	
	public PutItemOutcome insertIntoDynamoDB(APIGatewayProxyRequestEvent input, String customerId, PersonWithID thisPerson, LambdaLogger logger) {
		logger.log("Now inserting a row in DynamoDB for CustomerID = " + customerId);
		Item item = new Item();
		item.withPrimaryKey("CustomerID", customerId);
		item.withString("Resource", input.getResource());
		item.withString("Path", input.getPath());
		item.withString("HTTPMethod", input.getHttpMethod());
		item.withString("RequestContextAccountID", input.getRequestContext().getAccountId());
		item.withString("RequestContextStage", input.getRequestContext().getStage());
		item.withString("RequestContextResourceID", input.getRequestContext().getResourceId());
		item.withString("RequestContextRequestID", input.getRequestContext().getRequestId());
		item.withString("RequestContextIdentitySourceIP", input.getRequestContext().getIdentity().getSourceIp());
		item.withString("RequestContextIdentityUserAgent", input.getRequestContext().getIdentity().getUserAgent());
		item.withString("RequestContextResourcePath", input.getRequestContext().getResourcePath());
		item.withString("RequestContextHTTPMethod", input.getRequestContext().getHttpMethod());
		item.withString("RequestContextApiID", input.getRequestContext().getApiId());
		item.withString("RequestContextPath", input.getRequestContext().getPath());
    	Map<String, String> inputHeaders = input.getHeaders();
    	inputHeaders.forEach((k, v) -> {
    		item.withString("Headers" + k, v);
    	});
    	Map<String, List<String>> inputMultiValueHeaders = input.getMultiValueHeaders();
    	inputMultiValueHeaders.forEach((k1, v1) -> {
    		int i= 1;
    		for (String v2 : v1) {
    			item.withString("MultiValueHeaders" + k1 + i, v2);
    			i++;
    		}
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
