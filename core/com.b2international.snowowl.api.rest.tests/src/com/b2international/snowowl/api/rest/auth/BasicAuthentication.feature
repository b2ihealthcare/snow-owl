/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.api.rest.auth

import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.response.Response

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @since 1.0
 */
Feature: Basic Authentication

	The RESTful services of Snow Owl require basic authentication, with the exception of the services providing API documentation. 
	This feature checks if authentication works as expected for a certain protected endpoint.

	Background:
		RequestSpecification request
		Response response

	Scenario: Deny access if user is not authenticated

		Given an unauthenticated request to "/admin"
			request = givenUnauthenticatedRequest(args.first)
		When the user attempts to access "/repositories"
			response = request.when.get(args.first)
		Then return "401" status
			response.then.assertThat.statusCode(args.first.toInt)

	Scenario: Deny access if user specifies an incorrect password

		Given a request with an incorrect password to "/admin"
			request = givenInvalidPasswordRequest(args.first)
		When the user attempts to access "/repositories"
		Then return "401" status

	Scenario: Allow access with correct password

		Given a request with the correct password to "/admin"
			request = givenAuthenticatedRequest(args.first)
		When the user attempts to access "/repositories"
		Then return "200" status
