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
package com.b2international.snowowl.snomed.api.rest.concept

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.jayway.restassured.response.Response
import com.b2international.snowowl.snomed.api.rest.concept.*
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers
import java.util.UUID
import java.util.Date

/**
 * @since 1.0
 */
Feature: SnomedConceptCreateOnTaskApi

	Background: 
		static String API = "/snomed-ct"
		var req = givenAuthenticatedRequest(API)
		var Response res;
		
		var public taskId = UUID.randomUUID.toString
		var public String version;
	
	Scenario: New Concept on not existing task
	
		Given version "MAIN"
		And new concept
		When sending POST to "/${version}/tasks/invalid/concepts"
		Then return "404" status
		And return body with status "404"

	Scenario: New Concept on a newly created task
	
		Given version "MAIN"
		And new concept
		And a newly created task at "/${version}/tasks"
			givenAuthenticatedRequest(API).withJson(#{
				"taskId" -> taskId,
				"description" -> "New Concept on an existing SNOMED CT Task"
			}).post(args.first.renderWithFields(this)).then.statusCode(201)
		When sending POST to "/${version}/tasks/${taskId}/concepts"
		Then return "201" status
		And return location header pointing to "/${version}/tasks/${taskId}/concepts"
		And return empty body
	
	Scenario: New Concept on a newly created task with existing identifier
		
		var public conceptId = SnomedIdentifiers.generateConceptId()
		
		Given version "MAIN"
		And new concept with id
		And a newly created task at "/${version}/tasks"
		When sending POST to "/${version}/tasks/${taskId}/concepts"
		Then return "201" status
		And return location header pointing to "/${version}/tasks/${taskId}/concepts/${conceptId}"
		And return empty body