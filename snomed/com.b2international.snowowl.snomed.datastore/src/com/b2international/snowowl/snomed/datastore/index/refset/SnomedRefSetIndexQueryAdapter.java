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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDslIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Optional;

/**
 * Index query adapter for retrieving search results from lightweight store and building 
 * {@link SnomedRefSetIndexEntry reference sets}.
 * 
 */
public class SnomedRefSetIndexQueryAdapter extends SnomedDslIndexQueryAdapter<SnomedRefSetIndexEntry> implements Serializable {

	private static final long serialVersionUID = 4673150012297885991L;

	/**
	 * Search based on reference set id.
	 */
	public static final int SEARCH_BY_ID = 1;
	
	/**
	 * Search by reference set name.
	 */
	public static final int SEARCH_BY_LABEL = 1 << 1;
	
	/**
	 * Search reference set name used prefixed query string.
	 * Default is analyzed query string. 
	 */
	public static final int SEARCH_PREFIXED_TERM = 1 << 2;
	
	/**
	 * Search reference set name used prefixed query string.
	 * Default
	 */
	public static final int SEARCH_ANALYZED_TERM = 1 << 3;
	
	/**
	 * Do not return structural reference sets if this flag is set.
	 */
	public static final int SEARCH_REGULAR_ONLY = 1 << 4;

	private final Short referencedComponentType;
	private final SnomedRefSetType[] refSetTypes;

	/**
	 * Creates a new instance of this query adapter.
	 * @param searchString the search string. Can be {@code null}.
	 * @param referencedComponentType the numeric ID of the reference set's referenced component type. Can be {@code null}.
	 * @param refSetTypes the reference set types to search for. Types may not be {@code null}.
	 */
	public SnomedRefSetIndexQueryAdapter(final int searchFlags, @Nullable final String searchString, 
			@Nullable final Short referencedComponentType, @Nonnull final SnomedRefSetType... refSetTypes) { 
		this(searchFlags, searchString, referencedComponentType, null, refSetTypes);
	}
		
	public SnomedRefSetIndexQueryAdapter(final int searchFlags, @Nullable final String searchString, 
				@Nullable final Short referencedComponentType, @Nullable final String[] componentIds, @Nonnull final SnomedRefSetType... refSetTypes) { // XXX: order of arguments is different
		super(searchString, searchFlags, componentIds);
		this.referencedComponentType = referencedComponentType;
		this.refSetTypes = refSetTypes;
	}

	@Override
	public SnomedRefSetIndexEntry buildSearchResult(final Document document, final IBranchPath branchPath, final float score) {
		
		final Builder builder = SnomedRefSetIndexEntry.builder()
				.score(score)
				.id(SnomedMappings.id().getValueAsString(document))
				.moduleId(SnomedMappings.module().getValueAsString(document))
				.storageKey(SnomedMappings.refSetStorageKey().getValue(document)) // XXX: Different than concept storage key
				.active(BooleanUtils.valueOf(SnomedMappings.active().getValue(document).intValue())) 
				.released(BooleanUtils.valueOf(SnomedMappings.released().getValue(document).intValue()))
				.effectiveTimeLong(SnomedMappings.effectiveTime().getValue(document))
				.type(SnomedRefSetType.get(Mappings.intField(SnomedIndexBrowserConstants.REFERENCE_SET_TYPE).getValue(document)))
				.referencedComponentType(Mappings.intField(SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE).getShortValue(document));
				// TODO: .mapTargetComponentType(...) is not indexed yet 
				
		return builder.build();
	}

	@Override
	public Query createQuery() {
		final SnomedQueryBuilder query = SnomedMappings.newQuery()
				.refSet();
		
		if (referencedComponentType != null) {
			query.field(REFERENCE_SET_REFERENCED_COMPONENT_TYPE, referencedComponentType.intValue());
		}
		
		if (null != refSetTypes && refSetTypes.length > 0) {
			final SnomedQueryBuilder refsetTypeQuery = SnomedMappings.newQuery();
			for (final SnomedRefSetType refSetType : refSetTypes) {
				refsetTypeQuery.field(REFERENCE_SET_TYPE, refSetType.getValue());
			}
			// at least one type has to match
			query.and(refsetTypeQuery.matchAny());
		}

		if ((searchFlags & SEARCH_REGULAR_ONLY) != 0) {
			query.field(REFERENCE_SET_STRUCTURAL, 0);
		}

		// Shortcut for empty search terms: return all reference sets
		if (StringUtils.isEmpty(searchString)) {
			return query.matchAll();
		}
		
		final IndexQueryBuilder queryExpressionQueryBuilder = new IndexQueryBuilder();
		if (anyFlagSet(SEARCH_BY_ID)) {
			final Optional<Long> parsedSearchStringOptional = IndexUtils.parseLong(searchString);
			if (!parsedSearchStringOptional.isPresent()) {
				if (!anyFlagSet(SEARCH_BY_LABEL)) {
					//XXX akitta: if just ignore this case our query could end up like this:
					//reference sets type (could have regular reference set restriction)
					//even if the ID restriction was enabled and the query term was 'virtual'
					//so we rather restrict the ID to an invalid one
					//XXX zstorok: only add the invalid ID query if searching by label is not enabled
					queryExpressionQueryBuilder.require(SnomedMappings.newQuery().id(-1L).matchAll());
				} else {
					queryExpressionQueryBuilder.require(createLabelQueryBuilder());
				}
			} else {
				final Query componentIdQuery= SnomedMappings.newQuery().id(parsedSearchStringOptional.get()).matchAll();
				if (anyFlagSet(SEARCH_BY_LABEL)) {
					queryExpressionQueryBuilder.match(componentIdQuery);
					queryExpressionQueryBuilder.match(createLabelQueryBuilder());
				} else {
					queryExpressionQueryBuilder.require(componentIdQuery);
				}
			}
		} else {
			queryExpressionQueryBuilder.require(createLabelQueryBuilder());
		}
		return query.and(queryExpressionQueryBuilder.toQuery()).matchAll();
	}
	
	@Override
	protected List<DocumentWithScore> doSearch(final IIndexService<? super SnomedRefSetIndexEntry> indexService, final IBranchPath branchPath, final int limit) {
		final List<DocumentWithScore> results = super.doSearch(indexService, branchPath, limit);
		
		for (final Iterator<DocumentWithScore> itr = results.iterator(); itr.hasNext(); /**/) {
			
			final DocumentWithScore refSetDocument = itr.next();
			final Document doc = refSetDocument.getDocument();
			final String refSetId = SnomedMappings.id().getValueAsString(doc);
			if (!isEmpty(refSetId)) {
				
				@SuppressWarnings("rawtypes")
				final IIndexQueryAdapter queryAdapter = new SnomedDOIQueryAdapter(refSetId, ""/*user ID*/, (String[]) null);
				
				if (inactiveConcept(indexService, branchPath, queryAdapter)) {
					itr.remove();
				}
			}
		
		}
		return results;
		
	}
	
	@SuppressWarnings("unchecked")
	private boolean inactiveConcept(final IIndexService<? super SnomedRefSetIndexEntry> indexService, final IBranchPath branchPath, @SuppressWarnings("rawtypes") final IIndexQueryAdapter queryAdapter) {
		return 1 > indexService.getHitCount(branchPath, queryAdapter);
	}
	
	@Override
	public int getHitCount(IIndexService<? super SnomedRefSetIndexEntry> indexService, IBranchPath branchPath) {
		final int hitCount = super.getHitCount(indexService, branchPath);
		if (hitCount < 1) {
			return 0;
		}
		return doSearch(indexService, branchPath, hitCount).size();
	}
	
	private IndexQueryBuilder createLabelQueryBuilder() {
		final IndexQueryBuilder labelQueryBuilder = new IndexQueryBuilder();
		if ((searchFlags & SEARCH_PREFIXED_TERM) != 0) {
			labelQueryBuilder.matchAllTokenizedTermPrefixes(Mappings.label().fieldName(), searchString.toLowerCase());
		} else {
			//	analyzed query, there is no need of lowercasing the search string
			labelQueryBuilder.matchParsedTerm(Mappings.label().fieldName(), searchString);
		}
		return labelQueryBuilder;
	}
	
}
