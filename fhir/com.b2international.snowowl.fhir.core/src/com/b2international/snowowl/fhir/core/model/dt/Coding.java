/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.dt;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FHIR Coding complex datatype.
 * 
 * Note that this class is not turned into a collection of parameters but kept in its
 * normal serialization form.
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/datatypes.html#Coding">FHIR:Datatypes:Coding</a>
 * 
 * @since 6.3
 */
@Schema
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Coding.Builder.class)
public class Coding {
	
	public static final Coding CODING_SUBSETTED = Coding.builder()
			.system("http://hl7.org/fhir/v3/ObservationValue")
			.code("SUBSETTED")
			.display("As requested, resource is not fully detailed.")
			.build();
	
	@Valid
	private Code code;
	
	@Valid
	private Uri system;
	
	private String version;
	
	private Boolean userSelected;
	
	private String display;

	/**
	 * @param code
	 * @param system
	 * @param version
	 * @param userSelected
	 * @param display
	 */
	@JsonCreator
	Coding(
		@JsonProperty("code") Code code, 
		@JsonProperty("system") Uri system, 
		@JsonProperty("version") String version, 
		@JsonProperty("userSelected") Boolean userSelected, 
		@JsonProperty("display") String display) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.userSelected = userSelected;
		this.display = display;
	}

	/**
	 * @return the code
	 */
	public Code getCode() {
		return code;
	}
	
	/**
	 * @return the code value as a string
	 */
	@JsonIgnore
	public String getCodeValue() {
		return code.getCodeValue();
	}

	/**
	 * @return the system
	 */
	public Uri getSystem() {
		return system;
	}
	
	@JsonIgnore
	public String getSystemValue() {
		if (system == null) { 
			return null;
		} else {
			return system.getUriValue();
		}
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the userSelected
	 */
	public Boolean isUserSelected() {
		return userSelected;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((userSelected == null) ? 0 : userSelected.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coding other = (Coding) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		if (userSelected == null) {
			if (other.userSelected != null)
				return false;
		} else if (!userSelected.equals(other.userSelected))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	@AssertTrue(message = "SNOMED CT version is defined as part of the system URI")
	private boolean isVersionValid() {
		
		if (version != null && system != null && system.isSnomedUri()) {
			return false;
		}
		return true;
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Coding> {
		
		private Code code;
		private Uri system;
		private String version;
		private Boolean userSelected;
		private String display;

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

		public Builder userSelected(final boolean userSelected) {
			this.userSelected = userSelected;
			return this;
		}

		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		@Override
		public Coding build() {
			return super.build();
		}
		
		@Override
		protected Coding doBuild() {
			return new Coding(code, system, version, userSelected, display);
		}
		
	}
	
	@Override
	public String toString() {
		return "Coding [code=" + code + ", systemUri=" + system + ", version=" + version + ", userSelected=" + userSelected + ", display="
				+ display + "]";
	}

}
