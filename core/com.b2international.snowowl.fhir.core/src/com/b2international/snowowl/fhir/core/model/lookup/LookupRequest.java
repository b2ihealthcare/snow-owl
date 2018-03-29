/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.lookup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.conversion.LookupRequestConverter;
import com.b2international.snowowl.fhir.core.model.conversion.Order;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * This class represents a FHIR lookup operation request.
 * This domain model class is capable of building itself from a collection of
 * serialized parameters. See {@link #toModelObject()}.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
@ApiModel
@JsonDeserialize(converter=LookupRequestConverter.class)
public final class LookupRequest {
	
	//FHIR header "resourceType" : "Parameters",
	@JsonProperty
	private final String resourceType = "Parameters";
	
	@JsonProperty(value="parameter")
	private List<SerializableParameter> parameters = Collections.emptyList();
	
	// The code that is to be located. If a code is provided, a system must be provided (0..1)
	//@ApiModelProperty(dataType = "java.lang.String")
	@Order(value = 1)
	private Code code;

	// The system for the code that is to be located (0..1)
	@Order(value = 2)
	private Uri system;
	
	// The version that these details are based on (0..1)
	@Order(value = 3)
	private String version;

	// The coding to look up (0..1)
	@Order(value = 4)
	private Coding coding;
	
	/*
	 * The date for which the information should be returned. Normally, this is the
	 * current conditions (which is the default value) but under some circumstances,
	 * systems need to acccess this information as it would have been in the past. A
	 * typical example of this would be where code selection is constrained to the
	 * set of codes that were available when the patient was treated, not when the
	 * record is being edited. Note that which date is appropriate is a matter for
	 * implementation policy.
	 */
	@Order(value = 5)
	private Date date;
	
	//The requested language for display (see ExpansionProfile.displayLanguage)
	@Order(value = 6)
	private Code displayLanguage;
	
	/*
	 * A property that the client wishes to be returned in the output. If no
	 * properties are specified, the server chooses what to return. The following
	 * properties are defined for all code systems: url, name, version (code system
	 * info) and code information: display, definition, designation, parent and
	 * child, and for designations, lang.X where X is a designation language code.
	 * Some of the properties are returned explicit in named parameters (when the
	 * names match), and the rest (except for lang.X) in the property parameter
	 * group
	 */
	@Order(value = 7)
	private Collection<Code> properties = Lists.newArrayList();
	
	//For Jackson
	LookupRequest() {}
	
	LookupRequest(
			Code code, 
			Uri system, 
			String version, 
			Coding coding, 
			Date date, 
			Code displayLanguage,
			Collection<Code> properties) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.coding = coding;
		this.date = date;
		this.displayLanguage = displayLanguage;
		this.properties = properties;
	}
	
	public Code getCode() {
		return code;
	}

	public Uri getSystem() {
		return system;
	}

	public String getVersion() {
		return version;
	}

	public Coding getCoding() {
		return coding;
	}

	public Date getDate() {
		return date;
	}

	public Code getDisplayLanguage() {
		return displayLanguage;
	}

	public Collection<Code> getProperties() {
		return properties;
	}

	/**
	 * @return parameter collection to be serialized
	 */
	public Collection<SerializableParameter> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return "LookupRequest [code=" + code + ", system=" + system + ", version=" + version + ", coding=" + coding
				+ ", date=" + date + ", displayLanguage=" + displayLanguage + ", properties=" + Arrays.toString(properties.toArray()) + "]";
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends ValidatingBuilder<LookupRequest> {

		private Code code;
		private Uri system;
		private String version;
		private Coding coding;
		private Date date;
		private Code displayLanguage;
		private Collection<Code> properties = Collections.emptyList();
		
		public Builder code(final Code code) {
			this.code = code;
			return this;
		}

		public Builder code(final String codeValue) {
			this.code = new Code(codeValue);
			return this;
		}
		
		public Builder system(final Uri system) {
			this.system = system;
			return this;
		}

		public Builder system(final String systemValue) {
			this.system = new Uri(systemValue);
			return this;
		}
		
		public Builder version(final String version) {
			this.version = version;
			return this;
		}

		public Builder coding(Coding coding) {
			this.coding = coding;
			return this;
		}
		
		public Builder coding(String code, String system, String display) {
			Coding coding = Coding.builder()
				.code(code)
				.system(system)
				.display(display).build();
			
			this.coding = coding;
			return this;
		}
		
		public Builder date(Date date) {
			this.date = date;
			return this;
		}
		
		public Builder date(String dateString) {
			try {
				this.date = Dates.parse(dateString, FhirConstants.DATE_TIME_FORMAT);
			} catch (SnowowlRuntimeException e) {
				throw new BadRequestException("Incorrect date format '%s'.", dateString);
			}
			return this;
		}
		
		public Builder displayLanguage(final Code displayLanguage) {
			this.displayLanguage = displayLanguage;
			return this;
		}

		public Builder displayLanguage(final String displayLanguage) {
			this.displayLanguage = new Code(displayLanguage);
			return this;
		}

		public Builder properties(Collection<String> properties) {
			this.properties = Collections3.toImmutableSet(properties)
					.stream()
					.map(Code::new)
					.collect(Collectors.toSet());
			return this;
		}

		@Override
		protected LookupRequest doBuild() {
			return new LookupRequest(code, system, version, coding, date, displayLanguage, properties);
		}

	}
	
}
