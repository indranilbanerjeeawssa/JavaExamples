# apigateway_rest_nonproxy_event_consumer_function

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.

- apigateway_rest_nonproxy_event_consumer_function/src/main - Code for the application's Lambda function.
- events - Invocation events that you can use to invoke the function.
- apigateway_rest_nonproxy_event_consumer_function/src/test - Unit tests for the application code. 
- template.yaml - A template that defines the application's AWS resources.

The application uses several AWS resources, including Lambda functions and an API Gateway API. These resources are defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same deployment process that updates your application code.

## Use the SAM CLI to build and test locally

Build your application with the `sam build` command.

```bash
sam build
```

The SAM CLI installs dependencies defined in `apigateway_rest_nonproxy_event_consumer_function/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
sam local invoke --event events/event.json
```

## Deploy the sample application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To use the SAM CLI, you need the following tools.

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Docker - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modifies IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Disable rollback**: Keep the default of N
* **RESTNonProxyFunction may not have authorization defined, Is this okay?**: The default is N. Change this to y
* **Save arguments to configuration file**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.
* **SAM configuration file [samconfig.toml]**: By default the configuration is saved in a file called samconfig.toml in the same folder. Change the name of the file if you want to give it a different name.
* **SAM configuration environment [default]:**: By default the configuration is saved for a default environment. Change the name of the environment if you want to give it a different name.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

## Test the sample application

Once the lambda function is deployed, send some REST POST messages on the URL that the API Gateway fronting the Lambda function is listening on.

Use the project ../apigateway_rest_nonproxy_message_sender.

Look at the Readme of that project to determine how to build that project and run the command that will send REST POST requests with a Json payload to the lambda function built using this project. The lambda function will receive the REST POST requests with a JSON payload and input fields from the REST POST request into a DynamoDB table.

The value field of each REST POST request that will be sent out will be a Json element of the format

"person": {
        "firstname": "Myra",
        "lastname": "Munns",
        "company": "Anker Law Office",
        "street": "461 Prospect Pl #316",
        "city": "Euless",
        "county": "Tarrant",
        "state": "TX",
        "zip": "76040",
        "homePhone": "817-914-7518",
        "cellPhone": "817-451-3518",
        "email": "mmunns@cox.net",
        "website": "http://www.ankerlawoffice.com"
}

Then check Cloudwatch logs and you should see messages for the Cloudwatch Log Group with the name of the deployed Lambda function.

The lambda code parses the REST POST requests and outputs the fields in the REST POST requests to Cloudwatch logs

A single invocation of the lambda function receives a single request.

The code in this example prints out the fields in the REST POST request and logs them in Cloudwatch logs.

Apart from outputting to Cloudwatch logs, the lambda function also inputs fields from the REST POST request (both message metadata fields as well as payload fields from the JSON payload) into a DynamoDB table created by the SAM template. You can log into the AWS console and look at the DynamoDB table and run a scan to see data getting input from the REST POST request into the DynamoDB table
