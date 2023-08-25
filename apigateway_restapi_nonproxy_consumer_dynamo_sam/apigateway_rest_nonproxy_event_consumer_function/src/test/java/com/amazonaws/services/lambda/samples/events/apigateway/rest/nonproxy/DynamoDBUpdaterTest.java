package com.amazonaws.services.lambda.samples.events.apigateway.rest.nonproxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.ArgumentMatchers;

class DynamoDBUpdaterTest {

	@Test
	void testDynamoDBUpdater() {
		DynamoDBUpdater ddbUpdater = new DynamoDBUpdater("DBTable");
		assertNotNull(ddbUpdater);
		assertEquals(ddbUpdater.dynamoDBTableName, "DBTable");
		assertNotNull(ddbUpdater.client);
		assertNotNull(ddbUpdater.dynamoDB);
		assertNotNull(ddbUpdater.dynamoTable);
	}

	@Test
	void testInsertIntoDynamoDB() {
		byte[] buffer = null;
		String requestEventJson = "";
		try {
			InputStream fis = HandlerAPIGatewayRESTNonProxyTest.class.getClassLoader().getResourceAsStream("event.json");
			buffer = fis. readAllBytes();
			requestEventJson = new String(buffer);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ObjectMapper om = new ObjectMapper();
		APIGatewayProxyRequestEvent request = null;
		try {
			request = om.readValue(requestEventJson, APIGatewayProxyRequestEvent.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
			Table dynamoDbTable = mock(Table.class);
		    AmazonDynamoDB client = mock(AmazonDynamoDB.class);
			DynamoDB dynamoDB = mock(DynamoDB.class);
		    PutItemOutcome putoutcome = mock(PutItemOutcome.class);
		    LambdaLogger logger = mock(LambdaLogger.class);
		    DynamoDBUpdater ddbUpdater = new DynamoDBUpdater("DBTable");
		    ddbUpdater.client = client;
		    ddbUpdater.dynamoDB = dynamoDB;
		    ddbUpdater.dynamoTable = dynamoDbTable;
		    when(ddbUpdater.dynamoTable.putItem(ArgumentMatchers.any(Item.class))).thenReturn(putoutcome);
		    PersonWithID thisPerson = new PersonWithID();
			try {
				thisPerson = om.readValue(request.getBody(), PersonWithID.class);
			} catch (JsonProcessingException e1) {
				logger.log("An exception occurred while parsing the Json - " + e1.getMessage());
			}
		    PutItemOutcome putOutcome = ddbUpdater.insertIntoDynamoDB(request, java.util.UUID.randomUUID().toString(), thisPerson, logger);
			assertNotNull(putOutcome);
		
	    
	}
}
