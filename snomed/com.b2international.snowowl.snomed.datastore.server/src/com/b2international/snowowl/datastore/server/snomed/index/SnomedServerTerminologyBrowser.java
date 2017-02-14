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

import static com.b2international.commons.pcj.LongSets.newLongSetWithExpectedSize;
import static com.b2international.commons.pcj.LongSets.newLongSetWithMurMur3Hash;
import static com.b2international.commons.pcj.LongSets.parallelForEach;
import static com.b2international.commons.pcj.LongSets.toSet;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.util.StringUtil;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.graph.GraphUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.index.AbstractIndexTerminologyBrowser;
import com.b2international.snowowl.datastore.server.snomed.filteredrefset.FilteredRefSetMemberBrowser2Builder;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.EscgExpressionConstants;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.escg.EscgParseFailedException;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.filteredrefset.FilteredRefSetMemberBrowser2;
import com.b2international.snowowl.snomed.datastore.filteredrefset.IRefSetMemberOperation;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntryWithChildFlag;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
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
public class SnomedServerTerminologyBrowser extends AbstractIndexTerminologyBrowser<SnomedConceptIndexEntry> implements SnomedTerminologyBrowser {

	private static final Set<String> CONCEPT_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.iconId()
			.storageKey()
			.module()
			.active()
			.effectiveTime()
			.primitive()
			.exhaustive()
			.released()
			.parent()
			.statedParent()
			.ancestor()
			.statedAncestor()
			.build();
	
	/**
	 * Class constructor.
	 * @param index service for the ontology. 
	 */
	public SnomedServerTerminologyBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}

	@Deprecated
	@Override
	public IFilterClientTerminologyBrowser<SnomedConceptIndexEntry, String> filterTerminologyBrowser(IBranchPath branchPath, String expression,
			IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isTerminologyAvailable(IBranchPath branchPath) {
		return exists(branchPath, Concepts.ROOT_CONCEPT);
	}
	
	@Override
	protected Query getComponentIdQuery(String componentId) {
		return SnomedMappings.newQuery().id(componentId).matchAll();
	}
	
	@Override
	protected final Query getConceptByIdQueryBuilder(final String componentId) {
		return SnomedMappings.newQuery().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).id(componentId).matchAll();
	}
	
	@Override
	protected Query createFilterTerminologyBrowserQuery(final String expression) {
		if (StringUtils.isEmpty(expression)) {
			return SnomedMappings.newQuery().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).matchAny();
		}
		return SnomedMappings.newQuery().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).descriptionTerm(expression).matchAll();
	}
	
	@Override
	protected SnomedConceptIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		return SnomedConceptIndexEntry
				.builder(doc)
				.parents(SnomedMappings.parent().getValueAsLongList(doc))
				.statedParents(SnomedMappings.statedParent().getValueAsLongList(doc))
				.ancestors(SnomedMappings.ancestor().getValueAsLongList(doc))
				.statedAncestors(SnomedMappings.statedAncestor().getValueAsLongList(doc))
				.build();
	}

	@Override
	protected Set<String> getFieldNamesToLoad() {
		return CONCEPT_FIELDS_TO_LOAD;
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSuperTypesById(branchPath, concept.getId());
	}
	
	@Override
	public long getStorageKey(final IBranchPath branchPath, final String conceptId) {
		Preconditions.checkNotNull(conceptId, "Concept ID argument cannot be null.");
		
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(conceptId), 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1L;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().storageKey().build());
		return Mappings.storageKey().getValue(doc);
	}
	
	@Override
	public List<SnomedConceptIndexEntry> getSubTypesAsList(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		return Lists.newArrayList(getSubTypes(branchPath, concept));
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSubTypesById(branchPath, concept.getId());
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getAllSubTypesById(branchPath, concept.getId());
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypesById(final IBranchPath branchPath, final String id) {
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, getAllSubTypesQuery(id), collector);
			final DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
			return createResultObjects(branchPath, docIdsIterator);
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving sub types of " + id + ".", e);
		}
	}

	protected Query getAllSubTypesQuery(final String id) {
		return SnomedMappings.newQuery()
				.concept()
				.and(SnomedMappings.newQuery().ancestor(id).parent(id).matchAny())
				.matchAll();
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getAllSuperTypesById(branchPath, concept.getId());
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypesById(final IBranchPath branchPath, final String id) {
		final LongSet allSuperTypeIds = getAllSuperTypeIds(branchPath, Long.parseLong(id));
		final Builder<SnomedConceptIndexEntry> builder = ImmutableList.builder();
		for (final Long superType : allSuperTypeIds.toArray()) {
			builder.add(getConcept(branchPath, Long.toString(superType)));
		}
		return builder.build();
	}
	
	@Override
	public int getAllSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getAllSubTypeCountById(branchPath, concept.getId());
	}

	@Override
	public int getAllSubTypeCountById(final IBranchPath branchPath, final String id) {
		return getQueryResultCount(branchPath, getAllSubTypesQuery(id));
	}
	
	@Override
	public int getSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSubTypeCountById(branchPath, concept.getId());
	}

	@Override
	public int getAllSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getAllSuperTypeCountById(branchPath, concept.getId());
	}

	@Override
	public int getAllSuperTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(id, "ID must not be null.");
		// TODO: improve naive implementation
		return getAllSuperTypesById(branchPath, id).size();
	}
	
	@Override
	public int getSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSuperTypeCountById(branchPath, concept.getId());
	}
	
	@Override
	public int getSuperTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(id, "ID must not be null.");
		// TODO: improve naive implementation
		return getSuperTypesById(branchPath, id).size();
	}
	
	@Override
	public boolean isSuperTypeOf(final IBranchPath branchPath, final SnomedConceptIndexEntry superType, final SnomedConceptIndexEntry subType) {
		return isSuperTypeOfById(branchPath, superType.getId(), subType.getId());
	}
	
	@Override
	public boolean isSuperTypeOfById(final IBranchPath branchPath, final String superTypeId, final String subTypeId) {
		checkNotNull(superTypeId, "Super type ID must not be null.");
		checkNotNull(subTypeId, "Sub type ID must not be null.");
		final long subTypeIdLong = Long.parseLong(subTypeId);
		final long superTypeIdLong = Long.parseLong(superTypeId);
		
		final LongSet allSuperTypeIds = getAllSuperTypeIds(branchPath, subTypeIdLong);
		return allSuperTypeIds.contains(superTypeIdLong);
	}
	
	@Override
	public SnomedConceptIndexEntry getTopLevelConcept(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		final Collection<SnomedConceptIndexEntry> allSuperTypes = getAllSuperTypes(branchPath, concept);
		final Collection<SnomedConceptIndexEntry> rootConcepts = getRootConcepts(branchPath);
		for (final SnomedConceptIndexEntry conceptMini : allSuperTypes) {
			if (rootConcepts.contains(conceptMini))
				return conceptMini;
		}
		throw new IllegalStateException("Top level parent can't be determined for: " + concept);
	}

	@Override
	public Iterable<SnomedConceptIndexEntry> getConcepts(final IBranchPath branchPath) {
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath, SnomedMappings.newQuery().concept().matchAll(), collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying all concepts.", e);
		}
	}

	@Override
	public int getConceptCount(final IBranchPath branchPath) {
		return getQueryResultCount(branchPath, SnomedMappings.newQuery().concept().matchAll());
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getConcepts(final IBranchPath branchPath, final Iterable<String> ids) {
		checkNotNull(ids, "ids");
		final Set<SnomedConceptIndexEntry> concepts = newHashSet();
		for (final String id : ids) {
			final SnomedConceptIndexEntry concept = getConcept(branchPath, id);
			if (null != concept) {
				concepts.add(concept);
			}
		}
		return concepts;
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
			return new LongOpenHashSet();
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().build());
		return SnomedMappings.parent().getValueAsLongSet(document);
	}

	@Override
	public LongSet getAllSuperTypeIds(final IBranchPath branchPath, final long conceptId) {
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(Long.toString(conceptId)), 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return new LongOpenHashSet();
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().ancestor().build());
		
		final LongSet parents = SnomedMappings.parent().getValueAsLongSet(doc);
		final LongSet ancestors = SnomedMappings.ancestor().getValueAsLongSet(doc);
		final LongSet ids = newLongSetWithExpectedSize(parents.size() + ancestors.size());
		ids.addAll(parents);
		ids.addAll(ancestors);
		return ids;
	}
	
	@Override
	public LongSet getSubTypeStorageKeys(final IBranchPath branchPath, final String conceptId) {
		return getStorageKeys(branchPath, getSubTypesQuery(conceptId));
	}

	@Override
	public LongSet getAllSubTypeStorageKeys(final IBranchPath branchPath, final String conceptId) {
		return getStorageKeys(branchPath, getAllSubTypesQuery(conceptId));
	}

	private LongSet getIds(final IBranchPath branchPath, final Query query) {
		checkNotNull(query, "Query argument cannot be null.");
		try {
			final int resultSize = service.getTotalHitCount(branchPath, query);
			
			if (0 == resultSize) {
				return new LongOpenHashSet(); // guard against lower bound cannot be negative: 0
			}
			
			final LongSet ids = new LongOpenHashSet(resultSize, 1.0D); //optimized load factor
	
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
			
			final LongSet ids = newLongSetWithMurMur3Hash();
	
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

	@Override
	protected Set<String> getExtendedComponentFieldsToLoad() {
		return SnomedMappings.fieldsToLoad().fields(super.getExtendedComponentFieldsToLoad()).memberUuid().memberReferencedComponentId().memberReferencedComponentType().build();
	}
	
	@Override
	protected ExtendedComponent convertDocToExtendedComponent(IBranchPath branchPath, Document doc) {
		// if type field is null, then we are processing a reference set _member_
		final String iconId = doc.get(Mappings.iconId().fieldName());
		final List<Integer> types = Mappings.type().getValues(doc);
		final String id;
		
		if (types.isEmpty()) {
			id = Long.toString(SnomedMappings.memberReferencedComponentId().getValue(doc));
		} else {
			id = Long.toString(SnomedMappings.id().getValue(doc));
		}
		
		final short terminologyComponentId;
		
		if (types.isEmpty()) {
			terminologyComponentId = SnomedMappings.memberReferencedComponentType().getShortValue(doc);
		} else if (types.size() == 1) {
			// core SNOMED CT Component only
			terminologyComponentId = types.get(0).shortValue();
		} else {
			// concept + refset
			terminologyComponentId = SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
		}
		
		if (types.isEmpty()) {
			return new ExtendedComponentImpl(SnomedMappings.memberUuid().getValue(doc), id, iconId, SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER);
		} else {
			return new ExtendedComponentImpl(id, id, iconId, terminologyComponentId);
		}
	}

	@Override
	public LongKeyLongMap getConceptIdToStorageKeyMap(final IBranchPath branchPath) {
		final long[][] idPairs = getAllActiveConceptIdsStorageKeys(branchPath);
		
		if (CompareUtils.isEmpty(idPairs)) {
			return new LongKeyLongOpenHashMap();
		}
		
		final LongKeyLongOpenHashMap $ = new LongKeyLongOpenHashMap(idPairs.length);
		for (int i = 0; i < idPairs.length; i++) {
			$.put(idPairs[i][0], idPairs[i][1]);
		}
		
		return $;
	}
	
	@Override
	protected Query getRootConceptsQuery() {
		return SnomedMappings.newQuery().parent(SnomedMappings.ROOT_ID).active().matchAll();
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
	public boolean isUniqueId(final IBranchPath branchPath, final String componentId) {
		checkNotNull(componentId, "SNOMED CT core component ID argument cannot be null.");
		if (SnomedTerminologyComponentConstants.isCoreComponentId(componentId)) {
			return service.getHitCount(branchPath, SnomedMappings.newQuery().id(componentId).matchAll(), null) == 0;
		}
		return false;
	}
	
	@Override
	public boolean contains(final IBranchPath branchPath, final String expression, final String conceptId) {
		checkNotNull(conceptId, "SNOMED CT core component ID argument cannot be null.");
		checkNotNull(expression, "Query expression wrapper argument cannot be null.");
		
		if (EscgExpressionConstants.UNRESTRICTED_EXPRESSION.equals(expression)) {
			return true;
		}
		
		if (EscgExpressionConstants.REJECT_ALL_EXPRESSION.equals(expression)) {
			return false;
		}
		
		final IEscgQueryEvaluatorService escgService = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorService.class);
		
		try {
			
			final BooleanQuery query = escgService.evaluateBooleanQuery(branchPath, expression);
			query.add(SnomedMappings.newQuery().id(conceptId).matchAll(), Occur.MUST);
			return 0 < service.getHitCount(branchPath, query, null);
		
		} catch (final EscgParseFailedException e) {
			
			final LongCollection evaluateConceptIds = escgService.evaluateConceptIds(branchPath, expression);
			return evaluateConceptIds.contains(Long.valueOf(conceptId));
		}
	}
	
	@Override
	public int getDepth(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(conceptId, "conceptId");
		
		final long conceptIdL = Long.parseLong(conceptId);
		final LongSet allSuperTypeIds = getAllSuperTypeIds(branchPath, conceptIdL);
		if (LongSets.isEmpty(allSuperTypeIds)) {
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
	public boolean isLeaf(final IBranchPath branchPath, final String conceptId) {
		return service.getHitCount(branchPath, getAllSubTypesQuery(conceptId), null) < 1;
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypesById(final IBranchPath branchPath, final String id) {
		final Builder<SnomedConceptIndexEntry> builder = ImmutableList.builder();
		for (String superTypeId : getSuperTypeIds(branchPath, id)) {
			builder.add(getConcept(branchPath, superTypeId));
		}
		return builder.build();
	}

	@Override
	protected Query getSubTypesQuery(String id) {
		return SnomedMappings.newQuery().parent(id).and(getTerminologyComponentTypeQuery()).matchAll();
	}
	
	@Override
	public FilteredRefSetMemberBrowser2 createFilteredRefSetBrowser(final IBranchPath branchPath, 
			final long refSetId, 
			@Nullable final String filterExpression, 
			final boolean includeInactiveMembers, 
			final List<IRefSetMemberOperation> pendingOperations) {
		
		final FilteredRefSetMemberBrowser2Builder builder = new FilteredRefSetMemberBrowser2Builder(branchPath, 
				refSetId, 
				filterExpression, 
				includeInactiveMembers, 
				pendingOperations, 
				service, 
				this);
		
		return builder.build();
	}
	
	@Override
	protected short getConceptTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
	}

	@Override
	protected IComponentWithChildFlag<String> createComponentWithChildFlag(final SnomedConceptIndexEntry entry, final boolean hasChildren) {
		return new SnomedConceptIndexEntryWithChildFlag(entry, hasChildren);
	}
}
