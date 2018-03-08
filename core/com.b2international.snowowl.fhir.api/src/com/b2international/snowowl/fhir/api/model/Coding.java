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

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * Class to represent the FHIR Coding.
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
	
	@NotEmpty
	@Pattern(regexp = ValidationConstraints.CODE_REGEXP)
	private String code;
	
	@NotEmpty
	@Uri
	private String system;
	
	private String version;
	
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
	Coding(String code, String system, String version, boolean isUserSelected, String display) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.isUserSelected = isUserSelected;
		this.display = display;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the system
	 */
	public String getSystem() {
		return system;
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
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ModelValidator<Coding> {
		
		private String code;
		private String system;
		private String version;
		private boolean isUserSelected;
		private String display;

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

		
		public Builder isUserSelected(final boolean isUserSelected) {
			this.isUserSelected = isUserSelected;
			return this;
		}

		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
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
