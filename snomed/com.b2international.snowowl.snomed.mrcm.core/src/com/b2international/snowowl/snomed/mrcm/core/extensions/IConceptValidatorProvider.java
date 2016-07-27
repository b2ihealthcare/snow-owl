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
package com.b2international.snowowl.snomed.mrcm.core.extensions;

import java.util.Collection;

/**
 */
public interface IConceptValidatorProvider {

	/**
	 * Returns the registered validation rules for the specified concept.
	 * 
	 * @param branch
	 * @param conceptId the concept to collect validators for (may not be {@code null})
	 * @return a collection of {@link IConceptValidator}s relevant to the given concept, or an empty collection if none
	 * of the available validators are applicable
	 */
	public Collection<IConceptValidator> getValidators(final String branch, final String conceptId);
}