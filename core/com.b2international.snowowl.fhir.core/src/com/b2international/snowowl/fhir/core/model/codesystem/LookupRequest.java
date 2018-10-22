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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.AssertTrue;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Sets;

/**
 * This class represents a FHIR lookup operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
@JsonDeserialize(builder = LookupRequest.Builder.class)
@JsonPropertyOrder({"code", "system", "version", "coding", "date", "displayLanguage", "property"})
public class LookupRequest {
	
	// The code that is to be located. If a code is provided, a system must be provided (0..1)
	private final Code code;

	// The system for the code that is to be located (0..1)
	private final Uri system;
	
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
	private final Code displayLanguage;
	
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
	@FhirType(FhirDataType.PART)
	private final Collection<Code> property;
	
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
		this.property = properties;
	}
	
	public String getCode() {
		if (code != null) {
			return code.getCodeValue();
		} else if (coding != null) {
			return coding.getCode().getCodeValue();
		}
		return null;
	}

	public String getSystem() {
		if (system != null) {
			return system.getUriValue();
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

	public Code getDisplayLanguage() {
		return displayLanguage;
	}

	public Collection<Code> getProperties() {
		return property;
	}
	
	public Collection<String> getPropertyCodes() {
		return property.stream().map(p -> p.getCodeValue()).collect(Collectors.toSet());
	}

	/**
	 * Returns <code>true</code> if the given code is present in the given collection of properties, returns <code>false</code> otherwise.
	 * @param properties
	 * @param property
	 * @return
	 */
	public final boolean containsProperty(Code propertyCode) {
		return property.contains(propertyCode);
	}
	
	/**
	 * Returns true if the property is requested
	 * @param the property to check
	 * @return
	 */
	public boolean isPropertyRequested(IConceptProperty conceptProperty) {
		return containsProperty(conceptProperty.getCode());
	}
	
	/**
	 * Returns true if the <i>version</i> property is requested to be returned.
	 * @return
	 */
	public final boolean isVersionPropertyRequested() {
		return containsProperty(SupportedCodeSystemRequestProperties.VERSION.getCode());
	}
	
	/**
	 * Returns true if the <i>designation</i> property is requested to be returned.
	 * @return
	 */
	public final boolean isDesignationPropertyRequested() {
		return containsProperty(SupportedCodeSystemRequestProperties.DESIGNATION.getCode());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@AssertTrue(message = "Source needs to be set either via code/system or code or codeable concept")
	private boolean isSourceValid() {
		System.out.println("   *** I am in the validating phase!");
		return true;
	} 
	
	@AssertTrue(message = "Code is not provided for the system")
	private boolean isCodeMissing() {

		if (system != null && code == null) {
			return false;
		}
		return true;
	}
	
	@AssertTrue(message = "System is missing for provided code")
	private boolean isSystemMissing() {

		if (system == null && code != null) {
			return false;
		}
		return true;
	}
	
	
	@AssertTrue(message = "Code/system/version and Coding do not match. Probably would make sense to specify only one of them.")
	private boolean isCodeOrCodingInvalid() {

		if (code != null && coding != null) {
			if (!coding.getCode().getCodeValue().equals(code.getCodeValue())) {
				return false;
			}
			
			if (!coding.getSystem().getUriValue().equals(system.getUriValue())) {
				return false;
			}
			
			if (!Objects.equals(coding.getVersion(), version)) {
				return false;
			}
		}
		return true;
	}
	
	@AssertTrue(message = "Both system URI and version tag identifies a version.")
	private boolean isUriVersionInvalid() {

		//SNOMED CT specific, both the URI and version identifies the version
		if (system != null) {
			if (system.getUriValue().startsWith("http://snomed.info/sct") 
					&& system.getUriValue().contains("version")
					&& version != null) {
				return false;
			}
		}
		return true;
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<LookupRequest> {

		private Code code;
		private Uri system;
		private String version;
		private Coding coding;
		private Date date;
		private Code displayLanguage;
		private Set<Code> properties = Sets.newHashSet();
		
		Builder() {}
		
		public Builder code(final String code) {
			this.code = new Code(code);
			return this;
		}

		public Builder system(final String system) {
			this.system = new Uri(system);
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
			this.displayLanguage = new Code(displayLanguage);
			return this;
		}

		/**
		 * Alternative property deserializer to be used when converting FHIR Parameters representation to LookupRequest. Multi-valued property expand.
		 */
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		Builder property(Collection<Code> props) {
			properties = Collections3.toImmutableSet(props);
			return this;
		}
		
		public Builder properties(Collection<String> properties) {
			this.properties = properties.stream().map(p -> new Code(p)).collect(Collectors.toSet());
			return this;
		}
		
		public Builder codeProperties(Collection<Code> properties) {
			this.properties = Collections3.toImmutableSet(properties);
			return this;
		}
		
		public Builder addProperty(String property) {
			this.properties.add(new Code(property));
			return this;
		}

		@Override
		protected LookupRequest doBuild() {
			return new LookupRequest(code, system, version, coding, date, displayLanguage, properties);
		}

	}
	
}
