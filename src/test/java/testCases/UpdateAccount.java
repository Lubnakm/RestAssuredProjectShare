package testCases;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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

public class UpdateAccount extends GenerateBearerToken {
	String baseUri;
	String updateAccountEndPoint;
	String getSingleAccountEndPoint;
	String headerContentType;
	String firstAccount_id;
	Map<String, String> updateMap;
	public static Logger logger = LogManager.getLogger(UpdateAccount.class);

	/*
	 * https://qa.codefios.com/api/account/create headers:
	 * Content-Type:application/json Authorization: Bearer {{bearer_token}}
	 * account_id: {{firstAccount_id}} payload/body: createAccountFilePath
	 * httpMethod: Post given(): all input details
	 * ->(baseURI,Headers,Authorization,Payload/Body,QueryParameters) when(): submit
	 * api requests-> HttpMethod(Endpoint/Resource) then(): validate response ->
	 * (status code, Headers, responseTime, Payload/Body)
	 */

	public UpdateAccount() {
		baseUri = extractProperty("baseURL");
		updateAccountEndPoint = extractProperty("updateAccountInfoEndPoint");
		getSingleAccountEndPoint = extractProperty("getSingleAccountInfoEndPoint");
		headerContentType = extractProperty("headerContentType");
		updateMap = new HashMap<String, String>();
	}

	/*
	 * { "account_name": "LKPractice account 150", "description":
	 * "Test description 100", "balance": 4000.22, "account_number": 1388892468,
	 * "contact_person": "LKM" }
	 */
	
	public Map<String,String> getupdateMap(){
		updateMap.put("account_id","952");
		updateMap.put("account_name","LKPractice account 150");
		updateMap.put("description","Test description 200");
		updateMap.put("balance","2220.22");
		updateMap.put("account_number","1388892468");
		updateMap.put("contact_person","LKM");
		
		return updateMap;
		
	}
	@Test(priority = 1)
	public void updateAccount() {

		Response response = 
				given()
					.baseUri(baseUri)
					.header("Content-Type", headerContentType)
					.header("Authorization", "Bearer " + generateBearerToken())
					.body(getupdateMap())
					.log().all().
				when()
					.put(updateAccountEndPoint).
				then()
					.log().all()
					.extract().response();

		int responseCode = response.getStatusCode();
		System.out.println("Response code:" + responseCode);
		Assert.assertEquals(responseCode, 200, "Status codes are NOT matching!");

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Response header content type:" + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Content types are NOT matching!");

		String responseBody = response.getBody().asString(); // Get the body as a string.
		System.out.println("Response Body:" + responseBody);

		/*
		 * { "message": "Account created successfully." }
		 */

		JsonPath jp = new JsonPath(responseBody);

		String actualAccountudateMess = jp.getString("message");
		logger.info("****Actual account Name****" + actualAccountudateMess);
		Assert.assertEquals(actualAccountudateMess, "Account updated successfully.",
				"Account Updated Messages are not Matching!!");

	}

	@Test(priority = 2)
	public void getSingleAccountInfo() {

		Response response =
				given()
					.baseUri(baseUri)
					.header("Content-Type", headerContentType)
					.header("Authorization", "Bearer " + generateBearerToken())
					.queryParam("account_id", getupdateMap().get("account_id"))
					.log().all().
				when()
					.get(getSingleAccountEndPoint).
				then()
				.log().all()
				.extract().response();

		int responseCode = response.getStatusCode();
		System.out.println("Response code:" + responseCode);
		Assert.assertEquals(responseCode, 200, "Status codes are NOT matching!");

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Response header content type:" + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Content types are NOT matching!");

		String responseBody = response.getBody().asString(); // Get the body as a string.
		System.out.println("Response Body:" + responseBody);

		/*
		 * { "account_id":"921", "account_name":"MD Techfios account 111",
		 * "account_number":"123456789", "description":"Test description 1",
		 * "balance":"100.22", "contact_person":"MD Islam"}
		 */

		JsonPath jp = new JsonPath(responseBody);

		String actualAccountName = jp.getString("account_name");
		logger.info("****Actual account Name****" + actualAccountName);

		String actualAccountNumber = jp.getString("account_number");
		logger.info("****Actual account Number****" + actualAccountNumber);

		String actualDescription = jp.getString("description");
		logger.info("****Actual Description****" + actualDescription);

		String actualBalance = jp.getString("balance");
		logger.info("****Actual Balance****" + actualBalance);

		String actualcontact_person = jp.getString("contact_person");
		logger.info("****Actual Contact_Person****" + actualcontact_person);

	
		String expectedAccountName =getupdateMap().get("account_name");
		logger.info("****Actual account Name****" + actualAccountName);

		String expectedAccountNumber = getupdateMap().get("account_number");
		logger.info("****Actual account Number****" + actualAccountNumber);

		String expectedDescription = getupdateMap().get("description");
		logger.info("****Actual Description****" + actualDescription);

		String expectedBalance = getupdateMap().get("balance");
		logger.info("****Actual Balance****" + actualBalance);

		String expectedcontact_person = getupdateMap().get("contact_person");
		logger.info("****Actual Contact_Person****" + actualcontact_person);

		Assert.assertEquals(actualAccountName, expectedAccountName, "Account Names are not Matching!!");
		Assert.assertEquals(actualAccountNumber, expectedAccountNumber, "Account Numbers are not Matching!!");
		Assert.assertEquals(actualDescription, expectedDescription, "Descriptions are not Matching!!");
		Assert.assertEquals(actualBalance, expectedBalance, "Account Balance are not Matching!!");
		Assert.assertEquals(actualcontact_person, expectedcontact_person, "Contact Person name are not Matching!!");
	}

}