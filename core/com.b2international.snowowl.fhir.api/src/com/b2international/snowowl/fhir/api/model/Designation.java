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
package com.b2international.snowowl.fhir.api.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.core.annotation.Order;

import com.b2international.snowowl.fhir.api.model.serialization.FhirParameter;
import com.google.common.collect.Lists;

/**
 * 
 * @since 6.3
 */
public class Designation {
	
	//The language code this designation is defined for (0..1)
	@Order(value=1)
	private String language;
	
	//A code that details how this designation would be used (0..1)
	@Order(value=2)
	private Coding use;
	
	//The text value for this designation (1..1)
	@Order(value=3)
	private String value;
	
	Designation(final String language, final Coding use, final String value) {
		this.language = language;
		this.use = use;
		this.value = value;
	}
	
	public String getLanguage() {
		return language;
	}

	public Coding getUse() {
		return use;
	}

	public String getValue() {
		return value;
	}
	
	/**
	 * This method builds the object of the serialized representation
	 * of a designation:
	 * <pre>{@code
	   	{"name" : "languageCode", "valueCode" : "uk"},
		   	{"name" : "value", "valueString" : "whatever string this is"},
			{"name": "use", 
			"valueCoding" : {
				"code" : "code",
				"systemUri" : "systemUri",
				"version" : "version",
				"display" : null,
				"userSelected" : false
			}
			</pre>
		 *  @return
	 * @throws Exception
	 */
	public Collection<FhirParameter> toSerializedBean() throws Exception {
		//Designation designation = new Designation(language, use, value);
		
		List<FhirParameter> designationParameters = Lists.newArrayList();
		
		Field[] fields = Designation.class.getDeclaredFields();
		Arrays.sort(fields, new FieldOrderComparator());
		
		for (Field field : fields) {
			//field.setAccessible(true);
			Object value = field.get(this);
			String type = "value" + field.getType().getSimpleName();
			FhirParameter parameter = new FhirParameter(field.getName(), type, value);
			designationParameters.add(parameter);
		}
		return designationParameters;
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
		
		public Builder use(final Coding use) {
			this.use = use;
			return this;
		}
		
		public Builder value(final String value) {
			this.value = value;
			return this;
		}

		public Designation build() {
			return new Designation(languageCode, use, value);
		}
	}
	
}
