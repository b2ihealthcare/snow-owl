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
package com.b2international.snowowl.fhir.core.model.usagecontext;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.b2international.snowowl.fhir.core.model.dt.Range;

/**
 * 
 * FHIR Range Usage Context
 * 
 * https://www.hl7.org/fhir/metadatatypes.html#UsageContext
 * 
 * It can be {@link CodeableConcept}, {@link Quantity} and {@link Range} 
 * @since 6.6
 */
public class RangeUsageContext extends UsageContext<Range> {

	protected RangeUsageContext(final String id, final Collection<Extension> extensions, final Coding code, final Range value) {
		super(id, extensions, code, value);
	}
	
	public Coding getCode() {
		return code;
	}
	
	@Override
	public String getType() {
		return "Range";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends UsageContext.Builder<Builder, RangeUsageContext, Range> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected RangeUsageContext doBuild() {
			return new RangeUsageContext(id, extensions, code, value);
		}
	}

}
