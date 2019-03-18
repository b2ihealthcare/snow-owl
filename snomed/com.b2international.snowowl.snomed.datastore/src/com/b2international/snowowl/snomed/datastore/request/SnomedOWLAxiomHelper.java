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
import java.util.Set;
import java.util.StringTokenizer;

import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

/**
 * @since 5.16.0
 */
public final class SnomedOWLAxiomHelper {

	private static final String EQUIVALENTCLASSES = "equivalentclasses";

	public static DefinitionStatus getDefinitionStatusFromExpressions(Set<String> owlExpressions) {
		// XXX: Always look for and prefer equivalentClasses, it means the concept is
		// FULLY_DEFINED otherwise PRIMITIVE
		// Tokenize expressions on "(:"
			// Check if equivalentclasses follows up with valid SCT ID
		for (String owlExpression : owlExpressions) {
			final StringTokenizer tokenizer = new StringTokenizer(owlExpression.toLowerCase(Locale.ENGLISH), "(:");
			final String firstToken = tokenizer.nextToken();
			if (firstToken.equals(EQUIVALENTCLASSES)) {
				final String conceptId = tokenizer.nextToken().trim();

				if (SnomedIdentifiers.isConceptIdentifier(conceptId)) {
					return DefinitionStatus.FULLY_DEFINED;
				}
			}
		}

		return DefinitionStatus.PRIMITIVE;
	}

}
