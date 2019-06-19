/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;

/**
 * @since 6.14
 */
public final class SnomedOWLAxiomHelper {

	private static final String EQUIVALENTCLASSES = "equivalentclasses";

	public static DefinitionStatus getDefinitionStatusFromExpressions(Set<String> owlExpressions) {
		
		if (owlExpressions.isEmpty()) {
			return null;
		}
		
		Optional<String> equivalenClassesExpression = owlExpressions.stream()
			.filter(expression -> expression.toLowerCase(Locale.ENGLISH).contains(EQUIVALENTCLASSES))
			.findFirst();
		
		if (equivalenClassesExpression.isPresent()) {
			return DefinitionStatus.FULLY_DEFINED;
		}
		
		return DefinitionStatus.PRIMITIVE;

	}

}
