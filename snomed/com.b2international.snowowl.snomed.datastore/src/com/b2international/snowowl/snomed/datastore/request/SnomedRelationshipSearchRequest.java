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
package com.b2international.snowowl.snomed.datastore.request;


import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeId;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.destinationIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.sourceIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.typeId;

import java.io.IOException;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 4.5
 */
final class SnomedRelationshipSearchRequest extends SnomedSearchRequest<SnomedRelationships> {

	enum OptionKey {
		SOURCE,
		TYPE,
		DESTINATION,
		CHARACTERISTIC_TYPE, 
		// TODO implement group based filtering
		GROUP
	}
	
	SnomedRelationshipSearchRequest() {}

	@Override
	protected SnomedRelationships doExecute(BranchContext context) throws IOException {
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		addComponentIdFilter(queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.must(typeId(getString(OptionKey.TYPE)));
		}
		
		if (containsKey(OptionKey.CHARACTERISTIC_TYPE)) {
			queryBuilder.must(characteristicTypeId(getString(OptionKey.CHARACTERISTIC_TYPE)));
		}

		if (containsKey(OptionKey.SOURCE)) {
			queryBuilder.must(sourceIds(getCollection(OptionKey.SOURCE, String.class)));
		}
		
		if (containsKey(OptionKey.DESTINATION)) {
			queryBuilder.must(destinationIds(getCollection(OptionKey.DESTINATION, String.class)));
		}

		final Hits<SnomedRelationshipIndexEntry> hits = searcher.search(Query.select(SnomedRelationshipIndexEntry.class)
				.where(queryBuilder.build())
				.offset(offset())
				.limit(limit())
				.build());
		final int totalHits = hits.getTotal();
		
		if (limit() < 1 || totalHits < 1) {
			return new SnomedRelationships(offset(), limit(), totalHits);
		}
		
		return SnomedConverters.newRelationshipConverter(context, expand(), locales()).convert(hits.getHits(), offset(), limit(), totalHits);
	}

	@Override
	protected Class<SnomedRelationships> getReturnType() {
		return SnomedRelationships.class;
	}
}
