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
package com.b2international.snowowl.fhir.core.model;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;

/**
 * 0..1
 *  "id" : "<id>", // Logical id of this artifact
  "meta" : { Meta }, // Metadata about the resource
  "implicitRules" : "<uri>", // A set of rules under which this content was created
  "language" : "<code>" // Language of the resource content
 * @author bbanfai
 *
 */
public abstract class FhirResource {
	
	private Code language;
	
	private Id id;
	
	public FhirResource() {
		
	}
	
	public FhirResource(Id id, Code language) {
		this.id = id;
		this.language = language;
	}
	
	public FhirResource(String id, String language) {
		this.id = new Id(id);
		this.language = new Code(language);
	}
	
	public Code getLanguage() {
		return language;
	}
	
	public Id getId() {
		return id;
	}

}
