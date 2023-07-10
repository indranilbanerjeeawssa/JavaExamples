package com.amazonaws.services.lambda.samples.events.s3.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

class HandlerS3NotificationsTest {

	@Mock
	DynamoDBUpdater ddbUpdater;	
	
	@Test
	@ExtendWith(MockitoExtension.class)
	void invokeTest() {
		S3Event event = EventLoader.loadS3Event("event.json");
		Context context = new TestContext();
		DynamoDBUpdater dbUpdater = mock(DynamoDBUpdater.class);
		HandlerS3Notifications handler = new HandlerS3Notifications();
		handler.ddbUpdater = dbUpdater;
		String result = handler.handleRequest(event, context);
		assertEquals(result, "200-OK");
	}

}
