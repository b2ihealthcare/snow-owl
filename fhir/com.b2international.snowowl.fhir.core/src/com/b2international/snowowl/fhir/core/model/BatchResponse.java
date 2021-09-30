/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Response Metadata
 * 
 * @see <a href="https://www.hl7.org/fhir/bundle-definitions.html#Bundle.entry.response">FHIR:Bunlde:Entry:Response</a>
 * @since 8.0.0
 */
public class BatchResponse {
	
	@Mandatory
	@NotEmpty
	@JsonProperty
	private String status;
	
	@JsonCreator
	public BatchResponse(@JsonProperty("status") final String status) {
		this.status = status;
	}
	
	public static BatchResponse createOkResponse() {
		return new BatchResponse("200");
	}
	
	public String getStatus() {
		return status;
	}

}
