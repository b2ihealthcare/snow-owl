/*
 * Copyright 2012-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;

/**
 * @since 2.0
 */
public enum SnomedConstraintPredicateType {
	DATATYPE,
	DEPENDENCY,
	DESCRIPTION,
	RELATIONSHIP;

	public static SnomedConstraintPredicateType valueOf(final ConceptModelPredicate predicate) {
		checkNotNull(predicate, "Predicate instance may not be null.");
		
		if (predicate instanceof ConcreteDomainElementPredicate) {
			return DATATYPE;
		} else if (predicate instanceof DependencyPredicate) {
			return DEPENDENCY;
		} else if (predicate instanceof DescriptionPredicate) {
			return DESCRIPTION;
		} else if (predicate instanceof RelationshipPredicate) {
			return RELATIONSHIP;
		} else {
			throw new IllegalArgumentException("Unexpected concept model predicate class '" + predicate.getClass().getSimpleName() + "'.");
		}
	}
}
