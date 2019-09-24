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

import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Quantity complex datatype
 * 
 * The unit and code/system elements of the low or high elements SHALL match.
 * If the low or high elements are missing, the meaning is that the low or high boundaries 
 * are not known and therefore neither is the complete range.
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#quantity">FHIR:Data Types:Quantity</a>
 * @since 6.6
 */
public class Quantity extends BaseQuantity {
	
	Quantity(String id, Collection<Extension> extensions, Double value, Code comparator, String unit, Uri system, Code code) {
		super(id, extensions, value, comparator, unit, system, code);
	}
	
	/*
	 * To enable to override
	 * @return
	 */
	@JsonProperty
	@Override
	public Code getComparator() {
		return comparator;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends BaseQuantity.Builder<Builder, Quantity> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
	
		@Override
		protected Quantity doBuild() {
			return new Quantity(id, extensions, value, comparator, unit, system, code);
		}
	}

}
