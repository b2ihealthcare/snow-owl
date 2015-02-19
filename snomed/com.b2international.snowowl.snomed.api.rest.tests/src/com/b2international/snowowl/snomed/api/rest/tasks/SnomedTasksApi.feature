/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.snomed.api.rest.tasks

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.jayway.restassured.response.Response
import com.b2international.snowowl.snomed.api.rest.concept.*
import java.util.UUID
import static org.hamcrest.CoreMatchers.*
import java.util.Date

/**
 * @author mczotter
 * @since 1.0
 */
Feature: SnomedTasksApi

	Background:
		static String API = "/snomed-ct"
		var public String version;
		var public String taskId = UUID.randomUUID.toString
		var description = "Description at " + new Date
		var req = givenAuthenticatedRequest(API)
		var Response res;
		
	
	Scenario: New SNOMED CT Task
	
		Given version "MAIN"
		And new task request 
			req.withJson(#{
				"description" -> description,
  				"taskId" -> taskId
			})
		When sending POST to "/${version}/tasks"
		Then return "201" status
		And return location header pointing to "/${version}/tasks/${taskId}"
		
	Scenario: Get new SNOMED CT Task
		
		Given version "MAIN"
		And new task request
		And sending POST to "/${version}/tasks"
		When sending GET to "/${version}/tasks/${taskId}"
			res = givenAuthenticatedRequest(API).get(args.first.renderWithFields(this))
		Then return "200" status
		And representation with taskId
			res.then.body("taskId", equalTo(taskId))
		And representation with description
			res.then.body("description", equalTo(description))
		And representation with timestamp
			res.then.body("baseTimestamp", notNullValue)
		And representation with "SYNCHRONIZED" state
			res.inState(args.first)
				
	Scenario: Change SNOMED CT Task State to PROMOTED
	
		Given version "MAIN"
		And new task request
		And sending POST to "/${version}/tasks"
		When sending PUT to "/${version}/tasks/${taskId}" with state "PROMOTED"
			res = givenAuthenticatedRequest(API).withJson(#{
				"state" -> args.second
			}).put(args.first.renderWithFields(this))
		Then return "204" status
		And the task should be "PROMOTED"
			res = givenAuthenticatedRequest(API).get("/MAIN/tasks/{taskId}", taskId)
			res.inState(args.first)
			
	Scenario: New SNOMED CT task on non existing version
	
		Given version "1998-01-31"
		And new task request
		When sending POST to "/${version}/tasks"
		Then return "404" status
		And return body with status "404"
		
	Scenario: Duplicate SNOMED CT task
	
		Given version "MAIN"
		And new task request
		When sending POST to "/${version}/tasks"
		And sending POST to "/${version}/tasks"
		Then return "409" status
		And return body with status "409"
		
	Scenario: Updating task state to NOT_SYNCHRONIZED is not allowed

		Given version "MAIN"
		And new task request
		And sending POST to "/${version}/tasks"
		When sending PUT to "/${version}/tasks/${taskId}" with state "NOT_SYNCHRONIZED"
		Then return "400" status
		And return body with status "400"
