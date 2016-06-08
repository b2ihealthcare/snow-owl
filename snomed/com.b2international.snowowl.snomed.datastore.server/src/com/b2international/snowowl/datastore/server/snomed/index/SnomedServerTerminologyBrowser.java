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
	public boolean isTerminologyAvailable(IBranchPath branchPath) {
		return exists(branchPath, Concepts.ROOT_CONCEPT);
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSuperTypes(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSuperTypesById(branchPath, concept.getId());
	}
	
	@Override
	public long getStorageKey(final IBranchPath branchPath, final String conceptId) {
		throw new UnsupportedOperationException();
//		Preconditions.checkNotNull(conceptId, "Concept ID argument cannot be null.");
//		
//		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(conceptId), 1);
//		
//		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
//			return -1L;
//		}
//		
//		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().storageKey().build());
//		return Mappings.storageKey().getValue(doc);
	}
	
	@Override
	public List<SnomedConceptDocument> getSubTypesAsList(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<SnomedConceptDocument> getSubTypes(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<SnomedConceptDocument> getAllSubTypes(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Collection<SnomedConceptDocument> getAllSubTypesById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<SnomedConceptDocument> getAllSuperTypes(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<SnomedConceptDocument> getAllSuperTypesById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int getAllSubTypeCount(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getAllSubTypeCountById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int getSubTypeCount(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getAllSuperTypeCount(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getAllSuperTypeCountById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int getSuperTypeCount(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int getSuperTypeCountById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isSuperTypeOf(final IBranchPath branchPath, final SnomedConceptDocument superType, final SnomedConceptDocument subType) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isSuperTypeOfById(final IBranchPath branchPath, final String superTypeId, final String subTypeId) {
		throw new UnsupportedOperationException();
	}
	
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
	
	@Override
	public long[][] getAllConceptIdsStorageKeys(final IBranchPath branchPath) {
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final ConceptIdStorageKeyCollector collector = new ConceptIdStorageKeyCollector();
			service.search(branchPath, SnomedMappings.newQuery().concept().matchAll(), collector);
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
	public long[][] getAllActiveConceptIdsStorageKeys(final IBranchPath branchPath) {
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

//	@Override
//	protected Set<String> getExtendedComponentFieldsToLoad() {
//		return SnomedMappings.fieldsToLoad().fields(super.getExtendedComponentFieldsToLoad()).memberUuid().memberReferencedComponentId().memberReferencedComponentType().build();
//	}
//	
//	@Override
//	protected ExtendedComponent convertDocToExtendedComponent(IBranchPath branchPath, Document doc) {
//		// if type field is null, then we are processing a reference set _member_
//		final String iconId = doc.get(Mappings.iconId().fieldName());
//		final List<Integer> types = Mappings.type().getValues(doc);
//		final String id;
//		
//		if (types.isEmpty()) {
//			id = Long.toString(SnomedMappings.memberReferencedComponentId().getValue(doc));
//		} else {
//			id = Long.toString(SnomedMappings.id().getValue(doc));
//		}
//		
//		final short terminologyComponentId;
//		
//		if (types.isEmpty()) {
//			terminologyComponentId = SnomedMappings.memberReferencedComponentType().getShortValue(doc);
//		} else if (types.size() == 1) {
//			// core SNOMED CT Component only
//			terminologyComponentId = types.get(0).shortValue();
//		} else {
//			// concept + refset
//			terminologyComponentId = SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
//		}
//		
//		if (types.isEmpty()) {
//			return new ExtendedComponentImpl(SnomedMappings.memberUuid().getValue(doc), id, iconId, SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER);
//		} else {
//			return new ExtendedComponentImpl(id, id, iconId, terminologyComponentId);
//		}
//	}

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
	
//	@Override
//	protected Query getRootConceptsQuery() {
//		return SnomedMappings.newQuery().parent(SnomedMappings.ROOT_ID).active().matchAll();
//	}

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
	public boolean isUniqueId(final IBranchPath branchPath, final String componentId) {
		checkNotNull(componentId, "SNOMED CT core component ID argument cannot be null.");
		if (SnomedTerminologyComponentConstants.isCoreComponentId(componentId)) {
			return service.getHitCount(branchPath, SnomedMappings.newQuery().id(componentId).matchAll(), null) == 0;
		}
		return false;
	}
	
	@Override
	public int getDepth(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(conceptId, "conceptId");
		
		final long conceptIdL = Long.parseLong(conceptId);
		final LongSet allSuperTypeIds = getAllSuperTypeIds(branchPath, conceptIdL);
		if (CompareUtils.isEmpty(allSuperTypeIds)) {
			return 0;
		}
		
		final Multimap<Long, Long> parentageMap = Multimaps.synchronizedMultimap(HashMultimap.<Long, Long>create());
		parentageMap.putAll(conceptIdL, toSet(allSuperTypeIds));
		parallelForEach(allSuperTypeIds, new LongSets.LongCollectionProcedure() {
			@Override
			public void apply(long conceptId) {
				final LongSet superTypeIds = getSuperTypeIds(branchPath, conceptId);
				parentageMap.putAll(conceptId, toSet(superTypeIds));
			}
		});
		
		return GraphUtils.getLongestPath(parentageMap).size() - 1;
		
	}
	
	@Override
	public int getHeight(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(conceptId, "conceptId");
		
		final Query query = getAllSubTypesQuery(conceptId);

		final AtomicReference<IndexSearcher> searcher = new AtomicReference<IndexSearcher>();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		ReferenceManager<IndexSearcher> manager = null;

		try {

			final Multimap<Long, Long> parentageMap = Multimaps.synchronizedMultimap(HashMultimap.<Long, Long>create());
			manager = service.getManager(branchPath);
			searcher.set(manager.acquire());
			IndexUtils.parallelForEachDocId(collector.getDocIDs(), new IndexUtils.DocIdProcedure() {
				@Override
				public void apply(final int docId) throws IOException {
					final Document doc = searcher.get().doc(docId, SnomedMappings.fieldsToLoad().id().parent().build());
					final long id = SnomedMappings.id().getValue(doc);
					parentageMap.putAll(id, SnomedMappings.parent().getValues(doc));
				}
			});
			
			return GraphUtils.getLongestPath(parentageMap).size();
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting height of node '" + conceptId + "'.", e);
		} finally {
			if (null != manager && null != searcher.get()) {
				try {
					manager.release(searcher.get());
				} catch (final IOException e) {
					try {
						manager.release(searcher.get());
					} catch (final IOException e1) {
						e.addSuppressed(e1);
					}
					throw new IndexException("Error while releasing index searcher.", e);
				}
			}
		}
		
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSuperTypesById(final IBranchPath branchPath, final String id) {
		final Builder<SnomedConceptDocument> builder = ImmutableList.builder();
		for (String superTypeId : getSuperTypeIds(branchPath, id)) {
			builder.add(getConcept(branchPath, superTypeId));
		}
		return builder.build();
	}

	@Override
	protected Query getSubTypesQuery(String id) {
		return SnomedMappings.newQuery().parent(id).and(getTerminologyComponentTypeQuery()).matchAll();
	}
	
}
