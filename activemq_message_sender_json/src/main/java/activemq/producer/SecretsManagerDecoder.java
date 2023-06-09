package activemq.producer;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;





public class SecretsManagerDecoder {
	
	public static String getSecret() {

		String secretName = "AmazonMQCredentials";
		
		DefaultAwsRegionProviderChain defaultAwsRegionProviderChain = new DefaultAwsRegionProviderChain();
		Region region = defaultAwsRegionProviderChain.getRegion();
		System.out.println("regionString = " + region.toString());
		
		SecretsManagerClient client = SecretsManagerClient.builder()
	            .region(region)
	            .build();

	    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
	            .secretId(secretName)
	            .build();

	    GetSecretValueResponse getSecretValueResponse = null;

	    try {
	        getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    if (null != getSecretValueResponse) {
	    	return getSecretValueResponse.secretString();
	    } else {
	    	return "Sorry mate! No secret found";
	    }
		

		
		
	}
	
	public static void main(String[] args) {
		System.out.println(SecretsManagerDecoder.getSecret());
	}
}
