package com.amazonaws.services.lambda.samples.events.application.loadbalancer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.samples.events.application.loadbalancer.DynamoDBUpdater;
import com.amazonaws.services.lambda.samples.events.application.loadbalancer.HandlerApplicationLoadbalancer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


public class HandlerApplicationLoadbalancerTest {
	
//	@Mock
//	DynamoDBUpdater ddbUpdater;	
//	
//  @Test
//  @ExtendWith(MockitoExtension.class)
//  public void successfulResponse() {
//	HandlerApplicationLoadbalancer app = new HandlerApplicationLoadbalancer();
//	byte[] buffer = null;
//	String requestEventJson = "";
//	try {
//		InputStream fis = HandlerApplicationLoadbalancerTest.class.getClassLoader().getResourceAsStream("event.json");
//		buffer = fis. readAllBytes();
//		requestEventJson = new String(buffer);
//		fis.close();
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//	ObjectMapper om = new ObjectMapper();
//	APIGatewayProxyRequestEvent request = null;
//	try {
//		request = om.readValue(requestEventJson, APIGatewayProxyRequestEvent.class);
//	} catch (JsonMappingException e) {
//		e.printStackTrace();
//	} catch (JsonProcessingException e) {
//		e.printStackTrace();
//	}
//	Context context = new TestContext();
//	DynamoDBUpdater dbUpdater = mock(DynamoDBUpdater.class);
//	app.ddbUpdater = dbUpdater;
//    APIGatewayProxyResponseEvent result = app.handleRequest(request, context);
//    assertEquals(201, result.getStatusCode().intValue());
//    assertEquals("application/json", result.getHeaders().get("Content-Type"));
//    String content = result.getBody();
//    Map<String, String> contentMap = null;
//    try {
//		contentMap = om.readValue(content, HashMap.class);
//	} catch (JsonMappingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (JsonProcessingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//    if (null != contentMap) {
//    	contentMap.forEach((k, v) -> {
//    		System.out.println(k + " = " + v);
//    		if (k.equalsIgnoreCase("message")) {
//    			assertEquals(v, "Successfully created a new customer");
//    		} else {
//    			assertNotNull(v);
//    		}
//    	});
//    }
//    assertNotNull(content);
//  }
}
