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
package com.b2international.snowowl.fhir.core.model.dt;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

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
@ApiModel
@JsonInclude(Include.NON_NULL)
public class Coding {
	
	@Valid
	private Code code;
	
	@Valid
	private Uri system;
	
	private String version;
	
	@JsonProperty("userSelected")
	private boolean isUserSelected;
	
	private String display;

	//Jackson ObjectMapper
	@SuppressWarnings("unused")
	private Coding() {}
	
	/**
	 * @param code
	 * @param system
	 * @param version
	 * @param isUserSelected
	 * @param display
	 */
	Coding(Code code, Uri system, String version, boolean isUserSelected, String display) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.isUserSelected = isUserSelected;
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
	 * @return the isUserSelected
	 */
	public boolean isUserSelected() {
		return isUserSelected;
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
		result = prime * result + (isUserSelected ? 1231 : 1237);
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
		if (isUserSelected != other.isUserSelected)
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
	
	public static class Builder extends ValidatingBuilder<Coding> {
		
		private Code code;
		private Uri system;
		private String version;
		private boolean isUserSelected;
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

		
		public Builder isUserSelected(final boolean isUserSelected) {
			this.isUserSelected = isUserSelected;
			return this;
		}

		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		@Override
		protected Coding doBuild() {
			return new Coding(code, system, version, isUserSelected, display);
		}
		
	}
	
	@Override
	public String toString() {
		return "Coding [code=" + code + ", systemUri=" + system + ", version=" + version + ", isUserSelected=" + isUserSelected + ", display="
				+ display + "]";
	}

}
