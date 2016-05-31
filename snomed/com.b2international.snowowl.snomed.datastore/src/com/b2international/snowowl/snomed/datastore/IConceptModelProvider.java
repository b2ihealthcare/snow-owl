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
package com.b2international.snowowl.snomed.datastore;

import java.util.Set;

import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;

/**
 * Interface for {@link ConceptModel concept model} providers.
 * 
 */
public interface IConceptModelProvider {
	
	/**
	 * Returns an immutable set of both active and inactive constraints, where the specified concept is part of the domain.
	 * 
	 * @param conceptModel the concept model containing the attribute constraints.
	 * @param conceptId the SNOMED&nbsp;CT identifier of the concept.
	 * @param terminologyBrowser the {@link SnomedTerminologyBrowser} to use.
	 * @return a set of constraints associated with the SNOMED&nbsp;CT concept identified by the specified unique ID.
	 */
	public Set<ConstraintBase> getAllConstraints(final ConceptModel conceptModel, final String conceptId, final SnomedClientTerminologyBrowser terminologyBrowser);
	
	/**
	 * Returns an immutable set of <em>active</em> constraints, where the specified concept is part of the domain.
	 * 
	 * @param conceptModel the concept model containing the attribute constraints.
	 * @param conceptId the SNOMED&nbsp;CT identifier of the concept.
	 * @param terminologyBrowser the {@link SnomedTerminologyBrowser} to use.
	 * @param refSetBrowser reference set browser to use.
	 * @return a set of constraints associated with the SNOMED&nbsp;CT concept identified by the specified unique ID for the validation process.
	 */
	public Set<ConstraintBase> getConstraintsForValidation(final ConceptModel conceptModel, final String conceptId, final SnomedClientTerminologyBrowser terminologyBrowser);
}