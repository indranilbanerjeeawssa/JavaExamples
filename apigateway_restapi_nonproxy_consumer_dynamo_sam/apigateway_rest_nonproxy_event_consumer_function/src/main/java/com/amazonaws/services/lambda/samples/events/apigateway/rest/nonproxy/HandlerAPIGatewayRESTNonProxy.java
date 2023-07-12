package com.amazonaws.services.lambda.samples.events.apigateway.rest.nonproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

/**
 * Handler for requests to Lambda function.
 */
public class HandlerAPIGatewayRESTNonProxy implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
	Gson gson = new Gson();

	public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

		LambdaLogger logger = context.getLogger();
    	logger.log("Request = " + gson.toJson(input));
    	logger.log("Request Body = " + input.getBody());
    	
    	Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
        	
            String customerId = java.util.UUID.randomUUID().toString();
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setCustomerID(customerId);
            responseMessage.setMessage("Successfully create a new customer");
            return response
                    .withStatusCode(201)
                    .withBody(gson.toJson(responseMessage));
        } catch (Exception e) {
        	ResponseMessage responseMessage = new ResponseMessage();
        	responseMessage.setCustomerID("Could Not Create New Customer");
            responseMessage.setMessage(e.getMessage());
            return response
                    .withBody(gson.toJson(responseMessage))
                    .withStatusCode(500);
        }
    }

    
}
