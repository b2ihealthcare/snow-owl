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
package com.b2international.snowowl.fhir.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.model.CodeSystem;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * FHIR provider base class.
 * 
 * @since 6.3
 */
public abstract class FhirProvider {
	
	protected IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
	
	/**
	 * Returns the FHIR code systems available for the given repository
	 * @param repositoryId
	 * @return collection of {@link CodeSystem}
	 */
	protected Collection<CodeSystem> getCodeSystems(String repositoryId) {
		
		Promise<CodeSystems> codeSystemsPromise = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.execute(eventBus);
			
			return codeSystemsPromise.then(cs -> {
				List<CodeSystem> codeSystems = cs.stream()
					.map(this::createCodeSystem)
					.collect(Collectors.toList());
				
				return codeSystems;
			}).getSync();
	}
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a Snow Owl {@link CodeSystemEntry}
	 * @param codeSystemEntry
	 * @return
	 */
	protected abstract CodeSystem createCodeSystem(final CodeSystemEntry codeSystemEntry);

}
