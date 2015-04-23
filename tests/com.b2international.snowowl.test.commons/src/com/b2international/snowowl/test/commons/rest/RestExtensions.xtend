/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.test.commons.rest

import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import java.util.Collection
import java.util.Map
import org.apache.commons.lang.text.StrSubstitutor

import static com.jayway.restassured.RestAssured.*

import static extension com.b2international.snowowl.test.commons.json.JsonExtensions.*
import com.google.common.base.Preconditions
import org.hamcrest.CoreMatchers

/**
 * Useful extension methods when testing Snow Owl's RESTful API. High level REST related syntactic sugars and stuff like 
 * that should be put in here, other API related stuff should go in a separate extension class.
 * 
 * @since 1.0 
 */
class RestExtensions {

	// HTTP and REST API
	public static final String CONTEXT = "snowowl"

	public static final int OK = 200
	public static final int NO_CONTENT = 204

	public static final int UNAUTHORIZED = 401
	public static final int FORBIDDEN = 403
	public static final int NOT_FOUND = 404

	public static final String LOCATION = "Location"

	// Auth
	public static final String USER = "snowowl"
	public static final String PASS = "snowowl"
	public static final String WRONG_PASS = "wrong"

	def static RequestSpecification givenUnauthenticatedRequest(String api) {
		Preconditions.checkArgument(api.startsWith("/"), "Api param should start with a forward slash: '/'")
		given().port(getPort()).basePath(CONTEXT + api)
	}

	def static RequestSpecification givenAuthenticatedRequest(String api) {
		givenRequestWithPassword(api, PASS)
	}

	def static RequestSpecification givenInvalidPasswordRequest(String api) {
		givenRequestWithPassword(api, WRONG_PASS)
	}

	def private static RequestSpecification givenRequestWithPassword(String api, String password) {
		givenUnauthenticatedRequest(api).auth().basic(USER, password)
	}

	def static RequestSpecification withJson(RequestSpecification it, Map<String, ? extends Object> properties) {
		contentType(ContentType.JSON).body(properties.asJson)
	}

	def static String asPath(Collection<? extends Object> values) {
		"/" + values.join("/")
	}

	def static String location(Response it) {
		header(LOCATION)
	}

	def static String renderWithFields(String it, Object object) {
		it.render(object.fieldValueMap)
	}

	def static Map<String, Object> getFieldValueMap(Object object) {
		object.class.fields.toMap[name].mapValues[get(object)]
	}

	def static String render(String it, Map<String, Object> fieldValueMap) {
		new StrSubstitutor(fieldValueMap).replace(it)
	}

	/**
	 * Asserts whether a JSON body inside the given Response object have the same "state" field as the given state parameter.
	 */
	def static inState(Response it, String state) {
		then.body("state", CoreMatchers.equalTo(state))
	}

	/**
	 * Returns the port used in the test environment, this is equivalent with the jetty.port configuration parameter, or with 8080 if no jetty.port parameter found.
	 */
	def static int getPort() {
		val jettyPortProp = System.getProperty("jetty.port")
		return if(jettyPortProp != null) Integer.valueOf(jettyPortProp) else 8080
	}
	
	def static expectStatus(Response it, int expectedStatus) {
		if (statusCode() != expectedStatus) {
			System.err.println(body().asString)
		}
		then.statusCode(expectedStatus)
	}

}
