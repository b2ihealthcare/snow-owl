/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.Expressions.descendantIds;
import static com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.Expressions.refSetIds;
import static com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.Expressions.selfIds;
import static com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.Expressions.types;

import java.io.IOException;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.PredicateType;

/**
 * @since 4.7
 */
final class SnomedConstraintSearchRequest extends SearchResourceRequest<BranchContext, SnomedConstraints> {

	public enum OptionKey {
		
		/**
		 * Match MRCM constraints that are applicable to the given identifiers.
		 */
		SELF,
		
		/**
		 * Match MRCM constraints that are applicable to the hierarchy of the given identifiers.
		 */
		DESCENDANT,
		
		/**
		 * Match MRCM constraints that are applicable to the given reference set identifiers.
		 */
		REFSET, 
		
		/**
		 * Match MRCM constraints that has any of the given {@link PredicateType}.
		 */
		TYPE
		
	}
	
	@Override
	protected SnomedConstraints doExecute(BranchContext context) throws IOException {
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, SnomedConstraintDocument.Expressions::ids);
		
		if (containsKey(OptionKey.SELF)) {
			queryBuilder.must(selfIds(getCollection(OptionKey.SELF, String.class)));
		}
		
		if (containsKey(OptionKey.DESCENDANT)) {
			queryBuilder.must(descendantIds(getCollection(OptionKey.DESCENDANT, String.class)));
		}

		if (containsKey(OptionKey.REFSET)) {
			queryBuilder.must(refSetIds(getCollection(OptionKey.REFSET, String.class)));
		}
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.must(types(getCollection(OptionKey.TYPE, PredicateType.class)));
		}
		
		
		final Query<SnomedConstraintDocument> query = Query.select(SnomedConstraintDocument.class)
				.where(queryBuilder.build())
				.offset(offset())
				.limit(limit())
				.build();
		
		final Hits<SnomedConstraintDocument> hits = searcher.search(query);
		return SnomedConverters.newConstraintConverter(context, expand(), locales()).convert(hits.getHits(), offset(), limit(), hits.getTotal());
	}
	
	@Override
	protected SnomedConstraints createEmptyResult(int offset, int limit) {
		return new SnomedConstraints(offset, limit, 0);
	}

}
