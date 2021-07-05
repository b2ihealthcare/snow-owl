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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.Date;

import javax.validation.constraints.AssertTrue;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.serialization.FhirSerializedName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * This class represents a FHIR validate-code operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#validate-code">FHIR:CodeSystem:Operations:validate-code</a>
 * @since 7.17.0
 */
@JsonDeserialize(builder = ValidateCodeRequest.Builder.class)
@JsonPropertyOrder({ "url", "codeSystem", "code", "version", "display", "coding", "codeableConcept", "date", "isAbstract", "displayLanguage" })
public class ValidateCodeRequest {

	// CodeSystem URL to validate against (0..1)
	private final Uri url;
	
	// The codeSystem is provided directly as part of the request. (0..1) - not supported
	private final CodeSystem codeSystem;
	
	// The code to be validated (0..1)
	private final Code code;
	
	// The version of the code system (0..1)
	private final String version;
	
	// The display associated with the code, if provided (0..1)
	private final String display;
	
	// A coding to validate. The system must match the specified code system (0..1)
	private final Coding coding;
	
	// A full codeableConcept to validate (0..1)
	//The server returns true if one of the coding values is in the code system, and may also validate
	//that the codings are not in conflict with each other if more than one is present.
	private final CodeableConcept codeableConcept;
	
	// The date for which the validation should be checked (0..1)
	private final Date date;
	
	// 'Grouper' codes are abstract (0..1)
	@FhirSerializedName("abstract")
	private final Boolean isAbstract;
	
	private final Code displayLanguage;

	ValidateCodeRequest(Uri url, CodeSystem codeSystem, Code code, String version, String display, Coding coding, 
			CodeableConcept codeableConcept, Date date, Boolean isAbstract, Code displayLanguage) {
		
		this.url = url;
		this.codeSystem = codeSystem;
		this.code = code;
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
	
	public CodeSystem getCodeSystem() {
		return codeSystem;
	}

	public String getCode() {
		if (code != null) {
			return code.getCodeValue();
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
	
	public Boolean getAbstract() {
		return isAbstract;
	}
	
	public Code getDisplayLanguage() {
		return displayLanguage;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public void validate() {
		if (getCodeSystem() != null) {
			throw new BadRequestException("Validation against external code systems is not supported", "ValidateCodeRequest.codeSystem");
		}
		
		//If the code is specified, the code system needs to be specified as well
		if (getCode() != null && getUrl() == null) {
			throw new BadRequestException(String.format("Parameter 'url' is not specified for code '%s'.", getCode()), "ValidateCodeRequest.url");
		}
	}
	
	@AssertTrue(message = "Code is missing while display is provided")
	private boolean isCodeProvidedWithDisplay() {
		if (code == null && display != null) {
			return false;
		}
		return true;
	}
	
	@AssertTrue(message = "No code is provided to validate")
	private boolean isCodeMissing() {
		if (code == null && coding == null && codeableConcept == null) {
			return false;
		}
		return true;
	}
	
//	@AssertTrue(message = "System is missing for provided code")
//	private boolean isSystemMissing() {
//
//		if (url == null && code != null) {
//			return false;
//		}
//
//		if (coding != null && coding.getSystem() == null) {
//			return false;
//		}
//		
//		return true;
//	}
	
	@AssertTrue(message = "System URL and Coding.system are different")
	private boolean isSystemsDifferent() {

		if (coding != null && url != null) {
			if (coding.getSystem() != null && !coding.getSystem().equals(url)) {
				return false;
			}
		}
		return true;
	}
	
	@AssertTrue(message = "System URL and a Coding.system in Codeable are different")
	private boolean isInvalidCodeableSystem() {

		if (codeableConcept != null && url != null) {
			if (codeableConcept.getCodings() != null) {
				return !codeableConcept.getCodings().stream().anyMatch(c -> !c.getSystem().equals(url));
			}
		}
		return true;
	}
	
	@AssertTrue(message = "Either code, coding or codeable can be defined at a time")
	private boolean isTooManyCodesDefined() {

		if (coding != null && code != null) {
			return false;
		}
		
		if (coding != null && codeableConcept != null) {
			return false;
		}
		
		if (code != null && codeableConcept != null) {
			return false;
		}
		return true;
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder extends ValidatingBuilder<ValidateCodeRequest> {

		private Uri url;
		private CodeSystem codeSystem;
		private Code code;
		private String version;
		private String display;
		private Coding coding;
		private CodeableConcept codeableConcept;
		private Date date;
		private Boolean isAbstract;
		private Code displayLanguage;

		Builder() {
		}
		
		public Builder url(final String uriString) {
			this.url = new Uri(uriString);
			return this;
		}
		
		public Builder code(final String code) {
			this.code = new Code(code);
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
		
		@JsonProperty("abstract")
		public Builder isAbstract(Boolean isAbstract) {
			this.isAbstract = isAbstract;
			return this;
		}
		
		public Builder displayLanguage(Code displayLanguage) {
			this.displayLanguage = displayLanguage;
			return this;
		}


		@Override
		protected ValidateCodeRequest doBuild() {
			return new ValidateCodeRequest(url, codeSystem, code, version, display, coding, codeableConcept, date, isAbstract, displayLanguage);
		}

	}
}
