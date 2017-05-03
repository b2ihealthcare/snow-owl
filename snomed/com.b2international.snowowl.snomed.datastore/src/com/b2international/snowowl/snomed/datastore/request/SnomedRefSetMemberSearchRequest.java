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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.FieldValueFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.commons.options.Options;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import bak.pcj.LongCollection;

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
		final IndexSearcher searcher = context.service(IndexSearcher.class);

		final BooleanFilter filter = new BooleanFilter();
		final Collection<String> referenceSetIds = getCollection(OptionKey.REFSET, String.class);
		final Collection<String> referencedComponentIds = getCollection(OptionKey.REFERENCED_COMPONENT, String.class);
		final Collection<SnomedRefSetType> refSetTypes = getCollection(OptionKey.REFSET_TYPE, SnomedRefSetType.class);
		final Options propsFilter = getOptions(OptionKey.PROPS);
		
		if (!referenceSetIds.isEmpty()) {
			// if only one refset ID is defined, check if it's an ESCG expression and expand it, otherwise use as is
			final List<Long> selectedRefSetIds;
			if (referenceSetIds.size() == 1) {
				final String escg = Iterables.get(referenceSetIds, 0);
				final LongCollection matchingConceptIds = context.service(IEscgQueryEvaluatorService.class).evaluateConceptIds(context.branch().branchPath(), escg);
				selectedRefSetIds = LongSets.toList(matchingConceptIds);
			} else {
				selectedRefSetIds = StringToLongFunction.copyOf(referenceSetIds);
			}
			addFilterClause(filter, SnomedMappings.memberRefSetId().createTermsFilter(selectedRefSetIds), Occur.MUST);
		}
		
		if (!referencedComponentIds.isEmpty()) {
			addFilterClause(filter, SnomedMappings.memberReferencedComponentId().createTermsFilter(StringToLongFunction.copyOf(referencedComponentIds)), Occur.MUST);
		}
		
		if (!refSetTypes.isEmpty()) {
			final ImmutableList<Integer> types = FluentIterable.from(refSetTypes).transform(new Function<SnomedRefSetType, Integer>() {
				@Override
				public Integer apply(SnomedRefSetType input) {
					return input.getValue();
				}
			}).toList();
			
			addFilterClause(filter, SnomedMappings.memberRefSetType().createTermsFilter(types), Occur.MUST);
		}
		
		if (!propsFilter.isEmpty()) {
			if (propsFilter.containsKey(SnomedRf2Headers.FIELD_TARGET_COMPONENT)) {
				final Collection<String> targetComponentIds = propsFilter.getCollection(SnomedRf2Headers.FIELD_TARGET_COMPONENT, String.class);
				addFilterClause(filter, SnomedMappings.memberTargetComponentId().createTermsFilter(targetComponentIds), Occur.MUST);
			} else if (propsFilter.containsKey(SnomedRf2Headers.FIELD_MAP_TARGET)) {
				final Collection<String> mapTargets = propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_TARGET, String.class);
				addFilterClause(filter, SnomedMappings.memberMapTargetComponentId().createTermsFilter(mapTargets), Occur.MUST);
			}
		}
		
		addFilterClause(filter, new FieldValueFilter(SnomedMappings.memberReferencedComponentType().fieldName()),Occur.MUST);
	
		SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery();
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		
		final Query query = createConstantScoreQuery(
				createFilteredQuery(queryBuilder.isEmpty() ? new MatchAllDocsQuery() : queryBuilder.matchAll(), filter));
		final int totalHits = getTotalHits(searcher, query);

		if (limit() < 1 || totalHits < 1) {
			return new SnomedReferenceSetMembers(offset(), limit(), totalHits);
		}
		
		final TopDocs topDocs = searcher.search(query, null, numDocsToRetrieve(searcher, totalHits), Sort.INDEXORDER, false, false);
		if (topDocs.scoreDocs.length < 1) {
			return new SnomedReferenceSetMembers(offset(), limit(), topDocs.totalHits);
		}

		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<SnomedRefSetMemberIndexEntry> memberBuilder = ImmutableList.builder();
		
		for (int i = offset(); i < scoreDocs.length; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedRefSetMemberIndexEntry indexEntry = SnomedRefSetMemberIndexEntry.builder(doc).build();
			memberBuilder.add(indexEntry);
		}

		List<SnomedRefSetMemberIndexEntry> members = memberBuilder.build();
		return SnomedConverters.newMemberConverter(context, expand(), locales()).convert(members, offset(), limit(), topDocs.totalHits);
	}

	@Override
	protected Class<SnomedReferenceSetMembers> getReturnType() {
		return SnomedReferenceSetMembers.class;
	}
}
