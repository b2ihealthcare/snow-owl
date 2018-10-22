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
package com.b2international.snowowl.fhir.core.model.valueset;

import java.util.Date;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * This class represents a FHIR ValueSet$validate-code operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html#validate-code">FHIR:ValueSet:Operations:validate-code</a>
 * @since 6.9
 */
@JsonDeserialize(builder = ValidateCodeRequest.Builder.class)
@JsonPropertyOrder({"url", "context", "valueSet", "code", "system", "version", "display", "coding", "codeableConcept", "date",
	"abstract", "displayLanguage"})
public class ValidateCodeRequest {
	
	//Value set Canonical URL. The server must know the value set (e.g. it is defined explicitly in the server's value sets, or it is defined implicitly by some code system known to the server
	private final Uri url;
	
	private final Uri context;
	
	private final ValueSet valueSet;
	
	// The code that is to be validated. If a code is provided, a system must be provided (0..1)
	private final String code;

	// The system for the code that is to be validated (0..1)
	private final String system;
	
	// The code system version of the code to be validated (0..1)
	private final String version;
	
	private final String display;

	// The coding to look up (0..1)
	private final Coding coding;
	
	private final CodeableConcept codeableConcept;
	
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
	
	@JsonProperty("abstract")
	private final Boolean isAbstract;
	
	//The requested language for display (see ExpansionProfile.displayLanguage)
	private final String displayLanguage;
	
	ValidateCodeRequest(
			Uri url,
			Uri context,
			ValueSet valueSet,
			String code, 
			String system, 
			String version,
			String display,
			Coding coding,
			CodeableConcept codeableConcept,
			Date date, 
			Boolean isAbstract,
			String displayLanguage) {
		
		this.url = url;
		this.context = context;
		this.valueSet = valueSet;
		this.code = code;
		this.system = system;
		this.version = version;
		this.display = display;
		this.coding = coding;
		this.codeableConcept = codeableConcept;
		this.date = date;
		this.isAbstract = isAbstract;
		this.displayLanguage = displayLanguage;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public Uri getContext() {
		return context;
	}
	
	public ValueSet getValueSet() {
		return valueSet;
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
	
	public String getDisplay() {
		return display;
	}

	public Coding getCoding() {
		return coding;
	}
	
	public CodeableConcept getCodeableConcept() {
		return codeableConcept;
	}

	public Date getDate() {
		return date;
	}
	
	public Boolean getIsAbstract() {
		return isAbstract;
	}

	public String getDisplayLanguage() {
		return displayLanguage;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<ValidateCodeRequest> {

		private Uri url;
		private Uri context;
		private ValueSet valueSet;
		private String code;
		private String system;
		private String version;
		private String display;
		private Coding coding;
		private CodeableConcept codeableConcept;
		private Date date;
		private Boolean isAbstract;
		private String displayLanguage;
		
		Builder() {}
		
		public Builder url(final Uri url) {
			this.url = url;
			return this;
		}
		
		public Builder url(final String urlString) {
			this.url = new Uri(urlString);
			return this;
		}
		
		public Builder context(final Uri context) {
			this.context = context;
			return this;
		}
		
		public Builder valueSet(final ValueSet valueSet) {
			this.valueSet = valueSet;
			return this;
		}
		
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
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}

		public Builder coding(Coding coding) {
			this.coding = coding;
			return this;
		}
		
		public Builder codeableConcept(CodeableConcept codeableConcept) {
			this.codeableConcept = codeableConcept;
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
		
		public Builder isAbstract(Boolean isAbstract) {
			this.isAbstract = isAbstract;
			return this;
		}
		
		public Builder displayLanguage(final String displayLanguage) {
			this.displayLanguage = displayLanguage;
			return this;
		}

		@Override
		protected ValidateCodeRequest doBuild() {
			return new ValidateCodeRequest(url, context, valueSet, code, system, version, display, coding, codeableConcept, date, isAbstract, displayLanguage);
		}

	}
	
}
