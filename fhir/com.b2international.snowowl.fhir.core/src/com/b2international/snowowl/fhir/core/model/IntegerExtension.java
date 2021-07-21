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
package com.b2international.snowowl.fhir.core.model;

import com.b2international.snowowl.fhir.core.codesystems.ExtensionType;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * 
 * FHIR Integer Extension
 * 
 * @see <a href="https://www.hl7.org/fhir/extensibility.html#Extension">FHIR:Foundation:Extensibility</a>
 * @since 6.3
 */
public class IntegerExtension extends Extension<Integer> {
	
	public IntegerExtension(final Uri url, final Integer value) {
		super(url, value);
	}

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.INTEGER;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Extension.Builder<Builder, IntegerExtension, Integer> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected IntegerExtension doBuild() {
			return new IntegerExtension(url, value);
		}
	}

}
