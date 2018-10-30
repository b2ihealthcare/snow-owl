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

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * FHIR Code datatype
 * 
 * @since 6.3
 */
@JsonPropertyOrder({"codeValue"})
public class Code {
	
	private static final String CODE_REGEXP = "[^\\s]+([\\s]?[^\\s]+)*"; //$NON-NLS-N$
	
	//When serialized into parameters, code will be: {"name":"codeValue","valueString":"value"}
	@FhirType(FhirDataType.CODE)
	@Pattern(regexp = CODE_REGEXP) //not empty is included
	@JsonProperty("codeValue")
	private String codeValue;

	//For Jackson
	@SuppressWarnings("unused")
	private Code() {}
	
	public Code(String codeValue) {
		this.codeValue = codeValue;
	}

	@JsonValue
	public String getCodeValue() {
		return codeValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeValue == null) ? 0 : codeValue.hashCode());
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
		Code other = (Code) obj;
		if (codeValue == null) {
			if (other.codeValue != null)
				return false;
		} else if (!codeValue.equals(other.codeValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Code [codeValue=" + codeValue + "]";
	}

}
