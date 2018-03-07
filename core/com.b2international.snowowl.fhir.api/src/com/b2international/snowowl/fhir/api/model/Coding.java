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

import java.net.URI;
import java.net.URISyntaxException;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * Class to represent the FHIR Coding
 * @see <a href="https://www.hl7.org/fhir/2016May/datatypes.html#Coding">FHIR:Datatypes:Coding</a>
 * 
 * @since 6.3
 */
@ApiModel
@JsonInclude(Include.NON_NULL)
public class Coding {
	
	public static final String CODE_REGEXP = "[^\\s]+([\\s]?[^\\s]+)*"; //$NON-NLS-N$
	
	@NotEmpty
	private String code;
	
	@NotEmpty
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
	public Coding(String code, String system, String version, boolean isUserSelected, String display) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.isUserSelected = isUserSelected;
		this.display = display;
	}
	
	/**
	 * @param code
	 * @param system
	 * @param version
	 * @param isUserSelected
	 * @param display
	 */
	public Coding(String code, String system, String version) {
		this.code = code;
		this.system = system;
		this.version = version;
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
	
	public void validate() {
		if (StringUtils.isEmpty(code)) {
			throw new BadRequestException("Code is not specified.");
		}
		
		if (StringUtils.isEmpty(system)) {
			throw new BadRequestException("Code system is not specified.");
		}
		
		if (!code.matches(CODE_REGEXP)) {
			throw new BadRequestException("Code format is incorrect: " + code);
		}
		
		try {
			new URI(system);
		} catch (URISyntaxException e) {
			throw new BadRequestException("URI format is incorrect: " + system);
		}
		
	}
	
	@Override
	public String toString() {
		return "Coding [code=" + code + ", systemUri=" + system + ", version=" + version + ", isUserSelected=" + isUserSelected + ", display="
				+ display + "]";
	}

}
