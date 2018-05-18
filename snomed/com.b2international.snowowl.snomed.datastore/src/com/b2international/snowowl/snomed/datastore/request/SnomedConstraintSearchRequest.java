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
package com.b2international.snowowl.snomed.datastore.request;

import static com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument.Expressions.descendantIds;
import static com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument.Expressions.refSetIds;
import static com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument.Expressions.relationshipKeys;
import static com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument.Expressions.selfIds;
import static com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument.Expressions.predicateTypes;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintPredicateType;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;

/**
 * @since 4.7
 */
final class SnomedConstraintSearchRequest extends SearchIndexResourceRequest<BranchContext, SnomedConstraints, SnomedConstraintDocument> {

	public enum OptionKey {
		
		/**
		 * Match MRCM constraints that are applicable to concepts having the given
		 * identifiers.
		 */
		SELF,
		
		/**
		 * Match MRCM constraints that are applicable to concepts that are descendants
		 * of other concepts with the given identifiers.
		 */
		DESCENDANT,
		
		/**
		 * Match MRCM constraints that are applicable to concepts that are members of 
		 * reference sets with the specified identifiers.
		 */
		REFSET, 
		
		/**
		 * Match MRCM constraints that are applicable to concepts that have a relationship
		 * with the specified type and destination identifiers.
		 */
		RELATIONSHIP,
		
		/**
		 * Match MRCM constraints that has any of the given {@link PredicateType}.
		 */
		TYPE
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, SnomedConstraintDocument.Expressions::ids);
		
		if (containsKey(OptionKey.SELF)) {
			queryBuilder.filter(selfIds(getCollection(OptionKey.SELF, String.class)));
		}
		
		if (containsKey(OptionKey.DESCENDANT)) {
			queryBuilder.filter(descendantIds(getCollection(OptionKey.DESCENDANT, String.class)));
		}

		if (containsKey(OptionKey.REFSET)) {
			queryBuilder.filter(refSetIds(getCollection(OptionKey.REFSET, String.class)));
		}
		
		if (containsKey(OptionKey.RELATIONSHIP)) {
			queryBuilder.filter(relationshipKeys(getCollection(OptionKey.RELATIONSHIP, String.class)));
		}
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.filter(predicateTypes(getCollection(OptionKey.TYPE, SnomedConstraintPredicateType.class)));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected Class<SnomedConstraintDocument> getDocumentType() {
		return SnomedConstraintDocument.class;
	}
	
	@Override
	protected SnomedConstraints toCollectionResource(BranchContext context, Hits<SnomedConstraintDocument> hits) {
		return SnomedConverters.newConstraintConverter(context, expand(), locales()).convert(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), limit(), hits.getTotal());
	}
	
	@Override
	protected SnomedConstraints createEmptyResult(int limit) {
		return new SnomedConstraints(limit, 0);
	}

}
