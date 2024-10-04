package testCases;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import util.ConfigReader;
import io.restassured.*;
import static io.restassured.RestAssured.*;

public class GetAllAccounts extends GenerateBearerToken {
	String baseUri;
	String getAllEndPoint;
	String headerContentType;
	String firstAccountId;
	public static Logger logger = LogManager.getLogger(GetAllAccounts.class);
	
	/*
	 * https://qa.codefios.com/api/account/getAll
	 *  headers:
	 * Content-Type:application/json 
	 *  httpMethod: GET
	 *  given(): all input details ->(baseURI,Headers,Authorization,Payload/Body,QueryParameters) 
	 *  when(): submit api requests-> HttpMethod(Endpoint/Resource) 
	 *  then(): validate response -> (status code, Headers, responseTime, Payload/Body)
	 */

	public GetAllAccounts() {
			baseUri = extractProperty("baseURL");
			getAllEndPoint = extractProperty("getAllAccountsEndPoint");
			headerContentType = extractProperty("headerContentType");
	}

	

	@Test
	public void getAllAccounts() {

		Response response = 
			given()
				.baseUri(baseUri)
				.header("Content-Type", headerContentType)
				.auth().preemptive().basic("demo1@codefios.com", "abc123")
				.log().all().
			when()
				.get(getAllEndPoint).
			then()
				.log().all()
				.extract()
				.response();

		int responseCode = response.getStatusCode();
		System.out.println("Response code:" + responseCode);
		Assert.assertEquals(responseCode, 200, "Status codes are NOT matching!");

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Response header content type:" + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Content types are NOT matching!");

		
		String responseBody = response.getBody().asString(); // Get the body as a string.
		System.out.println("Response Body:" + responseBody);

		JsonPath jp = new JsonPath(responseBody);
		firstAccountId = jp.getString("records[0].account_id");
		System.out.println("FirstAccountId:" + firstAccountId);
		logger.info("****First Account Id****");

	}

}