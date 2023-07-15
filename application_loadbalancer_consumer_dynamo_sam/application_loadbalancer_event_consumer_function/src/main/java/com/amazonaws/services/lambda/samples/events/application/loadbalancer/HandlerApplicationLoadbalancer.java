package com.amazonaws.services.lambda.samples.events.application.loadbalancer;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Handler for requests to Lambda function.
 */
public class HandlerApplicationLoadbalancer implements RequestHandler<ApplicationLoadBalancerRequestEvent, ApplicationLoadBalancerResponseEvent> {
    
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper();

	public ApplicationLoadBalancerResponseEvent handleRequest(final ApplicationLoadBalancerRequestEvent input, final Context context) {
		addToDynamoDB = true;
		LambdaLogger logger = context.getLogger();
    	
		logger.log("Request = " + gson.toJson(input));
		logger.log(input.getBody());
    	logger.log("TargetGroupArn = " + input.getRequestContext().getElb().getTargetGroupArn());
    	logger.log("HttpMethod" + input.getHttpMethod());
    	logger.log("Path = " + input.getPath());
    	Map<String, String> queryStringParameters = input.getQueryStringParameters();
    	queryStringParameters.forEach((k,v) -> {
    		logger.log("QueryStringParameter_" + k + " = " + v);
    	});
    	Map<String, String> headers = input.getHeaders();
    	headers.forEach((k, v) -> {
    		logger.log("Header_" + k + " = " + v);
    	});
    	PersonWithID thisPerson = gson.fromJson(input.getBody(), PersonWithID.class);
    	logger.log("PersonID = " + thisPerson.getId());
    	logger.log("Firstname = " + thisPerson.getPerson().getFirstname());
    	logger.log("Lastname = " + thisPerson.getPerson().getLastname());
    	logger.log("Street = " + thisPerson.getPerson().getStreet());
    	logger.log("City = " + thisPerson.getPerson().getCity());
    	logger.log("County = " + thisPerson.getPerson().getCounty());
    	logger.log("State = " + thisPerson.getPerson().getState());
    	logger.log("Zip = " + thisPerson.getPerson().getZip());
    	logger.log("CellPhone = " + thisPerson.getPerson().getCellPhone());
    	logger.log("HomePhone = " + thisPerson.getPerson().getHomePhone());
    	logger.log("Email = " + thisPerson.getPerson().getEmail());
    	logger.log("Company = " + thisPerson.getPerson().getCompany());
    	logger.log("Website = " + thisPerson.getPerson().getWebsite());
    	logger.log("IsBase64Encoded = " + input.getIsBase64Encoded());
    	String customerId = java.util.UUID.randomUUID().toString();
    	String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
		if ((null == AWS_SAM_LOCAL) && (addToDynamoDB)) {
			ddbUpdater.insertIntoDynamoDB(input, customerId, gson, logger);
		}
    	
    	Map<String, String> outputHeaders = new HashMap<>();
    	outputHeaders.put("Content-Type", "application/json");
    	outputHeaders.put("X-Custom-Header", "application/json");
    	ApplicationLoadBalancerResponseEvent response = new ApplicationLoadBalancerResponseEvent();
                
        try {
            
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setCustomerID(customerId);
            responseMessage.setMessage("Successfully created a new customer");
            response.setBody(gson.toJson(responseMessage));
            response.setStatusCode(201);
            response.setHeaders(outputHeaders);
            response.setStatusDescription("Successfully created a new customer with ID = " + customerId);
            return response;
                    
        } catch (Exception e) {
        	ResponseMessage responseMessage = new ResponseMessage();
        	responseMessage.setCustomerID("Could not create a new customer");
            responseMessage.setMessage(e.getMessage());
            response.setStatusCode(500);
            response.setHeaders(outputHeaders);
            response.setStatusDescription("Internal Error: Could not create a new customer");
            return response;
        }
    }

    
}
