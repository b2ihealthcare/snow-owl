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
package com.b2international.snowowl.snomed.api.rest

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @since 4.1 
 */
class BranchingApiExtensions {
	
	def static String getOrCreate(String api, String parent, String branchName) {
		println("wtf")
		val res = api.get("branches", parent, branchName)
		if (res.getStatusCode == 404) {
			api.postJson(#{
				"parent" -> parent,
				"name" -> branchName
			}, "branches").expectStatus(201)
		}
		return parent + "/" + branchName
	}
	
}