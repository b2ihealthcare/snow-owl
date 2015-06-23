package com.b2international.snowowl.snomed.api.rest.versioning;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class SnomedVersioningApiTest extends AbstractSnomedApiTest {

	@Test
	public void getNonExistentVersion() {
		assertVersionGetStatus("nonexistent", 404);
	}
	
	@Test
	public void createVersionWithoutDescription() {
		assertVersionPostStatus("", "20150201", 400);
	}
	
	@Test
	public void createVersionWithNonLatestEffectiveDate() {
		assertVersionPostStatus("sct-v1", "20150101", 400);
	}
	
	@Test
	public void createVersion() {
		assertVersionPostStatus("sct-v2", "20150201", 201);
		assertVersionGetStatus("sct-v2", 200);
	}
	
	@Test
	public void createVersionWithSameNameAsBranch() {
		assertBranchCanBeCreated("MAIN", "sct-v3");
		assertVersionPostStatus("sct-v2", "20150202", 409);
	}

	private void assertVersionGetStatus(String version, int status) {
		givenAuthenticatedRequest(API)
		.when()
			.get("/codesystems/SNOMEDCT/versions/{id}", version)
		.then()
		.assertThat()
			.statusCode(status);
	}

	private void assertVersionPostStatus(String version, String effectiveDate, int status) {
		whenCreatingVersion(givenAuthenticatedRequest(API), version, effectiveDate)
		.then()
		.assertThat()
			.statusCode(status);
	}
	
	private Response whenCreatingVersion(RequestSpecification request, String version, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.of(
			"version", version,
			"description", version,
			"effectiveDate", effectiveDate
		);

		return request
		.and()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/codesystems/SNOMEDCT/versions");
	}

}