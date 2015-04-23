/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.snomed.api.rest.concept

import com.jayway.restassured.response.Response
import java.util.Date
import static org.hamcrest.CoreMatchers.*;

import com.b2international.snowowl.snomed.api.rest.concept.*
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @author mczotter
 * @since 1.0
 */
Feature: SnomedConceptCreateOnVersionApi

	Background:
		static String API = "/snomed-ct"
		var req = givenAuthenticatedRequest(API)
		var Response res;
		var public String version;
		
		val correctAcceptabilityMap = #{ REFSET_LANGUAGE_TYPE_UK -> "PREFERRED" }
		val incorrectAcceptabilityMap = #{ "1" -> "PREFERRED" }
		
	Scenario: New Concept on a not existing SNOMED CT version
		
		Given version "1998-01-31"
			version = args.first
		And new concept
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${version}/concepts"
			res = req.post(args.first.renderWithFields(this))
		Then return "404" status
			// if the status code is not the expected one print the method body to track failures easily
			res.expectStatus(args.first.toInt)
		And return body with status "404"
			res.then.body("status", equalTo(args.first.toInt))
			
	Scenario: New Concept under not existing Concept
		
		Given version "MAIN"
		And new concept under "1000"
			req.withJson(#{
				"parentId" -> args.first,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${version}/concepts"
		Then return "400" status
		And return body with status "400"

	Scenario: New Concept with unknown FSN language refset
	
		Given version "MAIN"
		And new concept with unknown refset
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> incorrectAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${version}/concepts"
		Then return "400" status
		And return body with status "400"

	Scenario: New Concept in an unkown module
	
		Given version "MAIN"
		And new concept with unknown module
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> "1",
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${version}/concepts"
		Then return "400" status
		And return body with status "400"

	Scenario: New Concept on an existing SNOMED CT version
	
		Given version "MAIN"
		And new concept
		When sending POST to "/${version}/concepts"
		Then return "201" status
		And return location header pointing to "/${version}/concepts"
			res.location should contain args.first.renderWithFields(this) 
		And return empty body
			res.getBody.asString => ""
	
	Scenario: New Concept with existing identifier
		
		var public conceptId = SnomedIdentifiers.generateConceptId()
		
		Given version "MAIN"
		And new concept with id
			req.withJson(#{
				"id" -> conceptId,
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${version}/concepts"
		Then return "201" status
		And return location header pointing to "/${version}/concepts/${conceptId}"
		And return empty body
			