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

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.google.common.collect.Iterables.getFirst;

import java.util.Collection;

import org.eclipse.core.expressions.PropertyTester;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * Property tester that checks the ID of a particular concept.
 *
 */
public class ConceptIdPropertyTester extends PropertyTester {

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		if (receiver instanceof Collection) {
			if (!isEmpty(receiver)) {
				final Object object = getFirst(((Collection<?>) receiver), null);
				if (object instanceof IComponent<?>) {
					return Concepts.GENERATED_SINGAPORE_MEDICINAL_PRODUCT.equals(((IComponent<?>) object).getId());
				}
			}
		}
		return false;
	}

}