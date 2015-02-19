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
package com.b2international.snowowl.core.validation;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProvider;

/**
 * Describes the results of running a global constraint.
 *
 */
public class GlobalConstraintStatus {
	private final String constraintId;
	private final List<IdAndTerminologyComponentIdProvider> violatingComponents;
	private final List<IdAndTerminologyComponentIdProvider> complyingComponents;
	
	public GlobalConstraintStatus(final String constraintId, 
			final Iterable<? extends IdAndTerminologyComponentIdProvider> violatingComponents, 
			final Iterable<? extends IdAndTerminologyComponentIdProvider> complyingComponents) {
		
		this.constraintId = constraintId;
		this.violatingComponents = unmodifiableList(newArrayList(violatingComponents));
		this.complyingComponents = unmodifiableList(newArrayList(complyingComponents));
	}

	public String getConstraintId() {
		return constraintId;
	}

	public List<IdAndTerminologyComponentIdProvider> getViolatingComponents() {
		return violatingComponents;
	}
	
	public List<IdAndTerminologyComponentIdProvider> getComplyingComponents() {
		return complyingComponents;
	} 
}