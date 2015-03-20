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
package com.b2international.snowowl.datastore.server.personalization;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.quicksearch.FullQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.personalization.IComponentSetManager;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.QuickSearchContentProviderBroker;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexServerService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 *
 */
public abstract class AbstractComponentSetManager extends SingleDirectoryIndexServerService implements IComponentSetManager {

	private static final String FIELD_ID = "id";
	
	private static final String FIELD_TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";

	private static final String FIELD_USER_ID = "userId";
	
	private static final String FIELD_DATE_ADDED = "dateAdded";

	private static final Sort DESCENDING_DATE_SORT = new Sort(new SortField(FIELD_DATE_ADDED, Type.LONG, true));
	
	private static final Set<String> FIELDS_TO_LOAD = ImmutableSet.of(FIELD_ID, FIELD_TERMINOLOGY_COMPONENT_ID);

	protected AbstractComponentSetManager(File indexRootPath) {
		super(indexRootPath);
	}
	
	@Override
	public QuickSearchContentResult getComponents(@Nullable final String queryExpression, @Nonnull final IBranchPathMap branchPathMap, @Nonnegative final int limit, 
			@Nullable final Map<String, Object> configuration) {
		
		final String userId = (String) configuration.get(IQuickSearchProvider.CONFIGURATION_USER_ID);
		
		if (null == userId) {
			return new QuickSearchContentResult();
		}
		
		final Set<ComponentIdentifierPair<String>> componentsForUser = getComponentsForUser(userId, limit);
		
		final Multimap<String, ComponentIdentifierPair<String>> componentsByTerminology = Multimaps.index(componentsForUser, new Function<ComponentIdentifierPair<String>, String>() {
			@Override public String apply(@Nullable final ComponentIdentifierPair<String> input) {
				return input.getTerminologyComponentId();
			}
		});
		
		final Multimap<String, String> componentIdsByTerminology = Multimaps.transformValues(componentsByTerminology, new Function<ComponentIdentifierPair<String>, String>() {
			@Override public String apply(@Nullable final ComponentIdentifierPair<String> input) {
				return input.getComponentId();
			}
		});
		
		final List<QuickSearchElement> results = newArrayList();
		
		for (final String terminologyComponentId : componentIdsByTerminology.keySet()) {
			
			final Collection<String> componentIds = componentIdsByTerminology.get(terminologyComponentId);
			final Map<String, Object> configurationCopy = newHashMap(configuration);
			final Set<String> valueIds = newHashSet();
			
			if (configurationCopy.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET)) {
				valueIds.addAll((Set<String>) configurationCopy.get(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET));
				valueIds.retainAll(componentIds); // Keep only those allowed values which were picked or bookmarked
			} else {
				valueIds.addAll(componentIds);
			}
			
			configurationCopy.put(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET, valueIds);
			configurationCopy.put(IQuickSearchProvider.TERMINOLOGY_COMPONENT_ID, terminologyComponentId);
			
			// if the value id set is empty for the specified configuration then skip asking for element from the provider (as the provider will return all elements for empty value ids)
			if (!valueIds.isEmpty()) {
				final IQuickSearchContentProvider provider = QuickSearchContentProviderBroker.INSTANCE.getProviderForComponent(terminologyComponentId);
				
				if (null == provider) {
					// TODO: Log or throw exception?
					continue;
				}
				
				final QuickSearchContentResult componentResult = provider.getComponents(queryExpression, branchPathMap, limit, configurationCopy);
				
				for (final QuickSearchElement element : componentResult.getElements()) {
					results.add(new FullQuickSearchElement(element.getId(), element.getImageId(), element.getLabel(), element.isApproximate(), terminologyComponentId));
				}
			}
		}
		
		return new QuickSearchContentResult(results.size(), results);
	}

	@Override
	public void registerComponent(final ComponentIdentifierPair<String> componentIdentifierPair, final String userId) {
		final Document document = new Document();
		document.add(new StringField(FIELD_ID, componentIdentifierPair.getComponentId(), Store.YES));
		document.add(new StringField(FIELD_TERMINOLOGY_COMPONENT_ID, componentIdentifierPair.getTerminologyComponentId(), Store.YES));
		document.add(new StringField(FIELD_USER_ID, userId, Store.NO));
		document.add(new NumericDocValuesField(FIELD_DATE_ADDED, System.currentTimeMillis()));
		
		try {
			writer.addDocument(document);
			commit();
		} catch (final IOException e) {
			throw new ComponentSetManagerException(e);
		}
	}

	@Override
	public void unregisterComponent(final ComponentIdentifierPair<String> componentIdentifierPair, final String userId) {
		
		final BooleanQuery deletionQuery = new BooleanQuery();
		deletionQuery.add(new TermQuery(new Term(FIELD_USER_ID, userId)), Occur.MUST);
		deletionQuery.add(new TermQuery(new Term(FIELD_TERMINOLOGY_COMPONENT_ID, componentIdentifierPair.getTerminologyComponentId())), Occur.MUST);
		deletionQuery.add(new TermQuery(new Term(FIELD_ID, componentIdentifierPair.getComponentId())), Occur.MUST);
		
		try {
			writer.deleteDocuments(deletionQuery);
			commit();
		} catch (IOException e) {
			throw new ComponentSetManagerException(e);
		}
	}

	@Override
	public Set<ComponentIdentifierPair<String>> getComponentsForUser(final String userId, final int limit) {
		
		final Set<ComponentIdentifierPair<String>> results = newHashSet();
		IndexSearcher searcher = null;
		
		try {
			
			searcher = manager.acquire();
			final TopFieldDocs topDocs = searcher.search(new TermQuery(new Term(FIELD_USER_ID, userId)), limit, DESCENDING_DATE_SORT);
			
			for ( final ScoreDoc scoreDoc : topDocs.scoreDocs) {
				final Document document = searcher.doc(scoreDoc.doc, FIELDS_TO_LOAD);
				results.add(ComponentIdentifierPair.create(document.get(FIELD_TERMINOLOGY_COMPONENT_ID), document.get(FIELD_ID)));
			}
			
		} catch (final IOException e) {
			throw new ComponentSetManagerException(e);
		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new ComponentSetManagerException(e);
				}
			}
		}
		
		return results;
	}

	@Override
	public void clearAllComponentsForUser(final String userId) {
		try {
			writer.deleteDocuments(new TermQuery(new Term(FIELD_USER_ID, userId)));
			commit();
		} catch (final IOException e) {
			throw new ComponentSetManagerException(e);
		}
	}
}