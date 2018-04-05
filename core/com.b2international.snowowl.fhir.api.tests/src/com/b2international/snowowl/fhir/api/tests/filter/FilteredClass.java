/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.filter;

import com.b2international.snowowl.fhir.core.model.conversion.Mandatory;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.4
 */
@JsonFilter("TestClassFilter")
public class FilteredClass {

	@JsonProperty
	private String firstName;

	@JsonProperty
	private String lastName;
	
	@Mandatory
	@JsonProperty
	private String id;

	public FilteredClass(String id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "FilteredClass [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
	

}
