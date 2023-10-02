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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * FHIR Id datatype
 * 
 * @since 6.3
 */
public class Id {
	
	private static final String REGEXP = "[A-Za-z0-9\\-\\.]{1,64}"; //$NON-NLS-N$
	
	@Pattern(regexp = REGEXP) //not empty is included
	private String idValue;

	public Id(String idValue) {
		this.idValue = idValue;
	}

	@JsonValue
	public String getIdValue() {
		return idValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idValue == null) ? 0 : idValue.hashCode());
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
		Id other = (Id) obj;
		if (idValue == null) {
			if (other.idValue != null)
				return false;
		} else if (!idValue.equals(other.idValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Id [idalue=" + idValue + "]";
	}

}
