package com.amazonaws.services.lambda.samples.events.activemq;

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
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mockito.ArgumentMatchers;

class DynamoDBUpdaterTest {
	
//	private static final String sqsEventJson = "{\n"
//			+ "    \"records\": [\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"8c9bf0ae-6fc4-484c-ba48-3081b296ec53\",\n"
//			+ "            \"receiptHandle\": \"AQEB0nQSbIimUUNcl1u9mzWCPd9s0tP3ZJWmyNrQdBaLGbjuMFNdt7tRIj+wVGFI3BPVl6u4WUzuarwQOpFUFeatAEkiPfqtHgN38ZEIotZCYOb1wCsQGch0ojF0DOcZjSsI6dwNLzIUZIPQVola/jX4NbD5LV1+WY6yhXAhXQqEvlrcjNMZ2JUUgMUTUwPqXVAUFYvL8+o5Fs58QT+VXJ11kuMYEvxFS38CYsVSXpcqIe9eepoTM71qDnzmb1cuQ++bGq0nJ938lQJz4YMz2JzLDCEQdWV5P4H0OzxOX4SsqSOpS4wmLqFSOcVcJRikC9y67jOa5jMKETL4rX1xlurLXeLF3p/8d14S7pVj/gOeVKJwG6sNZvL6zDa6XJWun6luiq62IS54MJkXsCfoBLVp3Q+d7Yu5NnIEH5AVpQMwMMA=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Sabra\\\",\\\"lastname\\\":\\\"Uyetake\\\",\\\"company\\\":\\\"Lowy Limousine Service\\\",\\\"street\\\":\\\"98839 Hawthorne Blvd #6101\\\",\\\"city\\\":\\\"Columbia\\\",\\\"county\\\":\\\"Richland\\\",\\\"state\\\":\\\"SC\\\",\\\"zip\\\":\\\"29201\\\",\\\"homePhone\\\":\\\"803-925-5213\\\",\\\"cellPhone\\\":\\\"803-681-3678\\\",\\\"email\\\":\\\"sabra@uyetake.org\\\",\\\"website\\\":\\\"http://www.lowylimousineservice.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"5a007b7dd73f11012dbb26e20dfa4738\",\n"
//			+ "            \"md5OfMessageAttributes\": \"442c98063e406723476691140558c7f5\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655113715\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655113716\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-70\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"52\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"44c836c0-c8c5-4804-8398-ff3e99ff6d3f\",\n"
//			+ "            \"receiptHandle\": \"AQEBKTijBYn0YukXB1W3u2eIww7kGKw5E/iYjkoyGgM0O8AnZPqXNpin0am620fK6Y1vHVBqgPt/uUpaKOocEOXCyiHqZAu2ICjmbJCzuUsPq9Qk1+pq0GrlEOneQtk7/SSO7GuuGhU47P0j2/XkpxK17+YsdhTQh7xU1haG2zcFaGPhmTJLb5osA9EsIo+2VoEvpEVln20RUlKzVvwujB/bwZxiFDc8dnzth4ruMIqiKRS5BPvHdh2Zj6D0oX6Q6tyAT+azs1s2+G3QTDc2mGrYA3d/3cQM6cH6Mj1VCI3ZDzlS1e+x7eqxAZucscoO+Jcp+PnSEbjBMa1QWtoJx5uoOENUOD6NDxtmnvpnk4O8/9z34SLf6jaEsIY1JAS7QZTU//LK4WHE2mfODZXa20KnZwqPv+AOse1HUCe19uTEJKc=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Amber\\\",\\\"lastname\\\":\\\"Monarrez\\\",\\\"company\\\":\\\"Branford Wire \\\\u0026 Mfg Co\\\",\\\"street\\\":\\\"14288 Foster Ave #4121\\\",\\\"city\\\":\\\"Jenkintown\\\",\\\"county\\\":\\\"Montgomery\\\",\\\"state\\\":\\\"PA\\\",\\\"zip\\\":\\\"19046\\\",\\\"homePhone\\\":\\\"215-934-8655\\\",\\\"cellPhone\\\":\\\"215-329-6386\\\",\\\"email\\\":\\\"amber_monarrez@monarrez.org\\\",\\\"website\\\":\\\"http://www.branfordwiremfgco.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"889aa6d4fc080acfea7918803c64ffc5\",\n"
//			+ "            \"md5OfMessageAttributes\": \"f2a636829c528975dfefa27b7a27eaf4\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655113841\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114329\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-83\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"56\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"7f2f3f01-d02d-4151-af41-8fe62cd5e34f\",\n"
//			+ "            \"receiptHandle\": \"AQEBfj0lBzqP9zSXTL+pwyPNe1OiUUwDNfzk+IIsd6/f7s6nOH+oc4kLLq+i1rglhcEQNPeiZlTcOeiX7W3e/hkJctk4ACuLHEfeGohrEjKA7RPxwZn2UWBEZhSVyRNI/qatPJRaRFEE0cJuABDyJQxqbnw39jYwcFwoq+JS1CKn4HrW+fjj3nDIsUh9dGQtY897/wBPsCzYAawlsBb/A2D2Za5+YTYDUvhuvXb/RoW8MvScRKYDpO/x6Sq3wDSPkN5BurR8dcY15c6D9ApCmUWi7UinbSkVBQCrsFoUxoSTGfFdx6+z2jRuBFsyrAW7uFQZVhfHGiUk7tY9AuTwt/8+dI9TdSSbmgDHdw6to+PU7bn8CXqHbBf/8ZeTYX9t9ilnQxgrK6aTHXg0P+fBsXtVvUbeSwUbbb3xix4G3mmC/yA=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Delmy\\\",\\\"lastname\\\":\\\"Ahle\\\",\\\"company\\\":\\\"Wye Technologies Inc\\\",\\\"street\\\":\\\"65895 S 16th St\\\",\\\"city\\\":\\\"Providence\\\",\\\"county\\\":\\\"Providence\\\",\\\"state\\\":\\\"RI\\\",\\\"zip\\\":\\\"2909\\\",\\\"homePhone\\\":\\\"401-458-2547\\\",\\\"cellPhone\\\":\\\"401-559-8961\\\",\\\"email\\\":\\\"delmy.ahle@hotmail.com\\\",\\\"website\\\":\\\"http://www.wyetechnologiesinc.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"62695cdd13ba05fd5516cb4d8e1bb863\",\n"
//			+ "            \"md5OfMessageAttributes\": \"dd19fad47e1d8841df7437a425a5a3f0\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655113870\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114329\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-86\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"58\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"fb150592-2b9d-4453-a6c8-8eef353695df\",\n"
//			+ "            \"receiptHandle\": \"AQEBAI9rIEOpHyueJcdac/MxiGnIlGEFHrr81Qy+7L+4zo1LGRcva9wORLt9RiS7LM/FDStnUkHH2jdfSiiIK4N16tdSEPsTGrbWdRSBExi0sHghta8AZmf+lcnEerK6Ly9Z7rt/i4eSDMv8klACoVlsuUwrKIwarrdTyKY3gY0CWBDASG5Bjw+155raUEgbrlB6Fexcf5joeOrPS+oMnbmk6/UeEolflQ9vVX4GKmHWWi/hqLodYzUVoIEbwx3mckjJ+yp2tcskLpwLa/H4AjyFuOPIOzYhTPzuhjxcC58OCslTcdI2BgrTVJb18KUKpgFHiuXLfeyY/PNQW4ICb5vt8fCfHb47LsfUGkrJX5qPCzYsM+m30aMpQyNgA1+ltQz4YY/zHTjQztyu7nevaqK+tgFShjYlekkY/hBjjlmLBrU=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Jamal\\\",\\\"lastname\\\":\\\"Vanausdal\\\",\\\"company\\\":\\\"\\\\\\\"Hubbard, Bruce Esq\\\\\\\"\\\",\\\"street\\\":\\\"53075 Sw 152nd Ter #615\\\",\\\"city\\\":\\\"Monroe Township\\\",\\\"county\\\":\\\"Middlesex\\\",\\\"state\\\":\\\"NJ\\\",\\\"zip\\\":\\\"8831\\\",\\\"homePhone\\\":\\\"732-234-1546\\\",\\\"cellPhone\\\":\\\"732-904-2931\\\",\\\"email\\\":\\\"jamal@vanausdal.org\\\",\\\"website\\\":\\\"http://www.hubbardbruceesq.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"6b65adbd1e936e5df4b5ed88d6a24396\",\n"
//			+ "            \"md5OfMessageAttributes\": \"bb1ed0d95ef533afea74b10c9dfce1a0\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655113941\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114329\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-93\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"61\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"014621a9-20c4-45bb-b61e-a8073d385afb\",\n"
//			+ "            \"receiptHandle\": \"AQEBlxquoUbIzloEmZOTmOskuotc19EzG6xXXIjVy3MBNGoRj2a1efvQDQZePfLQSk8tBavEESxqiWdz3HbOXbLn8brfI7/9kZGBKojY/2ShUxoVvo/Gryi47FMWZaCUiz2kOZV5XARofxnKaPmLb+ARePlJdqVOa060Daqc5BWcSPKZE2rcNK4jckfjUv9gXYcb+kIO8fTVBCFdWo3Nh/CORJULm43I+UN16GZI//8aX0OIaLuWfY3wgJf+DNzmPaIYwAeWizN+f+lUwi8Qnmlwl8iX06wTC2W6et0hBGAjRfXDrG//Up1aAnoUjDOnejpaXASPyiuSRJFxKHJD+K0S1kfbr1Ox7s04sXuKPfERK0Tdk7Vpk9+qZbEEMlHPovoYHHsC/zvlc7+pTu38MJQ4uDorQFojQh3MxFgnZX8iXHg=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Kallie\\\",\\\"lastname\\\":\\\"Blackwood\\\",\\\"company\\\":\\\"Rowley Schlimgen Inc\\\",\\\"street\\\":\\\"701 S Harrison Rd\\\",\\\"city\\\":\\\"San Francisco\\\",\\\"county\\\":\\\"San Francisco\\\",\\\"state\\\":\\\"CA\\\",\\\"zip\\\":\\\"94104\\\",\\\"homePhone\\\":\\\"415-315-2761\\\",\\\"cellPhone\\\":\\\"415-604-7609\\\",\\\"email\\\":\\\"kallie.blackwood@gmail.com\\\",\\\"website\\\":\\\"http://www.rowleyschlimgeninc.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"9e9666aa46a296802f0246f9be393d21\",\n"
//			+ "            \"md5OfMessageAttributes\": \"e602ada40b47ea262990061ed9d4969b\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655114070\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114329\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-06\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"70\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"7efbc2f6-2d11-47fe-aeb6-cbe5913d0991\",\n"
//			+ "            \"receiptHandle\": \"AQEBovaoRQJHcsm9j9seUraWo/rXv1T2ONRP7ua/jLuSI4RYyiubZ5gx0hVO1dd3igClm6OSCDlRV8vBb9CxQJqkW5HXke8vi6G/lUiEgSmyX7M6KAPgg63A9LnV3cIDBWV3YvDhRuRnSwCJzlC7Nwtwc52+4CDEuQFrrATed1VXTk3qxNZjuKleTR8Qa1aRZJXjAx3DX5ClNHd6LgRGGNOIQHoBs96W6Bq7dehWElsU8iK7nwquX/WlOJgDG7N4sKj2kIhp4Ne1sRY+3oeRbKDwaIOVUWglEsos0YX6+tiKF80vN/0F6kOkoSsTnTFr7xT6YH2U/K0zUnDaY/GfbcRk21+o9uT+I0lKrSXP9BBrDZjRGQhUwxhZWpPD1L+AlnJvj0BD69XHORnT7Vf6Pey0Snrg54RU/sDl6X97FbJXnJw=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Penney\\\",\\\"lastname\\\":\\\"Weight\\\",\\\"company\\\":\\\"Hawaiian King Hotel\\\",\\\"street\\\":\\\"18 Fountain St\\\",\\\"city\\\":\\\"Anchorage\\\",\\\"county\\\":\\\"Anchorage\\\",\\\"state\\\":\\\"AK\\\",\\\"zip\\\":\\\"99515\\\",\\\"homePhone\\\":\\\"907-797-9628\\\",\\\"cellPhone\\\":\\\"907-873-2882\\\",\\\"email\\\":\\\"penney_weight@aol.com\\\",\\\"website\\\":\\\"http://www.hawaiiankinghotel.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"218cd02c131ae49ae60ed257a0662ebe\",\n"
//			+ "            \"md5OfMessageAttributes\": \"5fd564706d0858f7ebf63cbfb8d8847a\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655114016\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114561\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-01\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"66\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"32307ec5-25a6-416b-be25-2aa2248897a3\",\n"
//			+ "            \"receiptHandle\": \"AQEBqDKM9o3muc1AcGXXWGo6bJEN53GU7m6PvRx2fj3UNBYjUOB9yb25Hon6u/YkwNMQC0UrmP2mbXN/ZS6d4BBFQMC39Nq8A3z3ujXyt7/rhXyxVn6FBQV6vW55W+UVHgEueGSS57KrswPnxm80nvW9Fn6Yrtyp6u8KFNUzk3JOQXniFc8DlbsmifDe5e/JrSZQzIhckZwG1IcjPFgHfS+xWLJCqBEfl6Dcnd6R2yHHCdzRA3P3zJpMu7LVi5c0tQXDVEFYP61bQ2A4KTPHP7GEoytPmgefAqLdWoFkQAmspjFf3WCNEZuBe6mjBfZD08MAuFIDYhvcvHSkiJEaVy6qy7ECueCfhUrsmFuEbZ7RR7U/KZqqHl1JiCoJXxnENfW5lzHNYzUETci3kB2VQ8C8Ocr3/6h/F9wgyJvu6bw0DtI=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Bobbye\\\",\\\"lastname\\\":\\\"Rhym\\\",\\\"company\\\":\\\"\\\\\\\"Smits, Patricia Garity\\\\\\\"\\\",\\\"street\\\":\\\"30 W 80th St #1995\\\",\\\"city\\\":\\\"San Carlos\\\",\\\"county\\\":\\\"San Mateo\\\",\\\"state\\\":\\\"CA\\\",\\\"zip\\\":\\\"94070\\\",\\\"homePhone\\\":\\\"650-528-5783\\\",\\\"cellPhone\\\":\\\"650-811-9032\\\",\\\"email\\\":\\\"brhym@rhym.com\\\",\\\"website\\\":\\\"http://www.smitspatriciagarity.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"f815d803e69f0834568cf4c83b2f7257\",\n"
//			+ "            \"md5OfMessageAttributes\": \"b2542d112a58cfba8eae035fb0c0bc1e\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655114107\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114561\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-10\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"72\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"38b4cc34-8c86-4535-9782-bb6dc38a4b6d\",\n"
//			+ "            \"receiptHandle\": \"AQEBxGPLAUaIabMJeaCLk/JAq39PBc+9jgkwIgQhhLlyMO+GT9KjSZUj5CVpYsk2FwnnpryTPUKdcY7x39K4pIEEBJsrm+/VfXaqeCQd7GB/AgAOm92tfyggEPuykfsy5G+uMcQoEsCOtfM7PMV473IUNNSemGpp81Aeq/kwFsMaMpl0lqpT3PCUaRFjCVHd3pJuSg3WJozH4agpRkealz3q3am4G93hEUUkjvLFsbiewUwwj9UnN0LbWzkz8hTk41mLvUnOH5kYoPOyxFvm3kMBIEt5kSW/dYaOfplpAMmLK78yT6I+aOXdT27PIUm1PNXcPpoLTRNOsFYcNk3BTB5ZWFa7MyKysXcS9q7CEEDaJT8om+F4q5rDK4n4y0wPi9yFRKZzTwyJohnBeqOTLwNpluCL5tetClPfYgLIX2ylK2E=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Micaela\\\",\\\"lastname\\\":\\\"Rhymes\\\",\\\"company\\\":\\\"H Lee Leonard Attorney At Law\\\",\\\"street\\\":\\\"20932 Hedley St\\\",\\\"city\\\":\\\"Concord\\\",\\\"county\\\":\\\"Contra Costa\\\",\\\"state\\\":\\\"CA\\\",\\\"zip\\\":\\\"94520\\\",\\\"homePhone\\\":\\\"925-647-3298\\\",\\\"cellPhone\\\":\\\"925-522-7798\\\",\\\"email\\\":\\\"micaela_rhymes@gmail.com\\\",\\\"website\\\":\\\"http://www.hleeleonardattorneyatlaw.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"30eae95ea99412d645cbbaac0f4c7744\",\n"
//			+ "            \"md5OfMessageAttributes\": \"168ed148172bfca859bb8c10e117c5f0\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655114123\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114561\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-11\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"73\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"056f346d-3ab7-4cdd-84e5-bd8b67551dfd\",\n"
//			+ "            \"receiptHandle\": \"AQEBksHT/TeYqfS3fdwVbn5ySybgS82U8A7QoMb/bTLvGXDsrqcxRTzmrq6Vqon3Zc1rb4scmksQhk1rKjeD8Zw/0U2byE5Xb+S47feOa8MFKGJF2Dk62mT180C5MJ8KM6GbqlmvaR9R+IerIUs2FqGUCgaMM0ZY9ZGWEpKl26yP9RxDjNgWS+SHK/jZUmMiv2+Q/dqS45znceE5rmz1HWbhG2NALDv5OZ/F6Fc4dVRo5wNFHmupbOYXKOkS/TTpUa9HP1Vq5nHoDvhIjuPowFSuuaoUw69GCWFo/zXorXhHCGq32sg33nFOQVxVWjybjGvamykZz1rl5GIzSLR8GY+KjAsgKtvUGwliGHHKvxP84q4UcCusnDZeXB8C05rqQlQH7bID5VKJL+PHUvz7A1J7mLylmv40sbOJgrhJSHrUSeY=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Laurel\\\",\\\"lastname\\\":\\\"Reitler\\\",\\\"company\\\":\\\"Q A Service\\\",\\\"street\\\":\\\"6 Kains Ave\\\",\\\"city\\\":\\\"Baltimore\\\",\\\"county\\\":\\\"Baltimore City\\\",\\\"state\\\":\\\"MD\\\",\\\"zip\\\":\\\"21215\\\",\\\"homePhone\\\":\\\"410-520-4832\\\",\\\"cellPhone\\\":\\\"410-957-6903\\\",\\\"email\\\":\\\"laurel_reitler@reitler.com\\\",\\\"website\\\":\\\"http://www.qaservice.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"7a5fdb5be72ed7c54ef84b5a49f920e8\",\n"
//			+ "            \"md5OfMessageAttributes\": \"bcdbece124cc6fab1bb8e323560fcc39\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655114162\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114561\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-15\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"76\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        },\n"
//			+ "        {\n"
//			+ "            \"messageId\": \"d97d43d7-e65f-4d54-b36c-5cbfd45b500e\",\n"
//			+ "            \"receiptHandle\": \"AQEBel2Ai6Nu4Om5NscZsU5pkJpvKsOrteOFlnC6RCz9d07P1UVgxa74bpYaNH8bhhL2ULt2KMtHX6pKi7nasbNgDiiht9aeP/mKZ/pZdtHJgMJB8nfncb70DRClR0JpWA+RL37oDWPFuZh2pnDfgnQWuGq4xFcBI98uUkFSXEK582ZyR7WdhsEGqlPf9xjeiIENcbpBFwkMjXlVzwxjRVlzzOSb9Ma3dMwBfV+C4EnghgFPJwP2oVk3KMem7T39Z+wTQG79ykV8Cpuk15aMeO81TImpi6j5BhXiycklDj76Ut0Ew9Fd5852vGCWxdM9l9U83KfL1mwGKyarWvk+7F+XjBRQVmCGpgtRwCqLoYYsAd7Cb91Ix2zDw4xOaj+6taKqbyErhW/a7XPBMsI2DHnkMWS64MFeYH0LFN89lYJaero=\",\n"
//			+ "            \"body\": \"{\\\"firstname\\\":\\\"Viva\\\",\\\"lastname\\\":\\\"Toelkes\\\",\\\"company\\\":\\\"Mark Iv Press Ltd\\\",\\\"street\\\":\\\"4284 Dorigo Ln\\\",\\\"city\\\":\\\"Chicago\\\",\\\"county\\\":\\\"Cook\\\",\\\"state\\\":\\\"IL\\\",\\\"zip\\\":\\\"60647\\\",\\\"homePhone\\\":\\\"773-446-5569\\\",\\\"cellPhone\\\":\\\"773-352-3437\\\",\\\"email\\\":\\\"viva.toelkes@gmail.com\\\",\\\"website\\\":\\\"http://www.markivpressltd.com\\\"}\",\n"
//			+ "            \"md5OfBody\": \"729f641229b1d1b0782c53a9a0d7e1f0\",\n"
//			+ "            \"md5OfMessageAttributes\": \"a01ac0c456efda377fc629b884bc51ac\",\n"
//			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:SQSLambdaDynamoDBSAMQueue\",\n"
//			+ "            \"eventSource\": \"aws:sqs\",\n"
//			+ "            \"awsRegion\": \"us-west-2\",\n"
//			+ "            \"attributes\": {\n"
//			+ "                \"ApproximateReceiveCount\": \"1\",\n"
//			+ "                \"SentTimestamp\": \"1684655114202\",\n"
//			+ "                \"SenderId\": \"AROAZVKD6SPUBDQBNO3D6:i-006b46228e77ad83f\",\n"
//			+ "                \"ApproximateFirstReceiveTimestamp\": \"1684655114561\"\n"
//			+ "            },\n"
//			+ "            \"messageAttributes\": {\n"
//			+ "                \"MessageKey\": {\n"
//			+ "                    \"stringValue\": \"TestKey10-05-21-2023-07-05-19\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                },\n"
//			+ "                \"MessageNumber\": {\n"
//			+ "                    \"stringValue\": \"78\",\n"
//			+ "                    \"stringListValues\": [],\n"
//			+ "                    \"binaryListValues\": [],\n"
//			+ "                    \"dataType\": \"String\"\n"
//			+ "                }\n"
//			+ "            }\n"
//			+ "        }\n"
//			+ "    ]\n"
//			+ "}";
//
//	@Test
//	void testDynamoDBUpdater() {
//		DynamoDBUpdater ddbUpdater = new DynamoDBUpdater("DBTable");
//		assertNotNull(ddbUpdater);
//		assertEquals(ddbUpdater.dynamoDBTableName, "DBTable");
//		assertNotNull(ddbUpdater.client);
//		assertNotNull(ddbUpdater.dynamoDB);
//		assertNotNull(ddbUpdater.dynamoTable);
//	}
//
//	@Test
//	void testInsertIntoDynamoDB() {
//
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		//SQSEvent event = gson.fromJson(sqsEventJson, SQSEvent.class);
//		ObjectMapper om = new ObjectMapper();
//		//SQSEvent event = gson.fromJson(sqsEventJson, SQSEvent.class);
//		SQSEvent event = null;
//		try {
//			event = om.readValue(sqsEventJson, SQSEvent.class);
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for(SQSMessage msg : event.getRecords()){
//			Table dynamoDbTable = mock(Table.class);
//		    AmazonDynamoDB client = mock(AmazonDynamoDB.class);
//			DynamoDB dynamoDB = mock(DynamoDB.class);
//		    PutItemOutcome putoutcome = mock(PutItemOutcome.class);
//		    LambdaLogger logger = mock(LambdaLogger.class);
//		    DynamoDBUpdater ddbUpdater = new DynamoDBUpdater("DBTable");
//		    ddbUpdater.client = client;
//		    ddbUpdater.dynamoDB = dynamoDB;
//		    ddbUpdater.dynamoTable = dynamoDbTable;
//		    when(ddbUpdater.dynamoTable.putItem(ArgumentMatchers.any(Item.class))).thenReturn(putoutcome);
//			PutItemOutcome putOutcome = ddbUpdater.insertIntoDynamoDB(msg, gson, logger);
//			assertNotNull(putOutcome);
//		}
//	    
//	}
}
