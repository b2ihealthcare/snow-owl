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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * This class represents a FHIR lookup operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
@JsonDeserialize(builder = LookupRequest.Builder.class)
@JsonPropertyOrder({"code", "system", "version", "coding"})
public class LookupRequest {
	
	// The code that is to be located. If a code is provided, a system must be provided (0..1)
	private final String code;

	// The system for the code that is to be located (0..1)
	private final String system;
	
	// The version that these details are based on (0..1)
	private final String version;

	// The coding to look up (0..1)
	private final Coding coding;
	
	/*
	 * The date for which the information should be returned. Normally, this is the
	 * current conditions (which is the default value) but under some circumstances,
	 * systems need to acccess this information as it would have been in the past. A
	 * typical example of this would be where code selection is constrained to the
	 * set of codes that were available when the patient was treated, not when the
	 * record is being edited. Note that which date is appropriate is a matter for
	 * implementation policy.
	 */
	private final Date date;
	
	//The requested language for display (see ExpansionProfile.displayLanguage)
	private final String displayLanguage;
	
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
	private final Collection<String> properties;
	
	LookupRequest(
			String code, 
			String system, 
			String version, 
			Coding coding, 
			Date date, 
			String displayLanguage,
			Collection<String> properties) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.coding = coding;
		this.date = date;
		this.displayLanguage = displayLanguage;
		this.properties = properties;
	}
	
	public String getCode() {
		if (code != null) {
			return code;
		} else if (coding != null) {
			return coding.getCode().getCodeValue();
		}
		return null;
	}

	public String getSystem() {
		if (system != null) {
			return system;
		} else if (coding != null && coding.getSystem() != null) {
			return coding.getSystem().getUriValue();
		}
		return null;
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

	public String getDisplayLanguage() {
		return displayLanguage;
	}

	public Collection<String> getProperties() {
		return properties;
	}

	/**
	 * Returns <code>true</code> if the given code is present in the given collection of properties, returns <code>false</code> otherwise.
	 * @param properties
	 * @param property
	 * @return
	 */
	public final boolean containsProperty(String property) {
		return properties.contains(property);
	}
	
	/**
	 * Returns true if the <i>name</i> property is requested to be returned.
	 * @return
	 */
	public final boolean isNamePropertyRequested() {
		return properties == null ||
			properties.isEmpty() ||
			containsProperty(SupportedCodeSystemRequestProperties.NAME.getCodeValue());
	}
	
	/**
	 * Returns true if the <i>version</i> property is requested to be returned.
	 * @return
	 */
	public final boolean isVersionPropertyRequested() {
		return properties == null ||
			properties.isEmpty() ||
			containsProperty(SupportedCodeSystemRequestProperties.VERSION.getCodeValue());
	}
	
	/**
	 * Returns true if the <i>display</i> property is requested to be returned.
	 * @return
	 */
	public final boolean isDisplayPropertyRequested() {
		return properties == null ||
			properties.isEmpty() ||
			containsProperty(SupportedCodeSystemRequestProperties.DISPLAY.getCodeValue());
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<LookupRequest> {

		private String code;
		private String system;
		private String version;
		private Coding coding;
		private Date date;
		private String displayLanguage;
		private Set<String> properties = Collections.emptySet();
		
		Builder() {}
		
		public Builder code(final String code) {
			this.code = code;
			return this;
		}

		public Builder system(final String system) {
			this.system = system;
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
		
		public Builder date(String date) {
			try {
				this.date = Dates.parse(date, FhirConstants.DATE_TIME_FORMAT);
			} catch (SnowowlRuntimeException e) {
				throw new BadRequestException("Incorrect date format '%s'.", date);
			}
			return this;
		}
		
		public Builder displayLanguage(final String displayLanguage) {
			this.displayLanguage = displayLanguage;
			return this;
		}

		/**
		 * Alternative property deserializer to be used when converting FHIR Parameters representation to LookupRequest. Multi-valued property expand.
		 */
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		Builder property(Collection<String> properties) {
			return properties(properties);
		}
		
		public Builder properties(Collection<String> properties) {
			this.properties = Collections3.toImmutableSet(properties);
			return this;
		}

		@Override
		protected LookupRequest doBuild() {
			return new LookupRequest(code, system, version, coding, date, displayLanguage, properties);
		}

	}
	
}
