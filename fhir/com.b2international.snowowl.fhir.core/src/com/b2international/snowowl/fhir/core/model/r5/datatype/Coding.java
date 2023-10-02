/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5.datatype;

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.base.DataType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;

/**
 * A Coding is a representation of a defined concept using a symbol from a defined "code system".
 * 
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#coding">2.1.28.0.4 Coding</a>
 * @since 9.0
 */
public class Coding extends DataType {

	/** Identity of the terminology system */
	@Summary
	private UriType system;
	
	/** Version of the system - if relevant */
	@Summary
	private String version;
	
	/** Symbol in syntax defined by the system */
	@Summary
	private CodeType code;
	
	/** Representation defined by the system */
	@Summary
	private String display;
	
	/**
	 * Was this coding chosen directly by the user, or did the system infer it via
	 * rules or processing?
	 */
	@Summary
	private Boolean userSelected;

	public UriType getSystem() {
		return system;
	}

	public void setSystem(UriType system) {
		this.system = system;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public CodeType getCode() {
		return code;
	}

	public void setCode(CodeType code) {
		this.code = code;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Boolean getUserSelected() {
		return userSelected;
	}

	public void setUserSelected(Boolean userSelected) {
		this.userSelected = userSelected;
	}
}
