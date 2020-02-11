package com.waes.diff.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SpringApplication.class,
	webEnvironment = WebEnvironment.RANDOM_PORT)
public class DiffControllerIntegrationTest
{
	private static final String DIFF_URL_TEST = "http://localhost:%s/v1/diff/%s/";
	private static final String DIFF_ID = "1";
	private static final String DIFF_NOT_FOUND = "2";
	private static final String DIFF_NOT_COMPLETE = "1234";

	private static final String DIFF_LEFT_VALUE = "eyAibmFtZSI6ImpvaG4iLCAiYWdlIjozMCwgImNhciI6ImNoZXZyb2xldCJ9"; //{ "name":"John", "age":30, "car":"chevrolet" }
	private static final String DIFF_RIGHT_VALUE = "eyAibmFtZSI6IkpIT04iLCAiYWdlIjozMCwgIkNBUiI6ImNoZXZyb2xFVCJ9";
	private static final String DIFF_SIZE_RIGHT_VALUE = "eyAibmFtZSI6IkpvaG4iLCAiYWdlIjozMCwgImNhciI6Im1hemRhIiB9"; //{ "name":"John", "age":30, "car":"mazda" }
	private static final String EXPECTED_RESULT = "{\"result\":[{\"difference\":\"'john' and 'JHON'\"},{\"difference\":\"'car' and 'CAR'\"},{\"difference\":\"'et' and 'ET'\"}]}";
	private static final String EQUAL_EXPECTED_RESULT = "{\"result\":\"The messages provided are equal\"}";
	private static final String NOT_EQUAL_EXPECTED_RESULT = "{\"result\":\"The messages provided does not have equal size\"}";

	private static final String DIFF_INVALID_LEFT_VALUE = "abc";
	private static final String DIFF_NO_JSON_VALUE = "dGhpcyBpcyBteSB0ZXN0";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testDiffProcessComplete()
	{
		ResponseEntity response = restTemplate.postForEntity(buildDiffLeftURL(), DIFF_LEFT_VALUE, Object.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.postForEntity(buildRightURL(), DIFF_RIGHT_VALUE, Object.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.getForEntity(buildURL(DIFF_ID), JsonNode.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().toString(), EXPECTED_RESULT);
	}

	@Test
	public void testDiffProcessCompleteEqualValues()
	{
		ResponseEntity response = restTemplate.postForEntity(buildDiffLeftURL(), DIFF_LEFT_VALUE, Object.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.postForEntity(buildRightURL(), DIFF_LEFT_VALUE, Object.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.getForEntity(buildURL(DIFF_ID), JsonNode.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().toString(), EQUAL_EXPECTED_RESULT);
	}

	@Test
	public void testDiffProcessCompleteDifferentSizeValues()
	{
		ResponseEntity response = restTemplate.postForEntity(buildDiffLeftURL(), DIFF_LEFT_VALUE, Object.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.postForEntity(buildRightURL(), DIFF_SIZE_RIGHT_VALUE, Object.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.getForEntity(buildURL(DIFF_ID), JsonNode.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().toString(), NOT_EQUAL_EXPECTED_RESULT);
	}

	@Test
	public void testDiffLeftInvalidValue()
	{
		ResponseEntity response = restTemplate.postForEntity(buildDiffLeftURL(), DIFF_INVALID_LEFT_VALUE, String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testDiffLeftNoJsonValue()
	{
		ResponseEntity response = restTemplate.postForEntity(buildDiffLeftURL(), DIFF_NO_JSON_VALUE, String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testDiffNotFound()
	{
		ResponseEntity response = restTemplate.getForEntity(buildNotFoundURL(), String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDiffNotCompleteData()
	{
		ResponseEntity response = restTemplate.postForEntity(buildNotCompleteURL() + "left", DIFF_LEFT_VALUE, String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);

		response = restTemplate.getForEntity(buildNotCompleteURL(), String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	private String buildNotCompleteURL()
	{
		return buildURL(DIFF_NOT_COMPLETE);
	}

	private String buildNotFoundURL()
	{
		return buildURL(DIFF_NOT_FOUND);
	}

	private String buildURL(String diffId) {
		return String.format(DIFF_URL_TEST, port, diffId);
	}

	private String buildDiffLeftURL()
	{
		return buildURL(DIFF_ID) + "left";
	}

	private String buildRightURL()
	{
		return buildURL(DIFF_ID) + "right";
	}

}
