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

/**
 * Client-side interface for SNOMED CT concept editor related services.
 * @deprecated - reimplement concept widget model related services
 */
public interface IClientSnomedConceptEditorService {
	
	/**
	 * Creates a populated {@link SnomedConceptDetailsBean} instance for the specified concept, using the currently active branch
	 * for looking up data.
	 * 
	 * @param conceptId the identifier of the edited concept 
	 * @param includeUnsanctioned {@code true} if "unsanctioned" widget beans and model elements (not derived from an MRCM
	 * rule) should be included in the details bean, {@code false} otherwise
	 * @return a {@code SnomedConceptDetailsBean} instance carrying required data for display in an editor
	 */
	SnomedConceptDetailsBean getConceptDetailsBean(long conceptId, boolean includeUnsanctioned);

}