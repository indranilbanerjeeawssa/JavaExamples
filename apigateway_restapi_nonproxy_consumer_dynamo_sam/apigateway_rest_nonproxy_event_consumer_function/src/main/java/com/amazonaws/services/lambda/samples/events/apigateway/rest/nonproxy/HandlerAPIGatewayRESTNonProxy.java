package com.amazonaws.services.lambda.samples.events.apigateway.rest.nonproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handler for requests to Lambda function.
 */
public class HandlerAPIGatewayRESTNonProxy implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
	//Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String dynamoDBTableName = System.getenv("DYNAMO_DB_TABLE");
	DynamoDBUpdater ddbUpdater = new DynamoDBUpdater(dynamoDBTableName);
	boolean addToDynamoDB;
	ObjectMapper objectMapper = new ObjectMapper();

	public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
		addToDynamoDB = true;
		LambdaLogger logger = context.getLogger();
		try {
			logger.log("Request = " + objectMapper.writeValueAsString(input));
		} catch (JsonProcessingException e2) {
			logger.log(e2.getMessage());
		}
    	logger.log("Request Body = " + input.getBody());
    	logger.log("Resource = " + input.getResource());
    	logger.log("Path = " + input.getPath());
    	logger.log("HTTPMethod = " + input.getHttpMethod());
    	logger.log("RequestContextAccountID = " + input.getRequestContext().getAccountId());
    	logger.log("RequestContextStage = " + input.getRequestContext().getStage());
    	logger.log("RequestContextResourceID = " + input.getRequestContext().getResourceId());
    	logger.log("RequestContextRequestID = " + input.getRequestContext().getRequestId());
    	logger.log("RequestContextIdentitySourceIP = " + input.getRequestContext().getIdentity().getSourceIp());
    	logger.log("RequestContextIdentityUserAgent = " + input.getRequestContext().getIdentity().getUserAgent());
    	//This particular use-case is API Gateway without Authentication
    	//With Authentication enabled, many more fields under 
    	//input.getRequestContext().getIdentity() will be populated
    	//input.getRequestContext().getIdentity() can also provide Cognito details
    	//if Cognito based authentication is used
    	//input.getRequestContext().getIdentity() can also provide API Key info if enabled
    	logger.log("RequestContextResourcePath = " + input.getRequestContext().getResourcePath());
    	logger.log("RequestContextHTTPMethod = " + input.getRequestContext().getHttpMethod());
    	logger.log("RequestContextApiID = " + input.getRequestContext().getApiId());
    	logger.log("RequestContextPath = " + input.getRequestContext().getPath());
    	Map<String, String> inputHeaders = input.getHeaders();
    	inputHeaders.forEach((k, v) -> {
    		logger.log("Headers" + k + " = " + v);
    	});
    	Map<String, List<String>> inputMultiValueHeaders = input.getMultiValueHeaders();
    	inputMultiValueHeaders.forEach((k1, v1) -> {
    		int i= 1;
    		for (String v2 : v1) {
    			logger.log("MultiValueHeaders" + k1 + i + " = " + v2);
    			i++;
    		}
    	});
    	//PersonWithID thisPerson1 = gson.fromJson(input.getBody(), PersonWithID.class);
    	PersonWithID thisPerson = new PersonWithID();
		try {
			thisPerson = objectMapper.readValue(input.getBody(), PersonWithID.class);
		} catch (JsonProcessingException e1) {
			logger.log("An exception occurred while parsing the Json - " + e1.getMessage());
		}
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
    	String customerId = java.util.UUID.randomUUID().toString();
    	String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
		if ((null == AWS_SAM_LOCAL) && (addToDynamoDB)) {
			ddbUpdater.insertIntoDynamoDB(input, customerId, thisPerson, logger);
		}
    	
    	Map<String, String> outputHeaders = new HashMap<>();
    	outputHeaders.put("Content-Type", "application/json");
    	outputHeaders.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(outputHeaders);
        try {
            
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setCustomerID(customerId);
            responseMessage.setMessage("Successfully created a new customer");
            return response
                    .withStatusCode(201)
                    .withBody(objectMapper.writeValueAsString(responseMessage));
        } catch (Exception e) {
        	ResponseMessage responseMessage = new ResponseMessage();
        	responseMessage.setCustomerID("Could not create a new customer");
            responseMessage.setMessage(e.getMessage());
            try {
				return response
				        .withBody(objectMapper.writeValueAsString(responseMessage))
				        .withStatusCode(500);
			} catch (JsonProcessingException e1) {
				return response
				        .withBody("Could not create a new customer")
				        .withStatusCode(500);
			}
        }
    }

    
}
