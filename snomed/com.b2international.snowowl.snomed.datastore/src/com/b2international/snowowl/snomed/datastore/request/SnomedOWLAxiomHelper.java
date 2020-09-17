/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.google.common.base.Strings;

/**
 * @since 6.14
 */
public final class SnomedOWLAxiomHelper {

	private static final String SUBCLASSOF = "subclassof";

	public static String getDefinitionStatusFromExpressions(Set<String> owlExpressions) {
		if (owlExpressions.isEmpty()) {
			return null;
		}
		
		return owlExpressions.stream()
				.filter(expression -> !Strings.isNullOrEmpty(expression))
				.filter(expression -> expression.toLowerCase(Locale.ENGLISH).contains(SUBCLASSOF))
				.findFirst()
				.map(equivalentClassesAxiom -> Concepts.PRIMITIVE)
				.orElse(Concepts.FULLY_DEFINED);
	}

}
