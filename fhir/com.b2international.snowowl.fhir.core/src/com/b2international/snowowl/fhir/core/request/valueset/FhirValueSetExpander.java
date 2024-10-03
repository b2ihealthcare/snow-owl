/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.request.valueset;

import org.hl7.fhir.r5.model.ValueSet;

import com.b2international.fhir.r5.operations.ValueSetExpandParameters;
import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 8.0
 */
@FunctionalInterface
public interface FhirValueSetExpander {

	FhirValueSetExpander NOOP = (context, valueSet, request) -> valueSet;
	
	/**
	 * An URL to use to access/set the after value from an expanded ValueSet resource response. 
	 */
	String EXTENSION_AFTER_PROPERTY_URL = "https://b2ihealthcare.com/fhir/StructureDefinition/valueset-expand-after";
	
	/**
	 * Expands a FHIR {@link ValueSet}'s compose definition into a list of member codes and terms (aka concepts) and returns the expanded {@link ValueSet}.
	 * 
	 * @param context
	 * @param valueSet
	 * @param parameters
	 * @return the expanded {@link ValueSet}, never <code>null</code>.
	 */
	ValueSet expand(ServiceProvider context, ValueSet valueSet, ValueSetExpandParameters parameters);
	
}
