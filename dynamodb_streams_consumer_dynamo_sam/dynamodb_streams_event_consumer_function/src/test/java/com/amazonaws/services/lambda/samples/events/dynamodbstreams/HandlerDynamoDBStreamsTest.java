package com.amazonaws.services.lambda.samples.events.dynamodbstreams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

class HandlerDynamoDBStreamsTest {
	
	@Mock
	DynamoDBUpdater ddbUpdater;	
	
	@Test
	@ExtendWith(MockitoExtension.class)
	void invokeTest() {
		byte[] buffer = null;
		String dynamodbStreamsEventJson = "";
		try {
			InputStream fis = DynamoDBUpdaterTest.class.getClassLoader().getResourceAsStream("event.json");
			buffer = fis. readAllBytes();
			dynamodbStreamsEventJson = new String(buffer);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ObjectMapper om = new ObjectMapper();
		DynamodbEvent event = null;
		try {
			event = om.readValue(dynamodbStreamsEventJson, DynamodbEvent.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Context context = new TestContext();
		DynamoDBUpdater dbUpdater = mock(DynamoDBUpdater.class);
		HandlerDynamoDBStreams handler = new HandlerDynamoDBStreams();
		handler.ddbUpdater = dbUpdater;
		String result = handler.handleRequest(event, context);
		assertEquals(result, "200-OK");
	}

}
