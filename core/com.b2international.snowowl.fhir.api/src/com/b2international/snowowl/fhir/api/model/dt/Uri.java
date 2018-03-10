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
package com.b2international.snowowl.fhir.api.model.dt;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * FHIR URI datatype
 * 
 * @since 6.3
 */
@JsonSerialize(using = ToStringSerializer.class)
public class Uri implements JsonStringProvider {
	
	@NotEmpty
	private String uriValue;

	public Uri(String uriValue) {
		this.uriValue = uriValue;
	}

	public String getUriValue() {
		return uriValue;
	}

	@Override
	public String toJsonString() {
		return uriValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uriValue == null) ? 0 : uriValue.hashCode());
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
		Uri other = (Uri) obj;
		if (uriValue == null) {
			if (other.uriValue != null)
				return false;
		} else if (!uriValue.equals(other.uriValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Uri [uriValue=" + uriValue + "]";
	}

}

