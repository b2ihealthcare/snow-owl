/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.refSetTypes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.referencedComponentTypes;

import java.io.IOException;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
final class SnomedRefSetSearchRequest extends SnomedSearchRequest<SnomedReferenceSets> {

	enum OptionKey {
		/**
		 * A collection of {@link SnomedRefSetType reference set types} to use
		 */
		TYPE,
		
		/**
		 * A collection of referenced component types to use
		 */
		REFERENCED_COMPONENT_TYPE
	};

	@Override
	protected SnomedReferenceSets doExecute(BranchContext context) throws IOException {
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addComponentIdFilter(queryBuilder);
		addModuleClause(queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.must(refSetTypes(getCollection(OptionKey.TYPE, SnomedRefSetType.class)));
		} else {
			// always add type filter, so only concept docs with refset props will be returned
			queryBuilder.must(Expressions.prefixMatch(SnomedConceptDocument.Fields.REFSET_TYPE, ""));
		}
		
		if (containsKey(OptionKey.REFERENCED_COMPONENT_TYPE)) {
			queryBuilder.must(referencedComponentTypes(getCollection(OptionKey.REFERENCED_COMPONENT_TYPE, Integer.class)));
		}
		
		final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
				.where(queryBuilder.build())
				.offset(offset())
				.limit(limit())
				.build();
		
		final Hits<SnomedConceptDocument> hits = searcher.search(query);
		
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedReferenceSets(offset(), limit(), hits.getTotal());
		} else {
			return SnomedConverters.newRefSetConverter(context, expand(), locales()).convert(hits.getHits(), offset(), limit(), hits.getTotal());
		}
	}
	
	@Override
	protected SnomedReferenceSets createEmptyResult(int offset, int limit) {
		return new SnomedReferenceSets(offset, limit, 0);
	}
	
}
