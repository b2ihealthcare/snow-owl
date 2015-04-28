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
package com.b2international.snowowl.snomed.api.rest.io

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.jayway.restassured.response.Response
import com.b2international.snowowl.snomed.api.rest.concept.*
import com.b2international.snowowl.snomed.api.rest.branches.*

/**
 * @since 2.0
 */
Feature: SnomedImportApi

	Background:
		static String UK_LANG_REFSET = "900000000000508004"
		static String API = "/snomed-ct/v2"
		static String MEDIA_TYPE = ""
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String importTicketId
	
	Scenario: Configure SNOMED CT Import
		
		Given SNOMED CT "DELTA" import configuration request on branch "MAIN"
			req.withJson(#{
				"type" -> args.first,
  				"branchPath" -> args.second,
  				// TODO remove unnecessary definition of langRefSetId when import supports it
  				"languageRefSetId" -> UK_LANG_REFSET
				
			})
		When sending POST to "/imports"
		Then return "201" status
		And return location header pointing to "/imports"
		And import configuration should be accessible via returned location header
			importTicketId = res.location.lastPathSegment
			expectResourceAt(API, "imports", importTicketId)
		
	Scenario: Delete SNOMED CT import configuration
		
		Given SNOMED CT "DELTA" import configuration on branch "MAIN"
			val response = givenAuthenticatedRequest(API)
				.withJson(#{
					"type" -> args.first,
	  				"branchPath" -> args.second,
	  				// TODO remove unnecessary definition of langRefSetId when import supports it
	  				"languageRefSetId" -> UK_LANG_REFSET
					
				})
				.post("/imports")
			response.expectStatus(201)
			importTicketId = response.location.lastPathSegment
		When sending DELETE to "/imports/${importTicketId}"
		Then return "204" status
		And configuration should not be accessible anymore
			expectNoResourceAt(API, "imports", importTicketId)
			