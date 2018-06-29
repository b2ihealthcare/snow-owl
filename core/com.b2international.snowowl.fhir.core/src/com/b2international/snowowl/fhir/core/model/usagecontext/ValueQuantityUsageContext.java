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
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;

/**
 * 
 * FHIR Value Quantity Usage Context
 * 
 * https://www.hl7.org/fhir/metadatatypes.html#UsageContext
 * @since 6.6
 */
public class ValueQuantityUsageContext extends UsageContext<Quantity> {

	protected ValueQuantityUsageContext(final String id, final Collection<Extension> extensions, final Coding code, final Quantity value) {
		super(id, extensions, code, value);
	}
	
	@Override
	public Coding getCode() {
		return code;
	}
	
	@Override
	public String getType() {
		return "Quantity";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends UsageContext.Builder<Builder, ValueQuantityUsageContext, Quantity> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected ValueQuantityUsageContext doBuild() {
			return new ValueQuantityUsageContext(id, extensions, code, value);
		}
	}

}
