/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.codesystem;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.2.0
 */
public interface FhirCodeSystemCUDSupport {

	/**
	 * Implementers need to create the specified codesystem in their tooling
	 * if it does not exist yet, update it otherwise
	 *  
	 * @param context - the context to use when looking for this code system
	 * @param codeSystem 
	 */
	public void updateOrCreateCodeSystem(ServiceProvider context, CodeSystem codeSystem);	
	
}
