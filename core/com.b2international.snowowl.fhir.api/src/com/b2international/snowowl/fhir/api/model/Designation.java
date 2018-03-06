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
package com.b2international.snowowl.fhir.api.model;

import java.lang.reflect.Field;

import com.b2international.snowowl.fhir.api.model.serialization.FhirDesignation;
import com.b2international.snowowl.fhir.api.model.serialization.FhirParameter;

public class Designation {
	
	//The language code this designation is defined for (0..1)
	private String languageCode;
	
	//A code that details how this designation would be used (0..1)
	private Coding use;
	
	//The text value for this designation (1..1)
	private String value;
	
	Designation(String languageCode, Coding use, String value) {
		this.languageCode = languageCode;
		this.use = use;
		this.value = value;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}

	public Coding getUse() {
		return use;
	}

	public String getValue() {
		return value;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String languageCode;
		private Coding use;
		private String value;

		public Builder langaugeCode(final String languageCode) {
			this.languageCode = languageCode;
			return this;
		}
		
		public Builder use(Coding use) {
			this.use = use;
			return this;
		}
		
		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Designation build() {
			return new Designation(languageCode, use, value);
		}
		
		public FhirDesignation buildSerializableBean() throws Exception {
			Designation designation = new Designation(languageCode, use, value);
			
			FhirDesignation fhirDesignation = new FhirDesignation();
			
			Field[] fields = Designation.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(designation);
				String type = "value" + field.getType().getSimpleName();
				FhirParameter parameter = new FhirParameter(field.getName(), type, value);
				fhirDesignation.add(parameter);
			}
			return fhirDesignation;
		}
		
	}
	
}
