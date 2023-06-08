package activemq.producer;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManagerDecoder {
	
	public static void getSecret() {

		String secretName = "AmazonMQCredentials";
	    //Region region = Region.of("us-west-2");

		
		
	    // Create a Secrets Manager client
	    SecretsManagerClient client = SecretsManagerClient.builder()
	    		.credentialsProvider(DefaultCredentialsProvider.builder().build())
	            .build();

	    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
	            .secretId(secretName)
	            .build();

	    GetSecretValueResponse getSecretValueResponse;

	    try {
	        getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
	    } catch (Exception e) {
	        // For a list of exceptions thrown, see
	        // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
	        throw e;
	    }

	    String secret = getSecretValueResponse.secretString();
	    
	}
	
	public static void main(String[] args) {
		SecretsManagerDecoder.getSecret();
	}
}
