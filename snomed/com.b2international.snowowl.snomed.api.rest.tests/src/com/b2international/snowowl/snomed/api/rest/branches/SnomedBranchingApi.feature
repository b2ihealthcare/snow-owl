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
package com.b2international.snowowl.snomed.api.rest.branches

import com.jayway.restassured.response.Response
import java.util.Date
import java.util.UUID
import com.b2international.snowowl.snomed.api.rest.components.*

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import java.util.Collection

/**
 * @since 2.0
 */
Feature: SnomedBranchingApi

	Background:
		static String API = "/snomed-ct/v2"
		var public String branchName = UUID.randomUUID.toString
		var public String branch2Name = UUID.randomUUID.toString
		var description = "Description at " + new Date
		var req = givenAuthenticatedRequest(API)
		var Response res
		val metadata = newHashMap()
		
	Scenario: Nonexistent SNOMED CT branch
	
		Given branchName "nonexistent"
			branchName = args.first
		When sending GET to "/branches/MAIN/${branchName}"
			res = req.get(args.first.renderWithFields(this))
		Then return "404" status
		And return body with status "404"
		
	Scenario: New SNOMED-CT branch under nonexistent parent 
	
		Given new SNOMED-CT branch request under parent branch "nonexistent" with name "${branchName}"
			val parent = args.first.renderWithFields(this)
			branchName = args.second.renderWithFields(this)
			req.withJson(#{
				"parent" -> parent,
  				"name" -> branchName,
  				"metadata" -> metadata
			})
		When sending POST to "/branches"
		Then return "400" status
		And return body with status "400"
		
	Scenario: New SNOMED CT branch under MAIN
	
		Given new SNOMED-CT branch request under parent branch "MAIN" with name "${branchName}"
		When sending POST to "/branches"
		Then return "201" status
		And return location header pointing to "/branches/MAIN/${branchName}"
		And return "200" with body when sending GET to "/branches/MAIN/${branchName}"
			res = API.get(args.second.renderWithFields(this))
			res.expectStatus(args.first.toInt)
	
	Scenario: Duplicate branch on same parent
	
		Given new SNOMED-CT branch request under parent branch "MAIN" with name "${branchName}"
		When sending POST to "/branches"
		And sending the second POST to "/branches"
			res = req.post(args.first.renderWithFields(this))
		Then return "409" status
		And return body with status "409"
		
	Scenario: Delete SNOMED-CT branch
		
		Given new SNOMED-CT branch under parent branch "MAIN" with name "${branchName}"
			val parent = args.first.renderWithFields(this)
			val name = args.second.renderWithFields(this)
			API.postJson(#{
				"parent" -> parent,
  				"name" -> name
			}, "branches").expectStatus(201)
		When sending DELETE to "/branches/MAIN/${branchName}"
			res = req.delete(args.first.renderWithFields(this))
		Then return "204" status
		And branch "MAIN/${branchName}" should be deleted
			res = API.get("branches", args.first.renderWithFields(this))
			res.expectStatus(200)
			res.getBody.path("deleted") should be true
			
	Scenario: Delete SNOMED-CT Branch with child branch
		
		Given new SNOMED-CT branch under parent branch "MAIN" with name "${branchName}"
		And new SNOMED-CT branch under parent branch "MAIN/${branchName}" with name "${branch2Name}"
		When sending DELETE to "/branches/MAIN/${branchName}"
		Then return "204" status
		And branch "MAIN/${branchName}" should be deleted
		And branch "MAIN/${branchName}/${branch2Name}" should be deleted

	Scenario: Create new SNOMED-CT branch on deleted branch
		
		Given new SNOMED-CT branch under parent branch "MAIN" with name "${branchName}"
		And sending DELETE to "/branches/MAIN/${branchName}"
		And return "204" status
		And branch "MAIN/${branchName}" should be deleted
		And new SNOMED-CT branch request under parent branch "MAIN/${branchName}" with name "childOfDeletedBranch"
		When sending POST to "/branches"
		Then return "400" status
		And return body with status "400"
		
	Scenario: Newly created SNOMED-CT branches appear on branch collection
	
		Given new SNOMED-CT branch under parent branch "MAIN" with name "${branchName}"
		When sending GET to "/branches"
		Then return "200" status
		And return body with items
			res.getBody.path("items.name") should be [
				it instanceof Collection && (it as Collection).contains(branchName)
			]
		And new branch appear on children list at "/branches/MAIN/children"
			res = API.get(args.first.renderWithFields(this))
			res.expectStatus(200)
			res.getBody.path("items.name") should be [
				it instanceof Collection && (it as Collection).contains(branchName)
			]

	Scenario: Metadata support
		
		Given metadata "description" with value "My awesome branch on MAIN"
			metadata.put(args.first, args.second)
		And new SNOMED-CT branch request under parent branch "MAIN" with name "${branchName}"
		When sending POST to "/branches"
		Then return "201" status
		And return location header pointing to "/branches/MAIN/${branchName}"
		And return "200" with body when sending GET to "/branches/MAIN/${branchName}"
		And return body containing metadata "My awesome branch on MAIN" on JSON path "metadata.description"
			res.getBody.path(args.second) should be args.first
	