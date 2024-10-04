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

public class GetSingleAccountInfo extends GenerateBearerToken {
	String baseUri;
	String getSingleAccountEndPoint;
	String headerContentType;
	public static Logger logger = LogManager.getLogger(GetSingleAccountInfo.class);

	/*
	 * https://qa.codefios.com/api/account/getOne headers:
	 * Content-Type:application/json httpMethod: GET given(): all input details
	 * ->(baseURI,Headers,Authorization,Payload/Body,QueryParameters) when(): submit
	 * api requests-> HttpMethod(Endpoint/Resource) then(): validate response ->
	 * (status code, Headers, responseTime, Payload/Body)
	 */

	public GetSingleAccountInfo() {
		baseUri = extractProperty("baseURL");
		getSingleAccountEndPoint = extractProperty("getSingleAccountInfoEndPoint");
		headerContentType = extractProperty("headerContentType");
	}

	@Test
	public void getSingleAccountInfo() {

		Response response = 
				given()
					.baseUri(baseUri)
					.header("Content-Type", headerContentType)
					.header("Authorization", "Bearer " + generateBearerToken())
					.queryParam("account_id", "921").log().all()
				.when().get(getSingleAccountEndPoint).then().log().all().extract().response();

		int responseCode = response.getStatusCode();
		System.out.println("Response code:" + responseCode);
		Assert.assertEquals(responseCode, 200, "Status codes are NOT matching!");

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Response header content type:" + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Content types are NOT matching!");

		String responseBody = response.getBody().asString(); // Get the body as a string.
		System.out.println("Response Body:" + responseBody);

		/*
		 {
		 	"account_id":"921",
		 	"account_name":"MD Techfios account 111",
		 	"account_number":"123456789",
		 	"description":"Test description 1",
		 	"balance":"100.22",
		 	"contact_person":"MD Islam"}
		 */

		JsonPath jp = new JsonPath(responseBody);
		String actualAccountName = jp.getString("account_name");
		logger.info("****Actual account Name****"+actualAccountName );
		Assert.assertEquals(actualAccountName, "MD Techfios account 111","Account Names are not Matching!!");
		
		String actualAccountNumber = jp.getString("account_number");
		logger.info("****Actual account Number****"+actualAccountNumber );
		Assert.assertEquals(actualAccountNumber, "123456789","Account Numbers are not Matching!!");
		
		String actualDescription = jp.getString("description");
		logger.info("****Actual Description****"+actualDescription);
		Assert.assertEquals(actualDescription, "Test description 1","Descriptions are not Matching!!");
		
		String actualBalance = jp.getString("balance");
		logger.info("****Actual Balance****"+actualBalance);
		Assert.assertEquals(actualBalance, "100.22","Account Balance are not Matching!!");
		
		String actualcontact_person= jp.getString("contact_person");
		logger.info("****Actual Contact_Person****"+actualcontact_person);
		Assert.assertEquals(actualcontact_person, "MD Islam","Contact Person name are not Matching!!");
	}

}