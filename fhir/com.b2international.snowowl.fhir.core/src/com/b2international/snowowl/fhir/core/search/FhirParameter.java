/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.FhirRequestParameterType;

/**
 * FHIR URI request parameter
 * <br>
 * parameter[:modifier]=[prefix]value
 * <br>
 * parameter[:modifier]=[prefix]system|code for tokens
 * 
 * <br>
 * where the modifiers are:<ul>
 * 		<li>missing
 * 		<li>exact
 * 		<li>contains
 * 		<li>text
 * 		<li>in
 * 		<li>below
 * 		<li>above
 * 		<li>not-in
 * 		<li>type
 * </ul>		
 * where the value types are:<ul>
 * 		<li>number (missing)
 * 		<li>date (missing)
 * 		<li>string (missing, exact, contains)
 * 		<li>token (missing, text, in, below, above, not-in)
 * 		<li>reference (missing, type)
 * 		<li>composite (missing)
 * 		<li>quantity (missing)
 * 		<li>uri (missing, below, above)
 * </ul>
 * prefixes are:<ul>
 * 		<li>eq - equal (default if no prefix is present)
 * 		<li>ne
 * 		<li>gt
 * 		<li>lt
 * 		<li>ge
 * 		<li>le
 * 		<li>sa
 * 		<li>eb
 * 		<li>ap
 * 
 * https://www.hl7.org/fhir/searchparameter-registry.html
 * @since 7.14
 */
public abstract class FhirParameter {
	
	protected FhirUriParameterDefinition parameterDefinition;
	
	protected Collection<String> values;
	
	public FhirParameter(final FhirUriParameterDefinition parameterDefinition, Collection<String> values) {
		this.parameterDefinition = parameterDefinition;
		this.values = values;
	}

	public String getName() {
		return parameterDefinition.getName();
	}
	
	public FhirRequestParameterType getType() {
		return parameterDefinition.getType();
	}
	
	public Collection<String> getValues() {
		return values;
	}
	
	public FhirUriParameterDefinition getParameterDefinition() {
		return parameterDefinition;
	}

	/**
	 * Validate this FHIR URI parameter
	 */
	public abstract void validate();

}
