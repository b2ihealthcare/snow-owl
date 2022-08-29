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

import java.time.LocalDate;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * Implementing classes provide support for code system interactions and named
 * operations which can not be achieved using tooling-independent building
 * blocks.
 * <p>
 * <b>Implementation note</b>: As at the moment only a single tooling has such
 * functionality, the implementing class is registered directly as a service on
 * the top-level {@link ServiceProvider} (ie. {@link Environment}).
 * <p>
 * For other resource types (value sets and concept maps) where multiple
 * implementations can coexist, the corresponding {@link Repository} should have
 * a registered service entry (and so the retrieved service reference will be
 * different depending on which {@link RepositoryContext} is opened for the
 * request).
 * 
 * @since 8.2.0
 */
public interface FhirCodeSystemOperations {

	/**
	 * Creates a new code system based on the specified input, or updates an
	 * existing one if it can be retrieved by ID.
	 * 
	 * @param context - the request context to use for creation/update
	 * @param fhirCodeSystem - the input FHIR representation of the code system
	 * @param owner - the commit author and resource owner, usually provided 
	 * via a request header (can be different from the user associated with the 
	 * service provider)
	 * @param ownerProfileName - the owner's display name, stored in resource settings
	 * @param defaultEffectiveDate - the default effective date to use if no date
	 * information is present on the resource (when not given and a version is present,
	 * but no effective time is recorded on the code system, an exception will be thrown)
	 * @param bundleId - the parent bundle identifier
	 */
	public void update(
		ServiceProvider context, 
		CodeSystem fhirCodeSystem, 
		String owner, 
		String ownerProfileName,
		LocalDate defaultEffectiveDate,
		String bundleId);	
}
