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
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.Objects;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * FHIR URI datatype
 * 
 * @since 6.4
 */
public final class Uri {
	
	public static final String SNOMED_BASE_URI_STRING = "http://snomed.info/sct"; //$NON-NLS-N$
	public static final Uri SNOMED_BASE_URI = new Uri(SNOMED_BASE_URI_STRING);
	
	@NotEmpty
	@ValidUri
	private String uriValue;

	public Uri(String uriValue) {
		this.uriValue = uriValue;
	}

	@JsonValue
	public String getUriValue() {
		return uriValue;
	}
	
	/**
	 * Returns true if this URI designates a SNOMED CT code system (starts with http://snomed.info/sct)
	 * @return
	 */
	public boolean isSnomedUri() {
		return uriValue !=null && uriValue.startsWith(SNOMED_BASE_URI_STRING);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uriValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Uri other = (Uri) obj;
		return Objects.equals(uriValue, other.uriValue);
	}

	@Override
	public String toString() {
		return "Uri [uriValue=" + uriValue + "]";
	}

}