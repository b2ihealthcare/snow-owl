/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.api.rest.codesystem

import com.jayway.restassured.response.Response
import java.util.Map

import static org.hamcrest.CoreMatchers.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @since 1.0
 */
Feature: Code system API

	Background:
		val request = givenAuthenticatedRequest("/admin")
		Response response;

	Scenario: Get all code systems

		When a GET request is made to "/codesystems"
			response = request.when.get(args.first)
		Then the response should have a "200" status
			response.then.assertThat.statusCode(args.first.toInt)
		And the response body for path "items.oid" should include "2.16.840.1.113883.6.96"
			response.then.assertThat.body(args.first, hasItem(args.second))

	Scenario: Look up code system by valid OID
		
		When a GET request is made to "/codesystems/2.16.840.1.113883.6.96"
		Then the response should have a "200" status
		And the response body for path "oid" should be equal to "2.16.840.1.113883.6.96"
			response.then.assertThat.body(args.first, equalTo(args.second))

	Scenario: Look up code system by non-existent OID

		When a GET request is made to "/codesystems/1.2.3.4.10000"
		Then the response should have a "404" status

	Scenario: Look up code system by valid short name
		
		When a GET request is made to "/codesystems/SNOMEDCT"
		Then the response should have a "200" status
		And the response body for path "shortName" should be equal to "SNOMEDCT"

//	Scenario: Create code system version
//		Map<String, ?> requestBody;
//			
//		Given a valid code system version body
//			requestBody = #{
//				"version" -> "v1",
//				"description" -> "v1",
//				"effectiveDate" -> "20141001"
//			}
//		When a POST request is made to "/codesystems/SNOMEDCT/versions"
//			response = request.withJson(requestBody).when.post(args.first)
//		Then the response should have a "201" status
