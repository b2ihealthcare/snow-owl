/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.test.commons.rest

import com.b2international.commons.platform.PlatformUtil
import com.b2international.snowowl.core.ApplicationContext
import com.b2international.snowowl.core.exceptions.AlreadyExistsException
import com.b2international.snowowl.identity.IdentityProvider
import com.b2international.snowowl.identity.IdentityWriter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import com.google.common.base.Splitter
import io.restassured.RestAssured
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import java.lang.reflect.Type
import java.util.List
import java.util.Map
import java.util.concurrent.atomic.AtomicBoolean
import org.apache.commons.lang.text.StrSubstitutor
import org.hamcrest.CoreMatchers

import static io.restassured.RestAssured.*

import static extension com.b2international.snowowl.test.commons.json.JsonExtensions.*

/**
 * Useful extension methods when testing Snow Owl's RESTful API. High level REST related syntactic sugars and stuff like 
 * that should be put in here, other API related stuff should go in a separate extension class.
 * 
 * @since 1.0 
 */
class RestExtensions {

	// HTTP and REST API
	static AtomicBoolean INITIALIZE_ONCE = new AtomicBoolean(false) 
	public static final String CONTEXT = "snowowl"

	public static final int OK = 200
	public static final int NO_CONTENT = 204

	public static final int UNAUTHORIZED = 401
	public static final int FORBIDDEN = 403
	public static final int NOT_FOUND = 404

	public static final String LOCATION = "Location"

	// Auth
	public static final String DEFAULT_USER = "snowowl"
	public static final String DEFAULT_PASS = "snowowl"
	
	public static final String WRONG_PASS = "wrong"
	
	static final String USER = if (!System.getProperty("test.user").nullOrEmpty) {
		System.getProperty("test.user")
	} else {
		DEFAULT_USER
	}
		
	static final String PASS = if (!System.getProperty("test.password").nullOrEmpty) {
		System.getProperty("test.password")
	} else {
		DEFAULT_PASS
	}

	def static RequestSpecification givenUnauthenticatedRequest(String api) {
		if (INITIALIZE_ONCE.compareAndSet(false, true)) {
			// change Base URI if defined as sysarg
			val serverLocation = System.getProperty("test.server.location")
			if (!serverLocation.nullOrEmpty) {
				RestAssured.baseURI = serverLocation
			}
			
			// Enable logging on failed requests
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
			// add custom 
			val mapper = new ObjectMapper()
			mapper.registerModule(new GuavaModule)
			RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
				override create(Type arg0, String arg1) {
					return mapper
				}
			}))
			
			// add the user to the current identity provider
			try {
				(ApplicationContext.instance.getServiceChecked(IdentityProvider) as IdentityWriter).addUser(USER, PASS)
			} catch (AlreadyExistsException e) {
				// ignore existing user
			}
		}
		Preconditions.checkArgument(api.startsWith("/"), "Api param should start with a forward slash: '/'")
		return given().port(getPort()).basePath(CONTEXT + api)
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

	def static String asPath(List<? extends String> values) {
		("/" + values.join("/")).replaceAll("//", "/")
	}

	def static String location(Response it) {
		header(LOCATION) ?: ""
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
		return if(jettyPortProp !== null) Integer.valueOf(jettyPortProp) else 8080
	}
	
	def static expectStatus(Response it, int expectedStatus) {
		if (statusCode() != expectedStatus) {
			System.err.println("Web server may reject your request, check access log")
			System.err.println("Headers: " + headers())
			System.err.println("Content-Type: " + getContentType)
			System.err.println("Body: " + body().asString)
		}
		then.statusCode(expectedStatus)
	}
	
	def static String lastPathSegment(String path) {
		Splitter.on('/').split(path).last
	}
	
	def static String joinPath(String... segments) {
		Joiner.on('/').join(segments)
	}
	
	def static RequestSpecification withFile(RequestSpecification it, String file, Class<?> cp) {
		multiPart(PlatformUtil.toAbsolutePath(cp, file).toFile)
	}
	
	// Simple REST operations
	
	def static Response get(String api, String...segments) {
		givenAuthenticatedRequest(api).get(asPath(segments))
	}
	
	def static Response delete(String api, String...segments) {
		givenAuthenticatedRequest(api).delete(asPath(segments))
	}
	
	def static Response postJson(String api, Map<String, ?> json, String...segments) {
		givenAuthenticatedRequest(api).withJson(json).post(asPath(segments))
	}

	def static Response putJson(String api, Map<String, ?> json, String...segments) {
		givenAuthenticatedRequest(api).withJson(json).put(asPath(segments))
	}

}
