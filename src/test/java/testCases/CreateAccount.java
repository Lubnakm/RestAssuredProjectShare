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

public class CreateAccount extends GenerateBearerToken {
	String baseUri;
	String getSingleAccountEndPoint;
	String createAccountEndPoint;
	String getAllEndPoint;
	String headerContentType;
	String firstAccount_id;
	String createAccountFilePath;
	public static Logger logger = LogManager.getLogger(CreateAccount.class);

	/*
	 * https://qa.codefios.com/api/account/create headers:
	 * Content-Type:application/json Authorization: Bearer {{bearer_token}}
	 * account_id: {{firstAccount_id}} payload/body: createAccountFilePath
	 * httpMethod: Post given(): all input details
	 * ->(baseURI,Headers,Authorization,Payload/Body,QueryParameters) when(): submit
	 * api requests-> HttpMethod(Endpoint/Resource) then(): validate response ->
	 * (status code, Headers, responseTime, Payload/Body)
	 */

	public CreateAccount() {
		baseUri = extractProperty("baseURL");
		createAccountEndPoint = extractProperty("createAccountInfoEndPoint");
		getSingleAccountEndPoint = extractProperty("getSingleAccountInfoEndPoint");
		getAllEndPoint = extractProperty("getAllAccountsEndPoint");
		headerContentType = extractProperty("headerContentType");
		createAccountFilePath = "src\\main\\java\\data\\createAccountPayload.json";
	}

	@Test(priority = 1)
	public void createAccount() {

		Response response = given().baseUri(baseUri).header("Content-Type", headerContentType)
				.header("Authorization", "Bearer " + generateBearerToken()).body(new File(createAccountFilePath)).log()
				.all().when().post(createAccountEndPoint).then().log().all().extract().response();

		int responseCode = response.getStatusCode();
		System.out.println("Response code:" + responseCode);
		Assert.assertEquals(responseCode, 201, "Status codes are NOT matching!");

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Response header content type:" + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Content types are NOT matching!");

		String responseBody = response.getBody().asString(); // Get the body as a string.
		System.out.println("Response Body:" + responseBody);

		/*
		 * { 
		 * "message": "Account created successfully."
		 * }
		 */

		JsonPath jp = new JsonPath(responseBody);
		
		String actualAccountcreateMess = jp.getString("message");
		logger.info("****Actual account Name****" + actualAccountcreateMess);
		Assert.assertEquals(actualAccountcreateMess, "Account created successfully.", "Account Created Messages are not Matching!!");

		
	}
	@Test(priority =2)
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
		firstAccount_id = jp.getString("records[0].account_id");
		System.out.println("FirstAccountId:" + firstAccount_id);
		logger.info("****First Account Id****"+firstAccount_id);

	}
	@Test(priority=3)
	public void getSingleAccountInfo() {

		Response response = 
				given()
					.baseUri(baseUri)
					.header("Content-Type", headerContentType)
					.header("Authorization", "Bearer " + generateBearerToken())
					.queryParam("account_id", firstAccount_id).log().all()
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
		
		String actualAccountNumber = jp.getString("account_number");
		logger.info("****Actual account Number****"+actualAccountNumber );
		
		String actualDescription = jp.getString("description");
		logger.info("****Actual Description****"+actualDescription);
		
		String actualBalance = jp.getString("balance");
		logger.info("****Actual Balance****"+actualBalance);
		
		String actualcontact_person= jp.getString("contact_person");
		logger.info("****Actual Contact_Person****"+actualcontact_person);
		
		File expectedRequestBody = new File(createAccountFilePath);
		JsonPath jp2 = new JsonPath(expectedRequestBody);

		String expectedAccountName = jp.getString("account_name");
		logger.info("****Actual account Name****"+actualAccountName );
		
		String expectedAccountNumber = jp.getString("account_number");
		logger.info("****Actual account Number****"+actualAccountNumber );
		
		String expectedDescription = jp.getString("description");
		logger.info("****Actual Description****"+actualDescription);
		
		String expectedBalance = jp.getString("balance");
		logger.info("****Actual Balance****"+actualBalance);
		
		String expectedcontact_person= jp.getString("contact_person");
		logger.info("****Actual Contact_Person****"+actualcontact_person);
		
		
		Assert.assertEquals(actualAccountName, expectedAccountName,"Account Names are not Matching!!");
		Assert.assertEquals(actualAccountNumber, expectedAccountNumber,"Account Numbers are not Matching!!");
		Assert.assertEquals(actualDescription, expectedDescription,"Descriptions are not Matching!!");
		Assert.assertEquals(actualBalance, expectedBalance,"Account Balance are not Matching!!");
		Assert.assertEquals(actualcontact_person, expectedcontact_person,"Contact Person name are not Matching!!");
	}


}