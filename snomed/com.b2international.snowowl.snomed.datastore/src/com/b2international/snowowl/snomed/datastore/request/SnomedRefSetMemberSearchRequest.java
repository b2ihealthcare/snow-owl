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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.refSetTypes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.referenceSetId;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.targetComponents;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.functions.LongToStringFunction;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.escg.ConceptIdQueryEvaluator2;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.dsl.query.RValue;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberSearchRequest extends SnomedSearchRequest<SnomedReferenceSetMembers> {

	enum OptionKey {
		/**
		 * Filter by containing reference set
		 */
		REFSET,

		/**
		 * Filter by referenced component
		 */
		REFERENCED_COMPONENT,
		
		/**
		 * The referenced component type to match
		 */
		REFERENCED_COMPONENT_TYPE,
		
		/**
		 * Filter by refset type
		 */
		REFSET_TYPE,
		
		/**
		 * Filter by member specific props, the value should be a {@link Map} of RF2 headers and their corresponding values.
		 */
		PROPS
	}
	
	SnomedRefSetMemberSearchRequest() {}
	
	@Override
	protected SnomedReferenceSetMembers doExecute(BranchContext context) throws IOException {
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);

		final Collection<String> referenceSetIds = getCollection(OptionKey.REFSET, String.class);
		final Collection<String> referencedComponentIds = getCollection(OptionKey.REFERENCED_COMPONENT, String.class);
		final Collection<SnomedRefSetType> refSetTypes = getCollection(OptionKey.REFSET_TYPE, SnomedRefSetType.class);
		final Options propsFilter = getOptions(OptionKey.PROPS);
		
		ExpressionBuilder queryBuilder = Expressions.builder();
		
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		addComponentIdFilter(queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		
		if (!referenceSetIds.isEmpty()) {
			// if only one refset ID is defined, check if it's an ESCG expression and expand it, otherwise use as is
			final List<String> selectedRefSetIds;
			if (referenceSetIds.size() == 1) {
				final String escg = Iterables.get(referenceSetIds, 0);
				final RValue expression = context.service(EscgRewriter.class).parseRewrite(escg);
				final LongSet matchingConceptIds = new ConceptIdQueryEvaluator2(searcher).evaluate(expression);
				selectedRefSetIds = LongToStringFunction.copyOf(LongSets.toList(matchingConceptIds));
			} else {
				selectedRefSetIds = newArrayList(referenceSetIds);
			}
			
			queryBuilder.must(referenceSetId(selectedRefSetIds));
		}
		
		if (!referencedComponentIds.isEmpty()) {
			queryBuilder.must(referencedComponentIds(referencedComponentIds));
		}
		
		if (!refSetTypes.isEmpty()) {
			queryBuilder.must(refSetTypes(refSetTypes));
		}
		
		if (!propsFilter.isEmpty()) {
			if (propsFilter.containsKey(SnomedRf2Headers.FIELD_TARGET_COMPONENT)) {
				final Collection<String> targetComponentIds = propsFilter.getCollection(SnomedRf2Headers.FIELD_TARGET_COMPONENT, String.class);
				queryBuilder.must(targetComponents(targetComponentIds));
			}
		}
		
		final Query<SnomedRefSetMemberIndexEntry> query = Query.builder(SnomedRefSetMemberIndexEntry.class)
			.selectAll()
			.where(queryBuilder.build())
			.offset(offset())
			.limit(limit())
			.build();

		final Hits<SnomedRefSetMemberIndexEntry> hits = searcher.search(query);
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedReferenceSetMembers(offset(), limit(), hits.getTotal());
		} else {
			return SnomedConverters.newMemberConverter(context, expand(), locales()).convert(hits.getHits(), offset(), limit(), hits.getTotal());
		}

	}

	@Override
	protected Class<SnomedReferenceSetMembers> getReturnType() {
		return SnomedReferenceSetMembers.class;
	}
}
