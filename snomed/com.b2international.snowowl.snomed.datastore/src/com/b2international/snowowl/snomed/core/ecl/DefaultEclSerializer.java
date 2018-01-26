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
package com.b2international.snowowl.snomed.core.ecl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.ISerializer;

import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.google.inject.Provider;

/**
 * @since 5.4
 */
public class DefaultEclSerializer implements EclSerializer {

	private final Provider<ISerializer> eclSerializer;

	public DefaultEclSerializer(Provider<ISerializer> eclSerializer) {
		this.eclSerializer = eclSerializer;
	}
	
	@Override
	public String serialize(ExpressionConstraint expression) {
		return eclSerializer.get().serialize(expression);
	}
	
	@Override
	public String serializeWithoutTerms(ExpressionConstraint expression) {
		removeTerms(expression);
		return eclSerializer.get().serialize(expression).trim();
	}

	private void removeTerms(EObject expression) {
		if (expression instanceof ConceptReference) {
			((ConceptReference) expression).setTerm(null);
		} else {
			for (EObject object : expression.eContents()) {
				removeTerms(object);
			}
		}
	}
}
