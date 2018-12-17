/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.rest;

import static io.restassured.RestAssured.given;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang.text.StrSubstitutor;
import org.hamcrest.CoreMatchers;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.IdentityWriter;
import com.b2international.snowowl.test.commons.json.JsonExtensions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Useful extension methods when testing Snow Owl's RESTful API. High level REST related syntactic sugars and stuff like 
 * that should be put in here, other API related stuff should go in a separate extension class.
 * 
 * @since 1.0 
 */
public class RestExtensions {

	// HTTP and REST API
	private static final AtomicBoolean INITIALIZE_ONCE = new AtomicBoolean(false); 
	public static final String CONTEXT = "snowowl";

	public static final int OK = 200;
	public static final int NO_CONTENT = 204;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;

	public static final String LOCATION = "Location";

	// Auth
	public static final String DEFAULT_USER = "snowowl";
	public static final String DEFAULT_PASS = "snowowl";
	public static final String WRONG_PASS = "wrong";
	
	static final String USER;
	static final String PASS; 
	
	static {
		if (!Strings.isNullOrEmpty(System.getProperty("test.user"))) {
			USER = System.getProperty("test.user");
		} else {
			USER = DEFAULT_USER;
		}
		
		if (!Strings.isNullOrEmpty(System.getProperty("test.password"))) {
			PASS = System.getProperty("test.password");
		} else {
			PASS = DEFAULT_PASS;
		}
	}
	

	public static RequestSpecification givenUnauthenticatedRequest(String api) {
		if (INITIALIZE_ONCE.compareAndSet(false, true)) {
			// change Base URI if defined as sysarg
			final String serverLocation = System.getProperty("test.server.location");
			if (!Strings.isNullOrEmpty(serverLocation)) {
				RestAssured.baseURI = serverLocation;
			}
			
			// Enable logging on failed requests
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
			
			// add the user to the current identity provider
			try {
				((IdentityWriter) ApplicationContext.getInstance().getServiceChecked(IdentityProvider.class)).addUser(USER, PASS);
			} catch (AlreadyExistsException e) {
				// ignore existing user
			}
		}
		Preconditions.checkArgument(api.startsWith("/"), "Api param should start with a forward slash: '/'");
		return given().port(getPort()).basePath(CONTEXT + api);
	}

	public static RequestSpecification givenAuthenticatedRequest(String api) {
		return givenRequestWithPassword(api, PASS);
	}
	
	public static RequestSpecification givenInvalidPasswordRequest(String api) {
		return givenRequestWithPassword(api, WRONG_PASS);
	}

	private static RequestSpecification givenRequestWithPassword(String api, String password) {
		return givenUnauthenticatedRequest(api).auth().basic(USER, password);
	}

	public static RequestSpecification withJson(RequestSpecification it, Map<String, ? extends Object> properties) {
		return it.contentType(ContentType.JSON).body(JsonExtensions.asJson(properties));
	}

	public static String asPath(List<? extends String> values) {
		return ("/" + values.stream().collect(Collectors.joining("/"))).replaceAll("//", "/");
	}

	public static String location(Response it) {
		final String header = it.header(LOCATION);
		return Strings.isNullOrEmpty(header) ? "" : header;
	}

	public static String renderWithFields(String it, Object object) {
		return render(it, getFieldValueMap(object));
	}

	public static Map<String, Object> getFieldValueMap(Object object) {
		return Arrays.asList(object.getClass().getFields()).stream().collect(Collectors.toMap(Field::getName, f -> {
			try {
				return f.get(object);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}));
	}

	public static String render(String it, Map<String, Object> fieldValueMap) {
		return new StrSubstitutor(fieldValueMap).replace(it);
	}

	/**
	 * Asserts whether a JSON body inside the given Response object have the same "state" field as the given state parameter.
	 */
	public static void inState(Response it, String state) {
		it.then().body("state", CoreMatchers.equalTo(state));
	}

	/**
	 * Returns the port used in the test environment, this is equivalent with the jetty.port configuration parameter, or with 8080 if no jetty.port parameter found.
	 */
	public static int getPort() {
		final String jettyPortProp = System.getProperty("jetty.port");
		return jettyPortProp != null ? Integer.valueOf(jettyPortProp) : 8080;
	}
	
	public static void expectStatus(Response it, int expectedStatus) {
		if (it.statusCode() != expectedStatus) {
			System.err.println("Web server may reject your request, check access log");
			System.err.println("Headers: " + it.headers());
			System.err.println("Content-Type: " + it.getContentType());
			System.err.println("Body: " + it.body().asString());
		}
		it.then().statusCode(expectedStatus);
	}
	
	public static String lastPathSegment(String path) {
		return Iterables.getLast(Splitter.on('/').splitToList(path));
	}
	
	public static String joinPath(String... segments) {
		return Joiner.on('/').join(segments);
	}
	
	public static RequestSpecification withFile(RequestSpecification it, String file, Class<?> cp) {
		return it.multiPart(PlatformUtil.toAbsolutePath(cp, file).toFile());
	}
	
	// Simple REST operations
	
	public static Response get(String api, String...segments) {
		return givenAuthenticatedRequest(api).get(asPath(Arrays.asList(segments)));
	}
	
	public static Response delete(String api, String...segments) {
		return givenAuthenticatedRequest(api).delete(asPath(Arrays.asList(segments)));
	}
	
	public static Response postJson(String api, Map<String, ?> json, String...segments) {
		return withJson(givenAuthenticatedRequest(api), json).post(asPath(Arrays.asList(segments)));
	}

	public static Response putJson(String api, Map<String, ?> json, String...segments) {
		return withJson(givenAuthenticatedRequest(api), json).put(asPath(Arrays.asList(segments)));
	}
	
}
