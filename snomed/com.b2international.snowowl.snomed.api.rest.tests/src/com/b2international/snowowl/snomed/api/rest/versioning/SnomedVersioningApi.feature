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
package com.b2international.snowowl.snomed.api.rest.versioning

import com.b2international.snowowl.snomed.api.rest.components.*
import com.b2international.snowowl.snomed.api.rest.branching.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import java.util.Map
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import com.b2international.snowowl.snomed.api.rest.io.*

/**
 * @since 2.0
 */
Feature: SnomedVersioningApi

	Background:
		static final String API = "/admin"
		var Map<?, ?> reqBody
		var RequestSpecification req = givenAuthenticatedRequest(API)
		var Response res

	Scenario: Get non-existent SNOMED-CT version
		
		When sending GET to "/codesystems/SNOMEDCT/versions/nonexistent"
			res = req.get(args.first.renderWithFields(this))
		Then return "404" status

	Scenario: Create invalid SNOMED-CT version
		
		Given new "" version request with effective time "20150201"
			req.withJson(#{
				"version" -> args.first.renderWithFields(this),
				"description" -> args.first.renderWithFields(this),
				"effectiveDate" -> args.second
			})
		When sending POST to "/codesystems/SNOMEDCT/versions"
			res = req.post(args.first.renderWithFields(this))
		Then return "400" status

	Scenario: Create valid SNOMED-CT version
		
		Given new "sct-v1" version request with effective time "20150201"
		When sending POST to "/codesystems/SNOMEDCT/versions"
		Then return "201" status
		And version should exists at "/codesystems/SNOMEDCT/versions/sct-v1"
			res = API.get(args.first)
			res.expectStatus(200)
			res.path("effectiveDate") should be "20150201"
			
	Scenario: Create SNOMED-CT version before the last version
		
		Given new "sct-v2" version request with effective time "20150101"
		When sending POST to "/codesystems/SNOMEDCT/versions"
		Then return "400" status

	Scenario: Create SNOMED-CT branch under MAIN and version with same name
		
		Given branch "version-branch-conflict" under "MAIN"
		And new "version-branch-conflict" version request with effective time "20150202"
		When sending POST to "/codesystems/SNOMEDCT/versions"
		Then return "409" status
		And version should not exists as "/codesystems/SNOMEDCT/versions/version-branch-conflict"
			API.get(args.first).expectStatus(404)
