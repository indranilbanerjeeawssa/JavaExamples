package com.amazonaws.services.lambda.samples.events.eventbridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.lambda.samples.events.eventbridge.DynamoDBUpdater;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.Gson;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

class HandlerEventbridgeTest {
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

	@Mock
	DynamoDBUpdater ddbUpdater;	
	
	@Test
	@ExtendWith(MockitoExtension.class)
	void invokeTest() {
		ObjectMapper om = new ObjectMapper().registerModule(new JodaModule());
		ScheduledEvent event = null;
		try {
			event = om.readValue(eventbridgeEventJson, ScheduledEvent.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Context context = new TestContext();
		PutItemOutcome putItemOutcome = mock(PutItemOutcome.class);
		DynamoDBUpdater dbUpdater = mock(DynamoDBUpdater.class);
		HandlerEventbridge handler = new HandlerEventbridge();
		handler.ddbUpdater = dbUpdater;
		//when(handler.ddbUpdater.insertIntoDynamoDB(ArgumentMatchers.any(SNSRecord.class), ArgumentMatchers.any(Gson.class), ArgumentMatchers.any(LambdaLogger.class))).thenReturn(putItemOutcome);
		String result = handler.handleRequest(event, context);
		assertEquals(result, "200-OK");
	}

}
