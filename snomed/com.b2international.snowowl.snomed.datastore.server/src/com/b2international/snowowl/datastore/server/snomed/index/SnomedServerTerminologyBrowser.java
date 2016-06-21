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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.commons.collect.LongSets.parallelForEach;
import static com.b2international.commons.collect.LongSets.toSet;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.hash.Hashing.murmur3_32;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.graph.GraphUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.EscgExpressionConstants;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.escg.EscgParseFailedException;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.filteredrefset.FilteredRefSetMemberBrowser2;
import com.b2international.snowowl.snomed.datastore.filteredrefset.IRefSetMemberOperation;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntryWithChildFlag;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Index-based SNOMED CT Terminology browser implementation.
 */
public class SnomedServerTerminologyBrowser implements SnomedTerminologyBrowser {

	@Override
	public SnomedConceptDocument getTopLevelConcept(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		checkNotNull(concept, "Concept must not be null.");
		final Collection<SnomedConceptDocument> allSuperTypes = getAllSuperTypes(branchPath, concept);
		final Collection<SnomedConceptDocument> rootConcepts = getRootConcepts(branchPath);
		for (final SnomedConceptDocument conceptMini : allSuperTypes) {
			if (rootConcepts.contains(conceptMini))
				return conceptMini;
		}
		throw new IllegalStateException("Top level parent can't be determined for: " + concept);
	}

	@Override
	public LongSet getAllSubTypeIds(final IBranchPath branchPath, final long conceptId) {
		return getIds(branchPath, getAllSubTypesQuery(String.valueOf(conceptId)));
	}
	
	@Override
	public LongSet getSubTypeIds(final IBranchPath branchPath, final long conceptId) {
		return getIds(branchPath, getSubTypesQuery(Long.toString(conceptId)));
	}

	@Override
	public LongSet getSuperTypeIds(final IBranchPath branchPath, final long conceptId) {
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(Long.toString(conceptId)), 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().build());
		return SnomedMappings.parent().getValueAsLongSet(document);
	}

	@Override
	public LongSet getAllSuperTypeIds(final IBranchPath branchPath, final long conceptId) {
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(Long.toString(conceptId)), 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().ancestor().build());
		
		final LongSet parents = SnomedMappings.parent().getValueAsLongSet(doc);
		final LongSet ancestors = SnomedMappings.ancestor().getValueAsLongSet(doc);
		if (parents.isEmpty() && ancestors.isEmpty()) {
			return LongCollections.emptySet();
		} else {
			final LongSet ids = PrimitiveSets.newLongOpenHashSetWithExpectedSize(parents.size() + ancestors.size());
			ids.addAll(parents);
			ids.addAll(ancestors);
			return ids;
		}
	}
	
	private LongSet getIds(final IBranchPath branchPath, final Query query) {
		checkNotNull(query, "Query argument cannot be null.");
		try {
			final int resultSize = service.getTotalHitCount(branchPath, query);
			
			if (0 == resultSize) {
				return PrimitiveSets.newLongOpenHashSet(); // guard against lower bound cannot be negative: 0
			}
			
			final LongSet ids = PrimitiveSets.newLongOpenHashSetWithExpectedSize(resultSize, 1.0D); //optimized load factor
	
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			while (itr.next()) {
				Document document = service.document(branchPath, itr.getDocID(), SnomedMappings.fieldsToLoad().id().build());
				ids.add(SnomedMappings.id().getValue(document));
			}
			
			return ids;
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	private LongSet getStorageKeys(final IBranchPath branchPath, final Query query) {
		checkNotNull(query, "Query argument cannot be null.");
		try {
			
			final LongSet ids = PrimitiveSets.newLongOpenHashSet(murmur3_32());
	
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			while (itr.next()) {
				final Document doc = service.document(branchPath, itr.getDocID(), SnomedMappings.fieldsToLoad().storageKey().build());
				ids.add(Mappings.storageKey().getValue(doc));
			}
			
			return ids;
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	@Override
	public LongCollection getAllActiveConceptIds(final IBranchPath branchPath) {
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.id().fieldName());
			service.search(branchPath, SnomedMappings.newQuery().active().concept().matchAll(), collector);
			return collector.getValues();
		} catch (final IOException e) {
			throw new IndexException(e);
		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}
	
	@Override
	public LongCollection getAllConceptIds(final IBranchPath branchPath) {
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.id().fieldName());
			service.search(branchPath, SnomedMappings.newQuery().concept().matchAll(), collector);
			return collector.getValues();
		} catch (final IOException e) {
			throw new IndexException(e);
		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}
	
	private long[][] getAllActiveConceptIdsStorageKeys(final IBranchPath branchPath) {
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final ConceptIdStorageKeyCollector collector = new ConceptIdStorageKeyCollector();
			service.search(branchPath, SnomedMappings.newQuery().active().concept().matchAll(), collector);
			return collector.getIds();
		} catch (final IOException e) {
			throw new IndexException(e);
		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}

	@Override
	public LongKeyLongMap getConceptIdToStorageKeyMap(final IBranchPath branchPath) {
		final long[][] idPairs = getAllActiveConceptIdsStorageKeys(branchPath);
		
		if (CompareUtils.isEmpty(idPairs)) {
			return PrimitiveMaps.newLongKeyLongOpenHashMap();
		}
		
		final LongKeyLongMap $ = PrimitiveMaps.newLongKeyLongOpenHashMapWithExpectedSize(idPairs.length);
		for (int i = 0; i < idPairs.length; i++) {
			$.put(idPairs[i][0], idPairs[i][1]);
		}
		
		return $;
	}
	
	@Override
	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId) {
		checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(componentId), 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().build());
		return SnomedMappings.parent().getValuesAsStringList(doc);
	}

	@Override
	public Collection<SnomedConceptDocument> getSuperTypesById(final IBranchPath branchPath, final String id) {
		final Builder<SnomedConceptDocument> builder = ImmutableList.builder();
		for (String superTypeId : getSuperTypeIds(branchPath, id)) {
			builder.add(getConcept(branchPath, superTypeId));
		}
		return builder.build();
	}

}
