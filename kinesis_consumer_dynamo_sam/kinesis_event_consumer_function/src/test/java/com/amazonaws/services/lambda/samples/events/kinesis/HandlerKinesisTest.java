package com.amazonaws.services.lambda.samples.events.kinesis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.StreamsEventResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

class HandlerKinesisTest {
	private static final String kinesisEventJson = "{\n"
			+ "    \"records\": [\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363599131927541332382326980658\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403523,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJNaXRzdWUiLCJsYXN0bmFtZSI6IlRvbGxuZXIiLCJjb21wYW55IjoiTW9ybG9uZyBBc3NvY2lhdGVzIiwic3RyZWV0IjoiNyBFYWRzIFN0IiwiY2l0eSI6IkNoaWNhZ28iLCJjb3VudHkiOiJDb29rIiwic3RhdGUiOiJJTCIsInppcCI6IjYwNjMyIiwiaG9tZVBob25lIjoiNzczLTU3My02OTE0IiwiY2VsbFBob25lIjoiNzczLTkyNC04NTY1IiwiZW1haWwiOiJtaXRzdWVfdG9sbG5lckB5YWhvby5jb20iLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy5tb3Jsb25nYXNzb2NpYXRlcy5jb20ifQ==\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-50-6\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363599131927541332382326980658\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363608803334098249415724630066\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403670,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJDYW1teSIsImxhc3RuYW1lIjoiQWxiYXJlcyIsImNvbXBhbnkiOiJcIlJvdXNzZWF1eCwgTWljaGFlbCBFc3FcIiIsInN0cmVldCI6IjU2IEUgTW9yZWhlYWQgU3QiLCJjaXR5IjoiTGFyZWRvIiwiY291bnR5IjoiV2ViYiIsInN0YXRlIjoiVFgiLCJ6aXAiOiI3ODA0NSIsImhvbWVQaG9uZSI6Ijk1Ni01MzctNjE5NSIsImNlbGxQaG9uZSI6Ijk1Ni04NDEtNzIxNiIsImVtYWlsIjoiY2FsYmFyZXNAZ21haWwuY29tIiwid2Vic2l0ZSI6Imh0dHA6Ly93d3cucm91c3NlYXV4bWljaGFlbGVzcS5jb20ifQ==\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-66-14\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363608803334098249415724630066\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363611221185737478674074042418\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403717,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJNZWFnaGFuIiwibGFzdG5hbWUiOiJHYXJ1ZmkiLCJjb21wYW55IjoiXCJCb2x0b24sIFdpbGJ1ciBFc3FcIiIsInN0cmVldCI6IjY5NzM0IEUgQ2FycmlsbG8gU3QiLCJjaXR5IjoiTWMgTWlubnZpbGxlIiwiY291bnR5IjoiV2FycmVuIiwic3RhdGUiOiJUTiIsInppcCI6IjM3MTEwIiwiaG9tZVBob25lIjoiOTMxLTMxMy05NjM1IiwiY2VsbFBob25lIjoiOTMxLTIzNS03OTU5IiwiZW1haWwiOiJtZWFnaGFuQGhvdG1haWwuY29tIiwid2Vic2l0ZSI6Imh0dHA6Ly93d3cuYm9sdG9ud2lsYnVyZXNxLmNvbSJ9\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-69-16\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363611221185737478674074042418\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363613639037376707932423454770\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403757,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJZdWtpIiwibGFzdG5hbWUiOiJXaG9icmV5IiwiY29tcGFueSI6IkZhcm1lcnMgSW5zdXJhbmNlIEdyb3VwIiwic3RyZWV0IjoiMSBTdGF0ZSBSb3V0ZSAyNyIsImNpdHkiOiJUYXlsb3IiLCJjb3VudHkiOiJXYXluZSIsInN0YXRlIjoiTUkiLCJ6aXAiOiI0ODE4MCIsImhvbWVQaG9uZSI6IjMxMy0yODgtNzkzNyIsImNlbGxQaG9uZSI6IjMxMy0zNDEtNDQ3MCIsImVtYWlsIjoieXVraV93aG9icmV5QGFvbC5jb20iLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy5mYXJtZXJzaW5zdXJhbmNlZ3JvdXAuY29tIn0=\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-74-18\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363613639037376707932423454770\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363616056889015937190772867122\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403799,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJCZXR0ZSIsImxhc3RuYW1lIjoiTmlja2EiLCJjb21wYW55IjoiU3BvcnQgRW4gQXJ0Iiwic3RyZWV0IjoiNiBTIDMzcmQgU3QiLCJjaXR5IjoiQXN0b24iLCJjb3VudHkiOiJEZWxhd2FyZSIsInN0YXRlIjoiUEEiLCJ6aXAiOiIxOTAxNCIsImhvbWVQaG9uZSI6IjYxMC01NDUtMzYxNSIsImNlbGxQaG9uZSI6IjYxMC00OTItNDY0MyIsImVtYWlsIjoiYmV0dGVfbmlja2FAY294Lm5ldCIsIndlYnNpdGUiOiJodHRwOi8vd3d3LnNwb3J0ZW5hcnQuY29tIn0=\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-78-20\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363616056889015937190772867122\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363617265814835551819947573298\",\n"
			+ "                \"approximateArrivalTimestamp\": 9999999999999,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJWZXJvbmlrYSIsImxhc3RuYW1lIjoiSW5vdXllIiwiY29tcGFueSI6IkMgNCBOZXR3b3JrIEluYyIsInN0cmVldCI6IjYgR3JlZW5sZWFmIEF2ZSIsImNpdHkiOiJTYW4gSm9zZSIsImNvdW50eSI6IlNhbnRhIENsYXJhIiwic3RhdGUiOiJDQSIsInppcCI6Ijk1MTExIiwiaG9tZVBob25lIjoiNDA4LTU0MC0xNzg1IiwiY2VsbFBob25lIjoiNDA4LTgxMy00NTkyIiwiZW1haWwiOiJ2aW5vdXllQGFvbC5jb20iLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy5jbmV0d29ya2luYy5jb20ifQ==\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-80-21\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363617265814835551819947573298\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363618474740655166449122279474\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403839,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJXaWxsYXJkIiwibGFzdG5hbWUiOiJLb2xtZXR6IiwiY29tcGFueSI6IlwiSW5nYWxscywgRG9uYWxkIFIgRXNxXCIiLCJzdHJlZXQiOiI2MTggVyBZYWtpbWEgQXZlIiwiY2l0eSI6IklydmluZyIsImNvdW50eSI6IkRhbGxhcyIsInN0YXRlIjoiVFgiLCJ6aXAiOiI3NTA2MiIsImhvbWVQaG9uZSI6Ijk3Mi0zMDMtOTE5NyIsImNlbGxQaG9uZSI6Ijk3Mi04OTYtNDg4MiIsImVtYWlsIjoid2lsbGFyZEBob3RtYWlsLmNvbSIsIndlYnNpdGUiOiJodHRwOi8vd3d3LmluZ2FsbHNkb25hbGRyZXNxLmNvbSJ9\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-82-22\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363618474740655166449122279474\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363619683666474781078296985650\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403856,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJNYXJ5YW5uIiwibGFzdG5hbWUiOiJSb3lzdGVyIiwiY29tcGFueSI6IlwiRnJhbmtsaW4sIFBldGVyIEwgRXNxXCIiLCJzdHJlZXQiOiI3NCBTIFdlc3RnYXRlIFN0IiwiY2l0eSI6IkFsYmFueSIsImNvdW50eSI6IkFsYmFueSIsInN0YXRlIjoiTlkiLCJ6aXAiOiIxMjIwNCIsImhvbWVQaG9uZSI6IjUxOC05NjYtNzk4NyIsImNlbGxQaG9uZSI6IjUxOC00NDgtODk4MiIsImVtYWlsIjoibXJveXN0ZXJAcm95c3Rlci5jb20iLCJ3ZWJzaXRlIjoiaHR0cDovL3d3dy5mcmFua2xpbnBldGVybGVzcS5jb20ifQ==\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-84-23\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363619683666474781078296985650\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363620892592294395707471691826\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403874,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJBbGlzaGEiLCJsYXN0bmFtZSI6IlNsdXNhcnNraSIsImNvbXBhbnkiOiJXdGx6IFBvd2VyIDEwNyBGbSIsInN0cmVldCI6IjMyNzMgU3RhdGUgU3QiLCJjaXR5IjoiTWlkZGxlc2V4IiwiY291bnR5IjoiTWlkZGxlc2V4Iiwic3RhdGUiOiJOSiIsInppcCI6Ijg4NDYiLCJob21lUGhvbmUiOiI3MzItNjU4LTMxNTQiLCJjZWxsUGhvbmUiOiI3MzItNjM1LTM0NTMiLCJlbWFpbCI6ImFsaXNoYUBzbHVzYXJza2kuY29tIiwid2Vic2l0ZSI6Imh0dHA6Ly93d3cud3RsenBvd2VyZm0uY29tIn0=\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-86-24\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363620892592294395707471691826\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"eventSource\": \"aws:kinesis\",\n"
			+ "            \"kinesis\": {\n"
			+ "                \"sequenceNumber\": \"49641044100202119009656361363624519369753239594995810354\",\n"
			+ "                \"approximateArrivalTimestamp\": 1684918403906,\n"
			+ "                \"data\": \"eyJmaXJzdG5hbWUiOiJFemVraWVsIiwibGFzdG5hbWUiOiJDaHVpIiwiY29tcGFueSI6IlwiU2lkZXIsIERvbmFsZCBDIEVzcVwiIiwic3RyZWV0IjoiMiBDZWRhciBBdmUgIzg0IiwiY2l0eSI6IkVhc3RvbiIsImNvdW50eSI6IlRhbGJvdCIsInN0YXRlIjoiTUQiLCJ6aXAiOiIyMTYwMSIsImhvbWVQaG9uZSI6IjQxMC02NjktMTY0MiIsImNlbGxQaG9uZSI6IjQxMC0yMzUtODczOCIsImVtYWlsIjoiZXpla2llbEBjaHVpLmNvbSIsIndlYnNpdGUiOiJodHRwOi8vd3d3LnNpZGVyZG9uYWxkY2VzcS5jb20ifQ==\",\n"
			+ "                \"partitionKey\": \"TestKey04-05-24-2023-08-05-90-27\",\n"
			+ "                \"encryptionType\": null,\n"
			+ "                \"kinesisSchemaVersion\": \"1.0\"\n"
			+ "            },\n"
			+ "            \"eventID\": \"shardId-000000000003:49641044100202119009656361363624519369753239594995810354\",\n"
			+ "            \"invokeIdentityArn\": \"arn:aws:iam::664251831272:role/kinesis-lambda-dynamo-jav-LambdaKinesisConsumerJav-6U8GGPJN56FR\",\n"
			+ "            \"eventName\": \"aws:kinesis:record\",\n"
			+ "            \"eventVersion\": \"1.0\",\n"
			+ "            \"eventSourceARN\": \"arn:aws:kinesis:us-west-2:664251831272:stream/KinesisLambdaDynamoDBSAMStream\",\n"
			+ "            \"awsRegion\": \"us-west-2\"\n"
			+ "        }\n"
			+ "    ]\n"
			+ "}";

	@Mock
	DynamoDBUpdater ddbUpdater;	
	
	@Test
	@ExtendWith(MockitoExtension.class)
	void invokeTest() {
		ObjectMapper om = new ObjectMapper();
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
		Context context = new TestContext();
		PutItemOutcome putItemOutcome = mock(PutItemOutcome.class);
		DynamoDBUpdater dbUpdater = mock(DynamoDBUpdater.class);
		HandlerKinesis handler = new HandlerKinesis();
		handler.ddbUpdater = dbUpdater;
		when(handler.ddbUpdater.insertIntoDynamoDB(ArgumentMatchers.any(KinesisEvent.KinesisEventRecord.class), ArgumentMatchers.any(Gson.class), ArgumentMatchers.any(LambdaLogger.class))).thenReturn(putItemOutcome);
		StreamsEventResponse result = handler.handleRequest(event, context);
		assertEquals(result.getBatchItemFailures().size(), 1);
		assertEquals(result.getBatchItemFailures().get(0).getItemIdentifier(), "49641044100202119009656361363617265814835551819947573298");
	}

}
