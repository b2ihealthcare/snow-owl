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

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Range complex datatype
 * 
 * The unit and code/system elements of the low or high elements SHALL match.
 * If the low or high elements are missing, the meaning is that the low or high boundaries 
 * are not known and therefore neither is the complete range.
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#range">FHIR:Data Types:Range</a>
 * @since 6.6
 */
public class Range extends Element {
	
	@JsonProperty
	private SimpleQuantity low;
	
	@JsonProperty
	private SimpleQuantity high;
	
	public Range(final SimpleQuantity low, final SimpleQuantity high) {
		this(low, high, null, null);
	}
	
	public Range(final SimpleQuantity low, final SimpleQuantity high, final String id, final Collection<Extension> extensions) {
		super(id, extensions);
		this.low = low;
		this.high = high;
	}

	public SimpleQuantity getLow() {
		return low;
	}
	
	public SimpleQuantity getHigh() {
		return high;
	}

}
