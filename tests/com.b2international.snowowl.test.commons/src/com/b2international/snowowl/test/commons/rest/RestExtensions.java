/*
 * Copyright 2019-2024 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;
import org.hamcrest.CoreMatchers;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.identity.JWTSupport;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.google.common.base.*;
import com.google.common.collect.Iterables;

import io.restassured.RestAssured;
import io.restassured.common.mapper.resolver.ObjectMapperResolver;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.internal.mapping.Jackson2Mapper;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

/**
 * Useful extension methods when testing Snow Owl's RESTful API. High level REST related syntactic sugars and stuff like 
 * that should be put in here, other API related stuff should go in a separate extension class.
 * 
 * @since 1.0 
 */
public class RestExtensions {

	public static final Joiner COMMA_JOINER = Joiner.on(",");
	public static final String JSON_UTF8 = ContentType.JSON.withCharset(Charsets.UTF_8);
	
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
	
	public static final String USER;
	public static final String PASS; 
	
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
		return givenUnauthenticatedRequest(getPort(), api);
	}

	public static RequestSpecification givenUnauthenticatedRequest(int port, String api) {
		if (INITIALIZE_ONCE.compareAndSet(false, true)) {
			
			// change Base URI if defined as sysarg
			final String serverLocation = System.getProperty("test.server.location");
			if (!Strings.isNullOrEmpty(serverLocation)) {
				RestAssured.baseURI = serverLocation;
			}
			
			RestAssured.config = RestAssuredConfig.config()
				.objectMapperConfig(objectMapperConfig().defaultObjectMapper(new Jackson2Mapper(new CustomJackson2ObjectMapperFactory())))
				.logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

			/*
			 * XXX: ObjectMapperResolver will not detect Jackson 2 if class initialization
			 * happens at a later time on an unsuitable class loader.
			 */
			final Thread thread = Thread.currentThread();
			final ClassLoader classLoader = thread.getContextClassLoader();
			try {
				thread.setContextClassLoader(RestExtensions.class.getClassLoader());
				ObjectMapperResolver.isJackson2InClassPath();
			} finally {
				thread.setContextClassLoader(classLoader);
			}
		}
		
		Preconditions.checkArgument(api.startsWith("/"), "Api param should start with a forward slash: '/'");
		return given().port(port).basePath(CONTEXT + api);
	}

	public static RequestSpecification givenAuthenticatedRequest(String api) {
		return givenRequestWithPassword(api, PASS);
	}
	
	public static RequestSpecification givenAuthenticatedRequest(int port, String api) {
		return givenRequestWithPassword(port, api, PASS);
	}
	
	public static RequestSpecification givenInvalidPasswordRequest(String api) {
		return givenRequestWithPassword(api, WRONG_PASS);
	}

	private static RequestSpecification givenRequestWithPassword(String api, String password) {
		return givenUnauthenticatedRequest(api).auth().preemptive().basic(USER, password);
	}
	
	private static RequestSpecification givenRequestWithPassword(int port, String api, String password) {
		return givenUnauthenticatedRequest(port, api).auth().preemptive().basic(USER, password);
	}
	
	public static RequestSpecification givenRequestWithToken(String api, String token) {
		return givenRequestWithToken(getPort(), api, token);
	}
	
	public static RequestSpecification givenRequestWithToken(int port, String api, String token) {
		return givenUnauthenticatedRequest(port, api).auth().preemptive().oauth2(token);
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
		return new StringSubstitutor(fieldValueMap).replace(it);
	}

	/**
	 * Asserts whether a JSON body inside the given Response object have the same "state" field as the given state parameter.
	 */
	public static void inState(Response it, String state) {
		it.then().body("state", CoreMatchers.equalTo(state));
	}

	/**
	 * Returns the port used in the test environment, this is equivalent with the jetty.http.port configuration parameter, or with 8080 if no jetty.http.port parameter found.
	 */
	public static int getPort() {
		final String jettyPortProp = System.getProperty("jetty.http.port");
		return jettyPortProp != null ? Integer.valueOf(jettyPortProp) : 8080;
	}
	
	public static ValidatableResponse expectStatus(Response it, int expectedStatus) {
		if (it.statusCode() != expectedStatus) {
			System.err.println("Web server may reject your request, check access log");
			System.err.println("Headers: " + it.headers());
			System.err.println("Content-Type: " + it.getContentType());
			System.err.println("Body: " + it.body().asString());
		}
		return it.then().statusCode(expectedStatus);
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
	
	public static String assertCreated(ValidatableResponse response) {
		return lastPathSegment(response.statusCode(201)
				.extract()
				.header("Location"));
	}
	
	public static String generateToken(Permission...permissions) {
		return generateToken(RestExtensions.USER, permissions);
	}
	
	public static String generateToken(String userId, Permission...permissions) {
		return ApplicationContext.getServiceForClass(JWTSupport.class).generate(new User(userId, List.of(permissions)));
	}

}
