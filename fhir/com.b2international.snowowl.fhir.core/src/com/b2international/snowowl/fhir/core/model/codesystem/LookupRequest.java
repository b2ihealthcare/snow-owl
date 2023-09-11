/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.AssertFalse;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.Coding;

import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableSet;

import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.UriDt;

/**
 * This class represents a FHIR lookup operation request's input parameters.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
public final class LookupRequest {

	private static final String SNOMED_BASE_URI = "http://snomed.info/sct/";

	// The code that is to be located. If "code" is provided, "system" must be given as well (0..1)
	private CodeDt code;

	// The system for the code that is to be located (0..1)
	private UriDt system;

	// The version that these details are based on (0..1)
	private String version;

	// A coding to look up (0..1)
	private Coding coding;

	/*
	 * The date for which the information should be returned. Normally, this is the
	 * current conditions (which is the default value) but under some circumstances,
	 * systems need to access this information as it would have been in the past. A
	 * typical example of this would be where code selection is constrained to the
	 * set of codes that were available when the patient was treated, not when the
	 * record is being edited. Note that which date is appropriate is a matter for
	 * implementation policy.
	 */
	private DateTimeDt date;

	// The requested language for display
	private CodeDt displayLanguage;

	/*
	 * A property that the client wishes to be returned in the output. If no
	 * properties are specified, the server chooses what to return. The following
	 * properties are defined for all code systems: 
	 * 
	 * - url
	 * - name
	 * - version (code system info) 
	 * 
	 * ...and code information: 
	 * 
	 * - display
	 * - definition
	 * - designation
	 * - parent
	 * - child
	 * 
	 * ...and for designations, lang.X where X is a designation language code.
	 * 
	 * Some of the properties are returned explicit in named parameters (when the
	 * names match, eg. "display"), and the rest (except for lang.X) in the "property" parameter
	 * group.
	 */
	private Set<CodeDt> properties = ImmutableSet.of();

	public CodeDt getCode() {
		return code;
	}

	public void setCode(final CodeDt code) {
		this.code = code;
	}

	public UriDt getSystem() {
		return system;
	}

	public void setSystem(final UriDt system) {
		this.system = system;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public Coding getCoding() {
		return coding;
	}

	public void setCoding(final Coding coding) {
		this.coding = coding;
	}

	public DateTimeDt getDate() {
		return date;
	}

	public void setDate(final DateTimeDt date) {
		this.date = date;
	}

	public CodeDt getDisplayLanguage() {
		return displayLanguage;
	}

	public void setDisplayLanguage(final CodeDt displayLanguage) {
		this.displayLanguage = displayLanguage;
	}

	public Set<CodeDt> getProperties() {
		return properties;
	}

	public void setProperties(final Iterable<CodeDt> properties) {
		this.properties = Collections3.toImmutableSet(properties);
	}

	// ---------------
	// Utility methods
	// ---------------

	public Set<String> getPropertyCodes() {
		return properties.stream()
			.map(p -> p.getValueAsString())
			.collect(Collectors.toSet());
	}

	/**
	 * @param propertyCode - the property code to check
	 * @return <code>true</code> if the given code is present in the given
	 * collection of properties, returns <code>false</code> otherwise.
	 */
	public final boolean containsProperty(final CodeDt propertyCode) {
		return properties != null && properties.contains(propertyCode);
	}

	/**
	 * @param codeSystemProperty - the property to check
	 * @return <code>true</code> if the property is requested, <code>false</code>
	 * otherwise.
	 */
	public boolean containsProperty(final CodeSystem.PropertyComponent codeSystemProperty) {
		return containsProperty(new CodeDt(codeSystemProperty.getCode()));
	}

	// ----------
	// Invariants
	// ----------

	@AssertFalse(message = "Code is not provided for the system")
	private boolean isCodeMissing() {

		if (system != null && code == null) {
			return true;
		}

		if (coding != null && coding.getCode() == null) {
			return true;
		}

		return false;
	}

	@AssertFalse(message = "System is missing for provided code")
	private boolean isSystemMissing() {

		if (system == null && code != null) {
			return true;
		}

		if (coding != null && coding.getSystem() == null) {
			return true;
		}

		return false;
	}

	@AssertFalse(message = "Code, system, version and coding inputs do not match. Probably would make sense to specify only one of them.")
	private boolean isCodeOrCodingInconsistent() {

		if (code != null && coding != null) {
			if (!coding.getCode().equals(code.getValueAsString())) {
				return true;
			}

			if (!coding.getSystem().equals(system.getValueAsString())) {
				return true;
			}

			if (!Objects.equals(coding.getVersion(), version)) {
				return true;
			}
		}

		return false;
	}

	@AssertFalse(message = "System and version inputs both specify a code system version.")
	private boolean isVersionAmbiguous() {

		// XXX: Only checking the presence of a (possible) version part in a SNOMED CT URI, not the consistency of the two inputs
		if (system != null) {
			final String systemValue = system.getValueAsString();
			if (systemValue.startsWith(SNOMED_BASE_URI) && systemValue.contains("/version/") && version != null) {
				return true;
			}
		}

		return false;
	}
}
