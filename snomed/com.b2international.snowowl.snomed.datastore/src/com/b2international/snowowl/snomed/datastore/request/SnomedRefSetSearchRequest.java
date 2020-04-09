/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.mapTargetComponentTypes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.refSetTypes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.referencedComponentTypes;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * @since 4.5
 */
final class SnomedRefSetSearchRequest extends SnomedSearchRequest<SnomedReferenceSets, SnomedConceptDocument> {

	enum OptionKey {
		/**
		 * A collection of {@link SnomedRefSetType reference set types} to use
		 */
		TYPE,
		
		/**
		 * A collection of referenced component types to use
		 */
		REFERENCED_COMPONENT_TYPE,
		
		/**
		 * A collection of map target component types, only applicable for maps
		 */
		MAP_TARGET_COMPONENT_TYPE
	};

	@Override
	protected Class<SnomedConceptDocument> getDocumentType() {
		return SnomedConceptDocument.class;
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();

		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addEffectiveTimeClause(queryBuilder);
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.filter(refSetTypes(getCollection(OptionKey.TYPE, SnomedRefSetType.class)));
		} else {
			// always add type filter, so only concept docs with refset props will be returned
			queryBuilder.filter(Expressions.exists(SnomedConceptDocument.Fields.REFSET_TYPE));
		}
		
		if (containsKey(OptionKey.REFERENCED_COMPONENT_TYPE)) {
			queryBuilder.filter(referencedComponentTypes(getCollection(OptionKey.REFERENCED_COMPONENT_TYPE, Integer.class)));
		}
		
		if (containsKey(OptionKey.MAP_TARGET_COMPONENT_TYPE)) {
			queryBuilder.filter(mapTargetComponentTypes(getCollection(OptionKey.MAP_TARGET_COMPONENT_TYPE, Integer.class)));
		}
		
		return queryBuilder.build();
	}
	
	@Override
	protected SnomedReferenceSets toCollectionResource(BranchContext context, Hits<SnomedConceptDocument> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedReferenceSets(limit(), hits.getTotal());
		} else {
			return SnomedConverters.newRefSetConverter(context, expand(), locales()).convert(hits.getHits(), hits.getSearchAfter(), limit(), hits.getTotal());
		}
	}
	
	@Override
	protected SnomedReferenceSets createEmptyResult(int limit) {
		return new SnomedReferenceSets(limit, 0);
	}
	
}
