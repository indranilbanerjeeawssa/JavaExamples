package com.amazonaws.services.lambda.samples.events.eventbridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

import org.mockito.ArgumentMatchers;

class DynamoDBUpdaterTest {
	
	private static final String eventbridgeEventJson = "{\n"
			+ "    \"account\": \"664251831272\",\n"
			+ "    \"region\": \"us-west-2\",\n"
			+ "    \"detail\": {\n"
			+ "        \"person\": {\n"
			+ "            \"firstname\": \"Carma\",\n"
			+ "            \"lastname\": \"Vanheusen\",\n"
			+ "            \"company\": \"Springfield Div Oh Edison Co\",\n"
			+ "            \"street\": \"68556 Central Hwy\",\n"
			+ "            \"city\": \"San Leandro\",\n"
			+ "            \"county\": \"Alameda\",\n"
			+ "            \"state\": \"CA\",\n"
			+ "            \"zip\": \"94577\",\n"
			+ "            \"homePhone\": \"510-503-7169\",\n"
			+ "            \"cellPhone\": \"510-452-4835\",\n"
			+ "            \"email\": \"carma@cox.net\",\n"
			+ "            \"website\": \"http://www.springfielddivohedisonco.com\"\n"
			+ "        },\n"
			+ "        \"messageKey\": \"TestKey06-06-01-2023-07-06-60\",\n"
			+ "        \"messageNumber\": 95\n"
			+ "    },\n"
			+ "    \"detailType\": \"eventbridge.producer.PersonWithKeyAndNumber\",\n"
			+ "    \"source\": \"eventbridge.producer.JsonEventbridgeProducer\",\n"
			+ "    \"id\": \"6df79d5f-e740-b135-1088-d3a1f948eda3\",\n"
			+ "    \"time\": 1685604509000,\n"
			+ "    \"resources\": []\n"
			+ "}";

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
		ObjectMapper om = new ObjectMapper().registerModule(new JodaModule());
		ScheduledEvent event = null;
		try {
			event = om.readValue(eventbridgeEventJson, ScheduledEvent.class);
			Table dynamoDbTable = mock(Table.class);
		    AmazonDynamoDB client = mock(AmazonDynamoDB.class);
			DynamoDB dynamoDB = mock(DynamoDB.class);
		    PutItemOutcome putoutcome = mock(PutItemOutcome.class);
		    LambdaLogger logger = mock(LambdaLogger.class);
		    DynamoDBUpdater ddbUpdater = new DynamoDBUpdater("DBTable");
		    ddbUpdater.client = client;
		    ddbUpdater.dynamoDB = dynamoDB;
		    ddbUpdater.dynamoTable = dynamoDbTable;
		    PersonWithKeyAndNumber personWithKeyAndNumber = om.readValue(om.writeValueAsString(event.getDetail()), PersonWithKeyAndNumber.class);
		    when(ddbUpdater.dynamoTable.putItem(ArgumentMatchers.any(Item.class))).thenReturn(putoutcome);
			PutItemOutcome putOutcome = ddbUpdater.insertIntoDynamoDB(event, personWithKeyAndNumber, logger);
			assertNotNull(putOutcome);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}


		
	    
	}
}
