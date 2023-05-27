package com.amazonaws.services.lambda.samples.events.kinesis;

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
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mockito.ArgumentMatchers;

class DynamoDBUpdaterTest {
	
	private static final String kinesisEventJson = "{\n"
			+ "   \"records\":[\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494050005413302710507151556610\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913019943,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJMZW5uYSIsImxhc3RuYW1lIjoiUGFwcm9ja2kiLCJjb21wYW55IjoiRmVsdHogUHJpbnRpbmcgU2VydmljZSIsInN0cmVldCI6IjYzOSBNYWluIFN0IiwiY2l0eSI6IkFuY2hvcmFnZSIsImNvdW50eSI6IkFuY2hvcmFnZSIsInN0YXRlIjoiQUsiLCJ6aXAiOiI5OTUwMSIsImhvbWVQaG9uZSI6IjkwNy0zODUtNDQxMiIsImNlbGxQaG9uZSI6IjkwNy05MjEtMjAxMCIsImVtYWlsIjoibHBhcHJvY2tpQGhvdG1haWwuY29tIiwid2Vic2l0ZSI6Imh0dHA6Ly93d3cuZmVsdHpwcmludGluZ3NlcnZpY2UuY29tIn0=\",\n"
			+ "            \"partitionKey\":\"TestKey033\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494050005413302710507151556610\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494051214339122325136326262786\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913019957,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJEb25ldHRlIiwibGFzdG5hbWUiOiJGb2xsZXIiLCJjb21wYW55IjoiUHJpbnRpbmcgRGltZW5zaW9ucyIsInN0cmVldCI6IjM0IENlbnRlciBTdCIsImNpdHkiOiJIYW1pbHRvbiIsImNvdW50eSI6IkJ1dGxlciIsInN0YXRlIjoiT0giLCJ6aXAiOiI0NTAxMSIsImhvbWVQaG9uZSI6IjUxMy01NzAtMTg5MyIsImNlbGxQaG9uZSI6IjUxMy01NDktNDU2MSIsImVtYWlsIjoiZG9uZXR0ZS5mb2xsZXJAY294Lm5ldCIsIndlYnNpdGUiOiJodHRwOi8vd3d3LnByaW50aW5nZGltZW5zaW9ucy5jb20ifQ==\",\n"
			+ "            \"partitionKey\":\"TestKey034\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494051214339122325136326262786\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494053632190761554394675675138\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913019985,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJNaXRzdWUiLCJsYXN0bmFtZSI6IlRvbGxuZXIiLCJjb21wYW55IjoiTW9ybG9uZyBBc3NvY2lhdGVzIiwic3RyZWV0IjoiNyBFYWRzIFN0IiwiY2l0eSI6IkNoaWNhZ28iLCJjb3VudHkiOiJDb29rIiwic3RhdGUiOiJJTCIsInppcCI6IjYwNjMyIiwiaG9tZVBob25lIjoiNzczLTU3My02OTE0IiwiY2VsbFBob25lIjoiNzczLTkyNC04NTY1IiwiZW1haWwiOiJtaXRzdWVfdG9sbG5lckB5YWhvby5jb20iLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy5tb3Jsb25nYXNzb2NpYXRlcy5jb20ifQ==\",\n"
			+ "            \"partitionKey\":\"TestKey036\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494053632190761554394675675138\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494056050042400783653025087490\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020015,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJTYWdlIiwibGFzdG5hbWUiOiJXaWVzZXIiLCJjb21wYW55IjoiVHJ1aGxhciBBbmQgVHJ1aGxhciBBdHR5cyIsInN0cmVldCI6IjUgQm9zdG9uIEF2ZSAjODgiLCJjaXR5IjoiU2lvdXggRmFsbHMiLCJjb3VudHkiOiJNaW5uZWhhaGEiLCJzdGF0ZSI6IlNEIiwiemlwIjoiNTcxMDUiLCJob21lUGhvbmUiOiI2MDUtNDE0LTIxNDciLCJjZWxsUGhvbmUiOiI2MDUtNzk0LTQ4OTUiLCJlbWFpbCI6InNhZ2Vfd2llc2VyQGNveC5uZXQiLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy50cnVobGFyYW5kdHJ1aGxhcmF0dHlzLmNvbSJ9\",\n"
			+ "            \"partitionKey\":\"TestKey038\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494056050042400783653025087490\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494065721448957700686422736898\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020135,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJNZWFnaGFuIiwibGFzdG5hbWUiOiJHYXJ1ZmkiLCJjb21wYW55IjoiXCJCb2x0b24sIFdpbGJ1ciBFc3FcIiIsInN0cmVldCI6IjY5NzM0IEUgQ2FycmlsbG8gU3QiLCJjaXR5IjoiTWMgTWlubnZpbGxlIiwiY291bnR5IjoiV2FycmVuIiwic3RhdGUiOiJUTiIsInppcCI6IjM3MTEwIiwiaG9tZVBob25lIjoiOTMxLTMxMy05NjM1IiwiY2VsbFBob25lIjoiOTMxLTIzNS03OTU5IiwiZW1haWwiOiJtZWFnaGFuQGhvdG1haWwuY29tIiwid2Vic2l0ZSI6Imh0dHA6Ly93d3cuYm9sdG9ud2lsYnVyZXNxLmNvbSJ9\",\n"
			+ "            \"partitionKey\":\"TestKey0316\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494065721448957700686422736898\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494069348226416544573946855426\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020166,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJGbGV0Y2hlciIsImxhc3RuYW1lIjoiRmxvc2kiLCJjb21wYW55IjoiUG9zdCBCb3ggU2VydmljZXMgUGx1cyIsInN0cmVldCI6IjM5NCBNYW5jaGVzdGVyIEJsdmQiLCJjaXR5IjoiUm9ja2ZvcmQiLCJjb3VudHkiOiJXaW5uZWJhZ28iLCJzdGF0ZSI6IklMIiwiemlwIjoiNjExMDkiLCJob21lUGhvbmUiOiI4MTUtODI4LTIxNDciLCJjZWxsUGhvbmUiOiI4MTUtNDI2LTU2NTciLCJlbWFpbCI6ImZsZXRjaGVyLmZsb3NpQHlhaG9vLmNvbSIsIndlYnNpdGUiOiJodHRwOi8vd3d3LnBvc3Rib3hzZXJ2aWNlc3BsdXMuY29tIn0=\",\n"
			+ "            \"partitionKey\":\"TestKey0319\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494069348226416544573946855426\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494070557152236159203121561602\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020178,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJCZXR0ZSIsImxhc3RuYW1lIjoiTmlja2EiLCJjb21wYW55IjoiU3BvcnQgRW4gQXJ0Iiwic3RyZWV0IjoiNiBTIDMzcmQgU3QiLCJjaXR5IjoiQXN0b24iLCJjb3VudHkiOiJEZWxhd2FyZSIsInN0YXRlIjoiUEEiLCJ6aXAiOiIxOTAxNCIsImhvbWVQaG9uZSI6IjYxMC01NDUtMzYxNSIsImNlbGxQaG9uZSI6IjYxMC00OTItNDY0MyIsImVtYWlsIjoiYmV0dGVfbmlja2FAY294Lm5ldCIsIndlYnNpdGUiOiJodHRwOi8vd3d3LnNwb3J0ZW5hcnQuY29tIn0=\",\n"
			+ "            \"partitionKey\":\"TestKey0320\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494070557152236159203121561602\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494072975003875388461470973954\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020199,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJXaWxsYXJkIiwibGFzdG5hbWUiOiJLb2xtZXR6IiwiY29tcGFueSI6IlwiSW5nYWxscywgRG9uYWxkIFIgRXNxXCIiLCJzdHJlZXQiOiI2MTggVyBZYWtpbWEgQXZlIiwiY2l0eSI6IklydmluZyIsImNvdW50eSI6IkRhbGxhcyIsInN0YXRlIjoiVFgiLCJ6aXAiOiI3NTA2MiIsImhvbWVQaG9uZSI6Ijk3Mi0zMDMtOTE5NyIsImNlbGxQaG9uZSI6Ijk3Mi04OTYtNDg4MiIsImVtYWlsIjoid2lsbGFyZEBob3RtYWlsLmNvbSIsIndlYnNpdGUiOiJodHRwOi8vd3d3LmluZ2FsbHNkb25hbGRyZXNxLmNvbSJ9\",\n"
			+ "            \"partitionKey\":\"TestKey0322\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494072975003875388461470973954\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494075392855514617719820386306\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020222,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJBbGlzaGEiLCJsYXN0bmFtZSI6IlNsdXNhcnNraSIsImNvbXBhbnkiOiJXdGx6IFBvd2VyIDEwNyBGbSIsInN0cmVldCI6IjMyNzMgU3RhdGUgU3QiLCJjaXR5IjoiTWlkZGxlc2V4IiwiY291bnR5IjoiTWlkZGxlc2V4Iiwic3RhdGUiOiJOSiIsInppcCI6Ijg4NDYiLCJob21lUGhvbmUiOiI3MzItNjU4LTMxNTQiLCJjZWxsUGhvbmUiOiI3MzItNjM1LTM0NTMiLCJlbWFpbCI6ImFsaXNoYUBzbHVzYXJza2kuY29tIiwid2Vic2l0ZSI6Imh0dHA6Ly93d3cud3RsenBvd2VyZm0uY29tIn0=\",\n"
			+ "            \"partitionKey\":\"TestKey0324\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494075392855514617719820386306\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "         \"eventSource\":\"aws:kinesis\",\n"
			+ "         \"kinesis\":{\n"
			+ "            \"sequenceNumber\":\"49641044100135216774060769494098362446087295742859280386\",\n"
			+ "            \"approximateArrivalTimestamp\":1684913020521,\n"
			+ "            \"data\":\"eyJmaXJzdG5hbWUiOiJMYXZlcmEiLCJsYXN0bmFtZSI6IlBlcmluIiwiY29tcGFueSI6IkFiYyBFbnRlcnByaXNlcyBJbmMiLCJzdHJlZXQiOiI2NzggM3JkIEF2ZSIsImNpdHkiOiJNaWFtaSIsImNvdW50eSI6Ik1pYW1pLURhZGUiLCJzdGF0ZSI6IkZMIiwiemlwIjoiMzMxOTYiLCJob21lUGhvbmUiOiIzMDUtNjA2LTcyOTEiLCJjZWxsUGhvbmUiOiIzMDUtOTk1LTIwNzgiLCJlbWFpbCI6ImxwZXJpbkBwZXJpbi5vcmciLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy5hYmNlbnRlcnByaXNlc2luYy5jb20ifQ==\",\n"
			+ "            \"partitionKey\":\"TestKey0343\",\n"
			+ "            \"encryptionType\":null,\n"
			+ "            \"kinesisSchemaVersion\":\"1.0\"\n"
			+ "         },\n"
			+ "         \"eventID\":\"shardId-000000000000:49641044100135216774060769494098362446087295742859280386\",\n"
			+ "         \"invokeIdentityArn\":\"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "         \"eventName\":\"aws:kinesis:record\",\n"
			+ "         \"eventVersion\":\"1.0\",\n"
			+ "         \"eventSourceARN\":\"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "         \"awsRegion\":\"us-west-2\"\n"
			+ "      }\n"
			+ "   ]\n"
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

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//SQSEvent event = gson.fromJson(sqsEventJson, SQSEvent.class);
		ObjectMapper om = new ObjectMapper();
		//SQSEvent event = gson.fromJson(sqsEventJson, SQSEvent.class);
		KinesisEvent event = null;
		try {
			event = om.readValue(kinesisEventJson, KinesisEvent.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(KinesisEvent.KinesisEventRecord msg : event.getRecords()){
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
			PutItemOutcome putOutcome = ddbUpdater.insertIntoDynamoDB(msg, gson, logger);
			assertNotNull(putOutcome);
		}
	    
	}
}
