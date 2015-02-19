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
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_ICON_ID;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_ID;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_PARENT;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_STORAGE_KEY;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_TYPE;
import static com.b2international.snowowl.datastore.index.IndexUtils.getLongValue;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_ANCESTOR;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EXHAUSTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PARENT;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.ROOT_ID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.graph.GraphUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.server.snomed.escg.EscgParseFailedException;
import com.b2international.snowowl.datastore.server.snomed.filteredrefset.FilteredRefSetMemberBrowser2Builder;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.EscgExpressionConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntryWithChildFlag;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.filteredrefset.FilteredRefSetMemberBrowser2;
import com.b2international.snowowl.snomed.datastore.filteredrefset.IRefSetMemberOperation;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptModelMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMemberNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedRelationshipNameProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Lucene based terminology browser implementation.
 * 
 */
public class SnomedServerTerminologyBrowser extends AbstractSnomedIndexTerminologyBrowser<SnomedConceptIndexEntry> implements SnomedTerminologyBrowser {

	private static final Set<String> FILEDS_TO_LOAD_FOR_EXTENDED_COMPONENT = unmodifiableSet(newHashSet(
			COMPONENT_ID, 
			COMPONENT_LABEL, 
			COMPONENT_ICON_ID,
			COMPONENT_TYPE,
			REFERENCE_SET_MEMBER_UUID));
	
	private static final Set<String> CONCEPT_FIELDS_TO_LOAD = ImmutableSet.of(COMPONENT_ID, 
			COMPONENT_LABEL, COMPONENT_ICON_ID,
			COMPONENT_STORAGE_KEY, CONCEPT_MODULE_ID,
			COMPONENT_ACTIVE, CONCEPT_PRIMITIVE,
			CONCEPT_EXHAUSTIVE, COMPONENT_RELEASED, CONCEPT_EFFECTIVE_TIME);
	
	private static final Set<String> ID_AND_PARENT_FIELDS_TO_LOAD = unmodifiableSet(newHashSet(COMPONENT_ID, CONCEPT_PARENT));
	private static final Set<String> PARENT_AND_ANCESTOR_FIELDS_TO_LOAD = unmodifiableSet(newHashSet(CONCEPT_PARENT, CONCEPT_ANCESTOR));
	
	private static final Set<String> DOI_FIELDS_TO_LOAD = ImmutableSet.of(CONCEPT_DEGREE_OF_INTEREST);
	private static final Set<String> STORAGE_KEY_FIELDS_TO_LOAD = ImmutableSet.of(COMPONENT_STORAGE_KEY);
	private static final Set<String> COMPONENT_ID_FILEDS_TO_LOAD = ImmutableSet.of(COMPONENT_ID);


	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.AbstractIndexTerminologyBrowser#createFilterTerminologyBrowserQuery(java.lang.String)
	 */
	@Override
	protected Query createFilterTerminologyBrowserQuery(final String expression) {
		return new SnomedConceptReducedQueryAdapter(expression, SnomedConceptReducedQueryAdapter.SEARCH_EVERYTHING).createQuery();
	}

	@Override
	protected Filter createComponentFilter(final String... componentIds) {
		if (CompareUtils.isEmpty(componentIds)) {
			return null;
		}
		
		final BytesRef[] bytesRefs = new BytesRef[componentIds.length];
		
		for (int i = 0; i < componentIds.length; i++) {
			bytesRefs[i] = IndexUtils.longToPrefixCoded(componentIds[i]);
		}
		
		return new CachingWrapperFilter(new TermsFilter(COMPONENT_ID, bytesRefs));
	}
	
	/**
	 * Class constructor.
	 * @param index service for the ontology. 
	 */
	public SnomedServerTerminologyBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}
	
	@Override
	protected SnomedConceptIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		final String id = doc.get(COMPONENT_ID);
		String label = doc.get(COMPONENT_LABEL);
		final String moduleId = doc.get(CONCEPT_MODULE_ID);
		final IndexableField storageKeyField = doc.getField(COMPONENT_STORAGE_KEY);
		final long storageKey = storageKeyField.numericValue().longValue();
		final String iconId = doc.get(COMPONENT_ICON_ID);
		final long effectiveTime = IndexUtils.getLongValue(doc.getField(CONCEPT_EFFECTIVE_TIME));
		
		final byte flags = SnomedConceptIndexEntry.generateFlags(IndexUtils.getBooleanValue(doc.getField(COMPONENT_ACTIVE)), 
					IndexUtils.getBooleanValue(doc.getField(CONCEPT_PRIMITIVE)),
					IndexUtils.getBooleanValue(doc.getField(CONCEPT_EXHAUSTIVE)),
					IndexUtils.getBooleanValue(doc.getField(COMPONENT_RELEASED)));
		// TODO: workaround for missing labels
		label = label == null ? "" : label;
		final SnomedConceptIndexEntry conceptMini = new SnomedConceptIndexEntry(id, moduleId, label, iconId, storageKey, flags, effectiveTime);
		return conceptMini;
	}

	@Override
	protected Set<String> getFieldNamesToLoad() {
		return CONCEPT_FIELDS_TO_LOAD;
	}

	@Override
	protected IndexQueryBuilder getConceptByIdQueryBuilder(final String key) {
		return new IndexQueryBuilder()
			.requireExactTerm(COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))
			.requireExactTerm(COMPONENT_ID, IndexUtils.longToPrefixCoded(key));
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getSuperTypesById(branchPath, concept.getId());
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.AbstractIndexTerminologyBrowser#getComponentIdTerm(java.lang.String)
	 */
	@Override
	protected Term getComponentIdTerm(final String componentId) {
		Preconditions.checkNotNull(componentId, "SNOMED CT concept ID argument cannot be null.");
		return new Term(COMPONENT_ID, IndexUtils.longToPrefixCoded(componentId));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getStorageKey(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public long getStorageKey(final IBranchPath branchPath, final String conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Concept ID argument cannot be null.");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);
		query.add(new TermQuery(getIdTerm(conceptId)), Occur.MUST);
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			
			return -1L;
			
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, COMPONENT_STORAGE_KEY_TO_LOAD);
		
		final IndexableField field = doc.getField(COMPONENT_STORAGE_KEY);
		
		if (null == field) {
			
			return -1L;
			
		}
		
		return IndexUtils.getLongValue(field);
	}
	
	private static final Set<String> COMPONENT_STORAGE_KEY_TO_LOAD = Sets.newHashSet(COMPONENT_STORAGE_KEY);
	
	@Override
	public List<SnomedConceptIndexEntry> getSubTypesAsList(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		return Lists.newArrayList(getSubTypes(branchPath, concept));
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getSubTypesById(branchPath, concept.getId());
	}

	public Collection<SnomedConceptIndexEntry> getAllSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getAllSubTypesById(branchPath, concept.getId());
	}
	
	public Collection<SnomedConceptIndexEntry> getAllSubTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		final Query query = getAllSubTypesQuery(branchPath, id);
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);
			final DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
			return createResultObjects(branchPath, docIdsIterator);
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving sub types of " + id + ".", e);
		}
	}

	private Query getAllSubTypesQuery(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		final BooleanQuery query = new BooleanQuery();
		final Query parentQuery = getSubTypesQueryBuilder(id).toQuery();
		query.add(parentQuery, Occur.SHOULD);
		final Query ancestorQuery = new TermQuery(new Term(CONCEPT_ANCESTOR, IndexUtils.longToPrefixCoded(id)));
		query.add(ancestorQuery, Occur.SHOULD);
		return query;
	}
	
	public Collection<SnomedConceptIndexEntry> getAllSuperTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getAllSuperTypesById(branchPath, concept.getId());
	}

	public Collection<SnomedConceptIndexEntry> getAllSuperTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		final Query query = getConceptByIdQueryBuilder(id).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, 
				Sets.newHashSet(CONCEPT_PARENT, CONCEPT_ANCESTOR));
		final IndexableField[] parentFields = document.getFields(CONCEPT_PARENT);
		final IndexableField[] ancestorFields = document.getFields(CONCEPT_ANCESTOR);
		final Builder<SnomedConceptIndexEntry> builder = ImmutableList.builder();
		for (final IndexableField parentField : parentFields) {
			if (ROOT_ID != parentField.numericValue().longValue()) {
				builder.add(getConcept(branchPath, parentField.stringValue()));
			}
		}
		for (final IndexableField ancestorField : ancestorFields) {
			if (ROOT_ID != ancestorField.numericValue().longValue()) {
				builder.add(getConcept(branchPath, ancestorField.stringValue()));
			}
		}
		return builder.build();
	}
	
	public int getAllSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getAllSubTypeCountById(branchPath, concept.getId());
	}

	public int getAllSubTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		final Query query = getAllSubTypesQuery(branchPath, id);
		try {
			return getQueryResultCount(branchPath, query);
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving the number of all sub types of " + id + ".", e);
		}
	}
	
	public int getSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSubTypeCountById(branchPath, concept.getId());
	}

	public int getAllSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getAllSuperTypeCountById(branchPath, concept.getId());
	}

	public int getAllSuperTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		// TODO: improve naive implementation
		return getAllSuperTypesById(branchPath, id).size();
	}
	
	public int getSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(concept, "Concept must not be null.");
		return getSuperTypeCountById(branchPath, concept.getId());
	}
	
	public int getSuperTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
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
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(superTypeId, "Super type ID must not be null.");
		checkNotNull(subTypeId, "Sub type ID must not be null.");
		final Query query = getConceptByIdQueryBuilder(subTypeId).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		if (topDocs.scoreDocs.length > 0) {
			final int docId = topDocs.scoreDocs[0].doc;
			final Document document = service.document(branchPath, docId, 
					ImmutableSet.of(CONCEPT_ANCESTOR, CONCEPT_PARENT));
			final IndexableField[] parentFields = document.getFields(CONCEPT_PARENT);
			final IndexableField[] ancestorFields = document.getFields(CONCEPT_ANCESTOR);
			final Iterable<IndexableField> allSuperTypeFields = Iterables.concat(Arrays.asList(parentFields), Arrays.asList(ancestorFields));
			for (final IndexableField superTypeField : allSuperTypeFields) {
				if (superTypeId.equals(superTypeField.stringValue()))
					return true;
			}
			return false;
		} else {
			throw new IllegalArgumentException("Can't find concept " + subTypeId);
		}
	}
	
	@Override
	public SnomedConceptIndexEntry getTopLevelConcept(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(concept, "Concept must not be null.");
		final Collection<SnomedConceptIndexEntry> allSuperTypes = getAllSuperTypes(branchPath, concept);
		final Collection<SnomedConceptIndexEntry> rootConcepts = getRootConcepts(branchPath);
		for (final SnomedConceptIndexEntry conceptMini : allSuperTypes) {
			if (rootConcepts.contains(conceptMini))
				return conceptMini;
		}
		throw new IllegalStateException("Top level parent can't be determined for: " + concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ConceptProvider#getConcepts()
	 */
	@Override
	public Iterable<SnomedConceptIndexEntry> getConcepts(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath, getAllConceptsQuery(), collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying all concepts.", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ConceptProvider#getConceptCount()
	 */
	@Override
	public int getConceptCount(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		try {
			return getQueryResultCount(branchPath, getAllConceptsQuery());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying concept count.", e);
		}
	}

	private Query getAllConceptsQuery() {
		return new TermQuery(new Term(COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getConceptDoi(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public float getConceptDoi(final IBranchPath branchPath, final String key) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(key, "Key must not be null.");
		final Query conceptQuery = getConceptByIdQueryBuilder(key).toQuery(); 
		final TopDocs topDocs = service.search(branchPath, conceptQuery, 1);
		
		if (topDocs.totalHits < 1) {
			return SnomedConceptModelMappingStrategy.DEFAULT_DOI;
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, DOI_FIELDS_TO_LOAD);
		return IndexUtils.getFloatValue(document.getField(CONCEPT_DEGREE_OF_INTEREST));
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getConcepts(final IBranchPath branchPath, final Iterable<String> ids) {
		checkNotNull(branchPath, "branchPath");
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
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getAllSubTypeIds(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public LongSet getAllSubTypeIds(final IBranchPath branchPath, final long conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		return getIds(branchPath, getAllSubTypesQuery(branchPath, String.valueOf(conceptId)));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getSubTypeIds(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public LongSet getSubTypeIds(final IBranchPath branchPath, final long conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		return getIds(branchPath, getSubTypesQueryBuilder(Long.toString(conceptId)).toQuery());
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getSuperTypeIds(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public LongSet getSuperTypeIds(final IBranchPath branchPath, final long conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final Query query = getConceptByIdQueryBuilder(Long.toString(conceptId)).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return new LongOpenHashSet();
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, Sets.newHashSet(CONCEPT_PARENT));
		final IndexableField[] parentFields = document.getFields(CONCEPT_PARENT);
		final LongSet ids = new LongOpenHashSet(parentFields.length);

		for (final IndexableField parentField : parentFields) {
			if (ROOT_ID != parentField.numericValue().longValue()) {
				ids.add(parentField.numericValue().longValue());
			}
		}
		
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getAllSuperTypeIds(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public LongSet getAllSuperTypeIds(final IBranchPath branchPath, final long conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final Query query = getConceptByIdQueryBuilder(Long.toString(conceptId)).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return new LongOpenHashSet();
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, 
				PARENT_AND_ANCESTOR_FIELDS_TO_LOAD);
		
		final IndexableField[] parentFields = document.getFields(CONCEPT_PARENT);
		final IndexableField[] ancestorFields = document.getFields(CONCEPT_ANCESTOR);
		final LongSet ids = newLongSetWithExpectedSize(parentFields.length + ancestorFields.length);

		for (final IndexableField parentField : parentFields) {
			if (ROOT_ID != parentField.numericValue().longValue()) {
				ids.add(Long.parseLong(parentField.stringValue()));
			}
		}
		
		for (final IndexableField ancestorField : ancestorFields) {
			if (ROOT_ID != ancestorField.numericValue().longValue()) {
				ids.add(Long.parseLong(ancestorField.stringValue()));
			}
		}
		
		return ids;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getSubTypeStorageKeys(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public LongSet getSubTypeStorageKeys(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		return getStorageKeys(branchPath, getSubTypesQueryBuilder(conceptId).toQuery());
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getAllSubTypeStorageKeys(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public LongSet getAllSubTypeStorageKeys(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		return getStorageKeys(branchPath, getAllSubTypesQuery(branchPath, conceptId));
	}

	private LongSet getIds(final IBranchPath branchPath, final Query query) {
		checkNotNull(query, "Query argument cannot be null.");
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
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
				ids.add(Long.valueOf(service.document(branchPath, itr.getDocID(), COMPONENT_ID_FILEDS_TO_LOAD).get(COMPONENT_ID)));
			}
			
			return ids;
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	private LongSet getStorageKeys(final IBranchPath branchPath, final Query query) {
		checkNotNull(query, "Query argument cannot be null.");
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		try {
			
			final LongSet ids = newLongSetWithMurMur3Hash();
	
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			while (itr.next()) {
				ids.add(Long.valueOf(service.document(branchPath, itr.getDocID(), STORAGE_KEY_FIELDS_TO_LOAD).get(COMPONENT_STORAGE_KEY)));
			}
			
			return ids;
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getAllActiveConceptIds(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public LongCollection getAllActiveConceptIds(final IBranchPath branchPath) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();
			
			final BooleanQuery query = new BooleanQuery();
			query.add(new TermQuery(new Term(COMPONENT_TYPE, 
					IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);

			query.add(new TermQuery(new Term(
					COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
			
			final LongDocValuesCollector collector = new LongDocValuesCollector(COMPONENT_ID);
			
			service.search(branchPath, query, collector);

			return collector.getValues();
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != searcher)
				
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
	
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getAllConceptIds(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public LongCollection getAllConceptIds(final IBranchPath branchPath) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();
			
			final Query query = new TermQuery(new Term(COMPONENT_TYPE, 
					IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)));

		
			final LongDocValuesCollector collector = new LongDocValuesCollector(COMPONENT_ID);
			
			service.search(branchPath, query, collector);

			return collector.getValues();
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != searcher)
				
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
	
		
	}
	
	@Override
	public long[][] getAllConceptIdsStorageKeys(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();
			
			final Query query = new TermQuery(new Term(COMPONENT_TYPE, 
					IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)));
			final ConceptIdStorageKeyCollector collector = new ConceptIdStorageKeyCollector();
			
			service.search(branchPath, query, collector);
			
			return collector.getIds();
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != searcher)
				
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getAllConceptIdsStorageKeys(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public long[][] getAllActiveConceptIdsStorageKeys(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();
			
			final BooleanQuery query = new BooleanQuery();
			query.add(new TermQuery(new Term(COMPONENT_TYPE, 
					IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);

			query.add(new TermQuery(new Term(
					COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
			
			final ConceptIdStorageKeyCollector collector = new ConceptIdStorageKeyCollector();
			
			service.search(branchPath, query, collector);

			return collector.getIds();
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
		
		} finally {
			
			if (null != searcher)
				
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getExtendedComponent(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public ExtendedComponent getExtendedComponent(final IBranchPath branchPath, final long storageKey) {
		
		checkNotNull(branchPath, "branchPath");
		checkArgument(storageKey > CDOUtils.NO_STORAGE_KEY);
		
		final TermQuery query = new TermQuery(new Term(COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(storageKey)));
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, FILEDS_TO_LOAD_FOR_EXTENDED_COMPONENT);
		
		//if type field is null, then we are processing a reference set 
		final IndexableField typeField = doc.getField(COMPONENT_TYPE);
		
		if (null == typeField) {

			final String uuid = doc.get(REFERENCE_SET_MEMBER_UUID);
			return new ExtendedComponentImpl(
					uuid, 
					SnomedRefSetMemberNameProvider.INSTANCE.getComponentLabel(branchPath, uuid),
					"",
					REFSET_MEMBER_NUMBER);
			
		} else {
			
			String label = doc.get(COMPONENT_LABEL);
			final String id = doc.get(COMPONENT_ID);
			final short terminologyComponentId = IndexUtils.getShortValue(typeField);
			if (null == label) {
				if (SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER == terminologyComponentId) {
					label = SnomedRelationshipNameProvider.INSTANCE.getComponentLabel(branchPath, id);
				}
			}
			
			return new ExtendedComponentImpl(
					id, 
					label,
					doc.get(COMPONENT_ICON_ID), 
					terminologyComponentId);
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#getConceptIdToStorageKeyMap(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public LongKeyLongMap getConceptIdToStorageKeyMap(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
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
	
	protected IndexQueryBuilder getRootConceptsQueryBuilder() {
		return new IndexQueryBuilder()
			.requireExactTerm(CONCEPT_PARENT, IndexUtils.longToPrefixCoded(ROOT_ID))
			.requireExactTerm(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1));
	}

	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final Query query = getConceptByIdQueryBuilder(componentId).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, Sets.newHashSet(COMPONENT_PARENT));
		final IndexableField[] parentFields = document.getFields(COMPONENT_PARENT);
		final String[] parentIds = new String[parentFields.length];
		int i = 0;
		for (final IndexableField parentField : parentFields) {
			if (ROOT_ID != parentField.numericValue().longValue()) {
				parentIds[i++] = parentField.stringValue();
			}
		}
		return Arrays.asList(Arrays.copyOf(parentIds, i));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser#isUniqueId(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public boolean isUniqueId(final IBranchPath branchPath, final String componentId) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "SNOMED CT core component ID argument cannot be null.");
		
		final short componentType = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId);
		
		Query componentTypeQuery = null;
		
		switch (componentType) {
			
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER: //$FALL-THROUGH$
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER: //$FALL-THROUGH$
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				componentTypeQuery = new TermQuery(new Term(COMPONENT_TYPE, IndexUtils.intToPrefixCoded(componentType)));
				break;
			default: 
				return false;
			
		}
		
		Preconditions.checkNotNull(componentTypeQuery, "Cannot create component type query for ID uniqueness check. ID: " + componentId + " type: " + componentType);
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(componentTypeQuery, Occur.MUST);
		query.add(new TermQuery(new Term(COMPONENT_ID, IndexUtils.longToPrefixCoded(componentId))), Occur.MUST);
		
		final int hitCount = service.getHitCount(branchPath, query, /*intentionally null*/null);
		
		return hitCount == 0; //ID does not exist in index.
	}
	
	@Override
	public boolean contains(final IBranchPath branchPath, final String expression, final String conceptId) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
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
			query.add(new TermQuery(new Term(COMPONENT_ID, IndexUtils.longToPrefixCoded(conceptId))), Occur.MUST);
			return 0 < service.getHitCount(branchPath, query, null);
		
		} catch (final EscgParseFailedException e) {
			
			final LongCollection evaluateConceptIds = escgService.evaluateConceptIds(branchPath, expression);
			return evaluateConceptIds.contains(Long.valueOf(conceptId));
		}
	}
	
	@Override
	public int getDepth(final IBranchPath branchPath, final String conceptId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptId, "conceptId");
		
		final long conceptIdL = Long.parseLong(conceptId);
		final LongSet allSuperTypeIds = getAllSuperTypeIds(branchPath, conceptIdL);
		if (LongSets.isEmpty(allSuperTypeIds)) {
			return 0;
		}
		
		final Multimap<Long, Long> parentageMap = Multimaps.synchronizedMultimap(HashMultimap.<Long, Long>create());
		parentageMap.putAll(conceptIdL, toSet(allSuperTypeIds));
		parallelForEach(allSuperTypeIds, new LongSets.LongCollectionProcedure() {
			public void apply(long conceptId) {
				final LongSet superTypeIds = getSuperTypeIds(branchPath, conceptId);
				parentageMap.putAll(conceptId, toSet(superTypeIds));
			}
		});
		
		return GraphUtils.getLongestPath(parentageMap).size() - 1;
		
	}
	
	@Override
	public int getHeight(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptId, "conceptId");
		
		final Query query = getAllSubTypesQuery(branchPath, conceptId);

		final AtomicReference<IndexSearcher> searcher = new AtomicReference<IndexSearcher>();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		ReferenceManager<IndexSearcher> manager = null;

		try {

			final Multimap<Long, Long> parentageMap = Multimaps.synchronizedMultimap(HashMultimap.<Long, Long>create());
			manager = service.getManager(branchPath);
			searcher.set(manager.acquire());
			IndexUtils.parallelForEachDocId(collector.getDocIDs(), new IndexUtils.DocIdProcedure() {
				public void apply(final int docId) throws IOException {
					final Document doc = searcher.get().doc(docId, ID_AND_PARENT_FIELDS_TO_LOAD);
					final long id = getLongValue(doc.getField(COMPONENT_ID));
					final IndexableField[] parentIdFields = doc.getFields(CONCEPT_PARENT);
					final Long[] parentIds = new Long[parentIdFields.length];
					int i = 0;
					for (final IndexableField parentIdField : parentIdFields) {
						parentIds[i++] = getLongValue(parentIdField);
					}
					parentageMap.putAll(id, Arrays.asList(parentIds));
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
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptId, "conceptId");
		final Query query = getAllSubTypesQuery(branchPath, conceptId);
		return service.getHitCount(branchPath, query, null) < 1;
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getConceptByIdQueryBuilder(id).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, Sets.newHashSet(CONCEPT_PARENT));
		final IndexableField[] parentFields = document.getFields(CONCEPT_PARENT);
		final Builder<SnomedConceptIndexEntry> builder = ImmutableList.builder();
		for (final IndexableField parentField : parentFields) {
			if (ROOT_ID != parentField.numericValue().longValue()) {
				builder.add(getConcept(branchPath, parentField.stringValue()));
			}
		}
		return builder.build();
	}

	@Override
	protected IndexQueryBuilder getSubTypesQueryBuilder(final String id) {
		return new IndexQueryBuilder()
			.require(getTerminologyComponentTypeQuery())
			.requireExactTerm(CONCEPT_PARENT, IndexUtils.longToPrefixCoded(id));
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