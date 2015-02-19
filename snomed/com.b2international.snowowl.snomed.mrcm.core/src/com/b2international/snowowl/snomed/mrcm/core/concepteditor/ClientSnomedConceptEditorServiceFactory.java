/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.mrcm.core.concepteditor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.Concept;

/**
 * {@link IClientSnomedConceptEditorService Client-side SNOMED concept service} factory.
 * 
 */
public class ClientSnomedConceptEditorServiceFactory {

	/**
	 * Creates a client-side SNOMED concept editor service for the specified concept.
	 * <p>
	 * Depending on the passed in concept's parent view state, either a server-side index service instance, 
	 * or a CDO-specific instance will be returned, which takes changes into account which are possibly 
	 * not persisted on the server yet.
	 * 
	 * @param concept the concept to edit (may not be {@code null})
	 * @return the editor service to use for retrieving concept detail beans
	 */
	public IClientSnomedConceptEditorService create(final Concept concept) {
		if (concept.cdoView().isDirty()) {
			return new CDOClientSnomedConceptEditorService(concept);
		} else {
			return ApplicationContext.getInstance().getService(IClientSnomedConceptEditorService.class);
		}
	}
}