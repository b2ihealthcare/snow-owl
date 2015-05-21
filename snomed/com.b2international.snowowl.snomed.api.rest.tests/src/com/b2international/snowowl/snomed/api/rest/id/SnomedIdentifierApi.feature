/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.id

import com.jayway.restassured.response.Response

import com.b2international.snowowl.snomed.api.rest.components.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @since 1.0
 */
Feature: SnomedIdentifierApi

	Background: 
		static String API = "/snomed-ct/v2"
		var req = givenAuthenticatedRequest(API)
		var Response res
		var componentApiSegment = "unknown"

	Scenario: Generate SNOMED CT International Concept ID
	
		Given new SNOMED CT "Concept" Identifier request
			componentApiSegment = args.first.toLowerCase + "s"
			req.withJson(#{
				"type" -> args.first.toUpperCase
			})
		When sending POST to "/ids"
		Then return "201" status
		And the partition identifier of the returned ID should be "0"
			val expectedPartitionId = args.first.charAt(0)
			val id = res.getBody.path("id") as String
			val actualPartitionId = id.charAt(id.length - 3)
			actualPartitionId should be expectedPartitionId 
		And the component identifier of the returned ID should be "0"
			val expectedComponentId = args.first.charAt(0)
			val id = res.getBody.path("id") as String
			val actualComponentId = id.charAt(id.length - 2)
			actualComponentId should be expectedComponentId
		And the returned ID should be unique on "MAIN"
			val branchPath = args.first
			val id = res.getBody.path("id") as String
			var checkRequest = givenAuthenticatedRequest(API)
			val response = checkRequest.get(asPath(#[branchPath, componentApiSegment, id]))
			response.getStatusCode should be 404
			
	Scenario: Generate SNOMED CT International Description ID
	
		Given new SNOMED CT "Description" Identifier request
		When sending POST to "/ids"
		Then return "201" status
		And the partition identifier of the returned ID should be "0"
		And the component identifier of the returned ID should be "1"
		And the returned ID should be unique on "MAIN"
		
	Scenario: Generate SNOMED CT International Relationship ID
	
		Given new SNOMED CT "Relationship" Identifier request
		When sending POST to "/ids"
		Then return "201" status
		And the partition identifier of the returned ID should be "0"
		And the component identifier of the returned ID should be "2"
		And the returned ID should be unique on "MAIN"
		
	Scenario: Generate SNOMED CT Extension Concept ID
	
		Given new SNOMED CT Extension "Concept" Identifier request with namespace "1000154"
			req.withJson(#{
				"type" -> args.first.toUpperCase,
				"namespace" -> args.second
			})
		When sending POST to "/ids"
		Then return "201" status
		And the partition identifier of the returned ID should be "1"
		And the component identifier of the returned ID should be "0"
		And the namespace of the returned ID should be "1000154"
			val expectedNamespaceId = args.first
			val id = res.getBody.path("id") as String
			val actualNamespaceId = id.substring(id.length - 10, id.length - 3)
			actualNamespaceId should be expectedNamespaceId
		And the returned ID should be unique on "MAIN" 
		
	Scenario: Generate SNOMED CT Extension Description ID
	
		Given new SNOMED CT Extension "Description" Identifier request with namespace "1000154"
		When sending POST to "/ids"
		Then return "201" status
		And the partition identifier of the returned ID should be "1"
		And the component identifier of the returned ID should be "1"
		And the namespace of the returned ID should be "1000154"
		And the returned ID should be unique on "MAIN"

	Scenario: Generate SNOMED CT Extension Relationship ID
	
		Given new SNOMED CT Extension "Relationship" Identifier request with namespace "1000154"
		When sending POST to "/ids"
		Then return "201" status
		And the partition identifier of the returned ID should be "1"
		And the component identifier of the returned ID should be "2"
		And the namespace of the returned ID should be "1000154"
		And the returned ID should be unique on "MAIN"
