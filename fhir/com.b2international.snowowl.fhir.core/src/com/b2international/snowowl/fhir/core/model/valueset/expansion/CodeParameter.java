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
package com.b2international.snowowl.fhir.core.model.valueset.expansion;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;

/**
 * Code expansion parameter
 * @since 6.7
 */
public class CodeParameter extends Parameter<Code> {

	CodeParameter(String name, Code value) {
		super(name, value);
	}
	
	@Override
	public FhirDataType getType() {
		return FhirDataType.CODE;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Parameter.Builder<Builder, CodeParameter, Code> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected CodeParameter doBuild() {
			return new CodeParameter(name, value);
		}
	}

}
