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

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.commons.pcj.LongSets.parallelForEach;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.b2international.snowowl.datastore.index.IndexUtils.getLongValue;
import static com.b2international.snowowl.datastore.index.IndexUtils.intToPrefixCoded;
import static com.b2international.snowowl.datastore.index.IndexUtils.longToPrefixCoded;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.apache.lucene.search.MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.eclipse.core.runtime.IProgressMonitor;

import bak.pcj.LongIterator;
import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.field.ComponentIdLongField;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexQueries;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

/**
 * Lucene based reference set browser implementation.
 * 
 */
public class SnomedServerRefSetBrowser extends AbstractSnomedIndexBrowser<SnomedRefSetIndexEntry> implements SnomedRefSetBrowser {

	private static final Set<String> MAPPING_REFERENCE_SET_ID_FIELD = Collections.unmodifiableSet(Sets.newHashSet(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID));
	private static final Set<String> REFERENCE_SET_ID_FIELD = Collections.unmodifiableSet(Sets.newHashSet(CONCEPT_REFERRING_REFERENCE_SET_ID));
	private static final Set<String> COMPONENT_ID_FIELD = Collections.unmodifiableSet(Sets.newHashSet(ComponentIdLongField.COMPONENT_ID));
	private static final Set<String> COMPONENT_STORAGE_KEY_TO_LOAD = Sets.newHashSet(CommonIndexConstants.COMPONENT_STORAGE_KEY);
	private static final Set<String> REFERENCE_SET_TYPE_FIELD = Collections.unmodifiableSet(Sets.newHashSet(REFERENCE_SET_TYPE));

	private static final Set<String> FILEDS_TO_LOAD_FOR_EXTENDED_COMPONENT = unmodifiableSet(newHashSet(
			ComponentIdLongField.COMPONENT_ID, 
			CommonIndexConstants.COMPONENT_LABEL, 
			CommonIndexConstants.COMPONENT_ICON_ID));
	
	private static final ImmutableSet<String> FIELD_NAMES_TO_LOAD = ImmutableSet.of(ComponentIdLongField.COMPONENT_ID, 
			CommonIndexConstants.COMPONENT_LABEL, 
			REFERENCE_SET_REFERENCED_COMPONENT_TYPE, 
			REFERENCE_SET_TYPE,
			CommonIndexConstants.COMPONENT_STORAGE_KEY,
			REFERENCE_SET_STRUCTURAL,
			CommonIndexConstants.COMPONENT_ICON_ID,
			COMPONENT_MODULE_ID);

	private static final Set<String> MEMBER_QUERY_FIELDS_TO_LOAD = unmodifiableSet(newHashSet(REFERENCE_SET_MEMBER_QUERY));
	private static final Set<String> MAP_TARGET_COMPONENT_ID_FIELDS_TO_LOAD = unmodifiableSet(newHashSet(
	REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID,
	REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID));
	private static final Set<String> REF_SET_MEMBERSHIP_FIELDS_TO_LOAD = unmodifiableSet(newHashSet(
	CONCEPT_REFERRING_REFERENCE_SET_ID,
	CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID,
	ComponentIdLongField.COMPONENT_ID
	));

	protected final class RefSetTypeToConceptFunction implements Function<SnomedRefSetType, SnomedConceptIndexEntry> {
		private final IBranchPath branchPath;
		RefSetTypeToConceptFunction(final IBranchPath branchPath) {
			this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		}
		@Override public SnomedConceptIndexEntry apply(final SnomedRefSetType type) {
			return getTerminologyBrowser().getConcept(branchPath, SnomedRefSetUtil.getConceptId(type));
		}
	}
	
	
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}

	public SnomedServerRefSetBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}
	
	@Override
	public int getMemberCount(final IBranchPath branchPath, final String refSetId) {
		final TotalHitCountCollector collector = new TotalHitCountCollector();
		final TermQuery refSetMemberByRefSetIdQuery = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)));
		service.search(branchPath, refSetMemberByRefSetIdQuery, collector);
		return collector.getTotalHits();
	}
	
	@Override
	public int getActiveMemberCount(final IBranchPath branchPath, final String refSetId) {
		final TotalHitCountCollector collector = new TotalHitCountCollector();
		final Query query = new IndexQueryBuilder().
			requireExactTerm(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1)).
			requireExactTerm(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)).toQuery();
		service.search(branchPath, query, collector);
		return collector.getTotalHits();
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getMemberConcepts(final IBranchPath branchPath, final String refSetId) {
		final TermQuery refSetMemberByRefSetIdQuery = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)));
		final TermQuery activeQuery = new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1)));
		final BooleanQuery query = new BooleanQuery();
		query.add(refSetMemberByRefSetIdQuery, Occur.MUST);
		query.add(activeQuery, Occur.MUST);
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		try {
			final DocIdsIterator scoredDocIdsIterator = collector.getDocIDs().iterator();
			final Set<SnomedConceptIndexEntry> concepts = Sets.newHashSet();
			while (scoredDocIdsIterator.next()) {
				final int docId = scoredDocIdsIterator.getDocID();
				final Document document = service.document(branchPath, docId, ImmutableSet.of(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID));
				final String referencedConceptId = document.get(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID);
				concepts.add(getTerminologyBrowser().getConcept(branchPath, referencedConceptId));
			}
			return concepts;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IRefSetBrowser#getMemberConceptIds(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object)
	 */
	@Override
	public Collection<String> getMemberConceptIds(final IBranchPath branchPath, final String refSetId) {
		
		final TermQuery refSetMemberByRefSetIdQuery = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)));
		final TermQuery activeQuery = new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1)));
		final BooleanQuery query = new BooleanQuery();
		query.add(refSetMemberByRefSetIdQuery, Occur.MUST);
		query.add(activeQuery, Occur.MUST);
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		try {
			final DocIdsIterator scoredDocIdsIterator = collector.getDocIDs().iterator();
			final Set<String> conceptIds = Sets.newHashSet();
			while (scoredDocIdsIterator.next()) {
				final int docId = scoredDocIdsIterator.getDocID();
				final Document document = service.document(branchPath, docId, ImmutableSet.of(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID));
				final String referencedConceptId = document.get(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID);
				conceptIds.add(referencedConceptId);
			}
			return conceptIds;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#isActiveMemberOf(com.b2international.snowowl.core.api.IBranchPath, long, long)
	 */
	@Override
	public boolean isActiveMemberOf(final IBranchPath branchPath, final long identifierConceptId, final long conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);
		query.add(new ComponentIdLongField(conceptId).toQuery(), Occur.MUST);
		query.add(new TermQuery(new Term(CONCEPT_REFERRING_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(identifierConceptId))), Occur.MUST);
		
		return service.getHitCount(branchPath, query, null) > 0;
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getComponentLabel(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component ID argument cannot be null.");
		
		return getTerminologyBrowser().getComponentLabel(branchPath, componentId);
	}
	
	@Override
	public SnomedRefSetIndexEntry getRefSet(final IBranchPath branchPath, final String refSetId) {
		final TopDocs topDocs = service.search(branchPath, getRefSetByIdQuery(refSetId), 1);
		return createSingleResultObject(branchPath, topDocs);
	}

	@Override
	public Iterable<SnomedRefSetIndexEntry> getRefsSets(final IBranchPath branchPath) {
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath, new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, 
					IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER))), collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying all statements.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getTypeOrdinal(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public int getTypeOrdinal(final IBranchPath branchPath, final String refSetId) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(refSetId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		
		final BooleanQuery query = new BooleanQuery(true);

		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER))), Occur.MUST);
		query.add(new ComponentIdLongField(refSetId).toQuery(), Occur.MUST);
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1;
		}
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		
		final Document doc = service.document(branchPath, scoreDoc.doc, REFERENCE_SET_TYPE_FIELD);
		
		final IndexableField field = doc.getField(REFERENCE_SET_TYPE);
		
		if (null == field) {
			return -1;
		}
		
		return IndexUtils.getIntValue(field);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IRefSetBrowser#getAllRefSetIds(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public Collection<String> getAllRefSetIds(final IBranchPath branchPath) {
		
		final TermQuery query = new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER)));
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		
		final Collection<String> $ = Sets.newHashSet();

		try {
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, COMPONENT_ID_FIELD);
				$.add(ComponentIdLongField.getString(doc));
			}
		} catch (final IOException e) {
			throw new IndexException("Error while getting all reference set identifier concept IDs.", e);
		}
		
		return Collections.unmodifiableCollection($);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getMemberStorageKey(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public long getMemberStorageKey(final IBranchPath branchPath, final String uuid) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Reference set member UUID argument cannot be null.");
		
		final Query query = new TermQuery(new Term(REFERENCE_SET_MEMBER_UUID, uuid));
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			
			return -1L;
			
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, COMPONENT_STORAGE_KEY_TO_LOAD);
		
		final IndexableField field = doc.getField(CommonIndexConstants.COMPONENT_STORAGE_KEY);
		
		if (null == field) {
			
			return -1L;
			
		}
		
		return IndexUtils.getLongValue(field);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getStorageKey(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public long getStorageKey(final IBranchPath branchPath, final String identifierConceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Concept ID argument cannot be null.");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER))), Occur.MUST);
		query.add(getComponentIdQuery(identifierConceptId), Occur.MUST);
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			
			return -1L;
			
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, COMPONENT_STORAGE_KEY_TO_LOAD);
		
		final IndexableField field = doc.getField(CommonIndexConstants.COMPONENT_STORAGE_KEY);
		
		if (null == field) {
			
			return -1L;
			
		}
		
		return IndexUtils.getLongValue(field);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getIdentifierId(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public String getIdentifierId(final IBranchPath branchPath, final long storageKey) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final Query query = new TermQuery(new Term(CommonIndexConstants.COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(storageKey)));
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return null;
		}
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		final Document doc = service.document(branchPath, scoreDoc.doc, COMPONENT_ID_FIELD);
		return ComponentIdLongField.getString(doc);
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getRootConcepts(final IBranchPath branchPath) {
		final int size = SnomedRefSetType.values().length;
		final Collection<SnomedConceptIndexEntry> roots = Lists.newArrayListWithExpectedSize(size);
		
		final RefSetTypeToConceptFunction function = new RefSetTypeToConceptFunction(branchPath);
		for (final SnomedRefSetType type : SnomedRefSetUtil.getTypesForUI()) {
			
			
			//workaround to avoid having such collection when SNOMED CT is not available
			//[null, null, null, null, null, null]
			final SnomedConceptIndexEntry rootConcept = function.apply(type); //can be null
			if (null != rootConcept) {
				roots.add(rootConcept);
			}
			
		}
		
		return roots;
	}

	@Override
	public SnomedConceptIndexEntry getConcept(final IBranchPath branchPath, final String id) {
		return getTerminologyBrowser().getConcept(branchPath, id);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		return getSuperTypesById(branchPath, concept.getId());
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		return getSubTypesById(branchPath, concept.getId());
	}

	@Override
	public List<SnomedConceptIndexEntry> getSubTypesAsList(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypesById(final IBranchPath branchPath, final String id) {
		return getTerminologyBrowser().getSuperTypesById(branchPath, id);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypesById(final IBranchPath branchPath, final String id) {
		// refset identifier concept subtypes
		final Collection<SnomedConceptIndexEntry> identifierConceptSubTypes = getTerminologyBrowser().getSubTypesById(branchPath, id);
		final Iterable<SnomedRefSetIndexEntry> refSets = getRefsSets(branchPath);
		final HashSet<String> refSetIdSet = Sets.newHashSet(Iterables.transform(refSets, new Function<SnomedRefSetIndexEntry, String>() {
			@Override public String apply(final SnomedRefSetIndexEntry input) {
				return input.getId();
			}
		}));
		final List<SnomedConceptIndexEntry> subRefSets = new ArrayList<SnomedConceptIndexEntry>();
		// look for known identifier concepts in the subtypes
		for(final SnomedConceptIndexEntry conceptMini : identifierConceptSubTypes){
			if(refSetIdSet.contains(conceptMini.getId())){
				subRefSets.add(conceptMini);
			}
		}
		return subRefSets;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getContainerRefSetIds(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public Collection<String> getContainerRefSetIds(final IBranchPath branchPath, final String conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");
		
		//if not a concept ID, we do nothing.
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
			
			return Collections.emptySet();
			
		}
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);
		query.add(new ComponentIdLongField(conceptId).toQuery(), Occur.MUST);
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptySet();
		}
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		
		final Document doc = service.document(branchPath, scoreDoc.doc, REFERENCE_SET_ID_FIELD);
		
		final IndexableField[] fields = doc.getFields(CONCEPT_REFERRING_REFERENCE_SET_ID);
		
		if (CompareUtils.isEmpty(fields)) {
			return Collections.emptySet();
		}
		
		final String[] refSetIds = new String[fields.length];
		
		int i = 0;
		for (final IndexableField field : fields) {
			refSetIds[i++] = field.stringValue();
		}
		
		
		return Arrays.asList(refSetIds);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getContainerMappingRefSetIds(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public Collection<String> getContainerMappingRefSetIds(final IBranchPath branchPath, final String conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");
		
		//if not a concept ID, we do nothing.
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
			
			return Collections.emptySet();
			
		}
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);
		query.add(new ComponentIdLongField(conceptId).toQuery(), Occur.MUST);
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptySet();
		}
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		
		final Document doc = service.document(branchPath, scoreDoc.doc, MAPPING_REFERENCE_SET_ID_FIELD);
		
		final IndexableField[] fields = doc.getFields(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID);
		
		if (CompareUtils.isEmpty(fields)) {
			return Collections.emptySet();
		}
		
		final String[] refSetIds = new String[fields.length];
		
		int i = 0;
		for (final IndexableField field : fields) {
			refSetIds[i++] = field.stringValue();
		}
		
		
		return Arrays.asList(refSetIds);
	}
	
	@Override
	public LongSet getPublishedModuleDependencyMembers(final IBranchPath branchPath, final String id) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(id, "ID argument cannot be null.");

		final BooleanQuery query = new BooleanQuery(true);
		
		final BooleanQuery moduleQuery = new BooleanQuery(true);

		final BooleanQuery sourceModuleQuery = new BooleanQuery(true);
		sourceModuleQuery.add(new TermQuery(new Term(COMPONENT_MODULE_ID, IndexUtils.longToPrefixCoded(id))), Occur.MUST);
		sourceModuleQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME, IndexUtils.longToPrefixCoded(EffectiveTimes.UNSET_EFFECTIVE_TIME))), Occur.MUST_NOT);
		
		final BooleanQuery targetModuleQuery = new BooleanQuery(true);
		targetModuleQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, id)), Occur.MUST);
		targetModuleQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME, IndexUtils.longToPrefixCoded(EffectiveTimes.UNSET_EFFECTIVE_TIME))), Occur.MUST_NOT);
		
		moduleQuery.add(sourceModuleQuery, Occur.SHOULD);
		moduleQuery.add(targetModuleQuery, Occur.SHOULD);

		query.add(moduleQuery, Occur.MUST);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(Concepts.REFSET_MODULE_DEPENDENCY_TYPE))), Occur.MUST);
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		
		final LongSet storageKeys = new LongOpenHashSet();

		try {
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, COMPONENT_STORAGE_KEY_TO_LOAD);
				storageKeys.add(IndexUtils.getLongValue(doc.getField(CommonIndexConstants.COMPONENT_STORAGE_KEY)));
			}
		} catch (final IOException e) {
			throw new IndexException("Error while querying module dependency reference set members.", e);
		}
	
		return storageKeys;
	}
	
	@Override
	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String conceptId) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypesById(final IBranchPath branchPath, final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypesById(final IBranchPath branchPath, final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getSubTypeCountById(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object)
	 */
	@Override
	public int getSubTypeCountById(final IBranchPath branchPath, final String id) {
		return getTerminologyBrowser().getSubTypeCountById(branchPath, id);
	}

	@Override
	public int getAllSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCountById(final IBranchPath branchPath, final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSuperTypeCountById(final IBranchPath branchPath, final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCountById(final IBranchPath branchPath, final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getExtendedComponent(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public ExtendedComponent getExtendedComponent(final IBranchPath branchPath, final long storageKey) {
		
		checkNotNull(branchPath, "branchPath");
		checkArgument(storageKey > CDOUtils.NO_STORAGE_KEY);
		
		final TermQuery query = new TermQuery(new Term(CommonIndexConstants.COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(storageKey)));
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, FILEDS_TO_LOAD_FOR_EXTENDED_COMPONENT);
		
		return new ExtendedComponentImpl(
				ComponentIdLongField.getString(doc), 
				doc.get(CommonIndexConstants.COMPONENT_LABEL),
				doc.get(CommonIndexConstants.COMPONENT_ICON_ID), 
				SnomedTerminologyComponentConstants.REFSET_NUMBER);
		
	}

	@Override
	public boolean isRegularRefSet(final IBranchPath branchPath, final long storageKey) {
		checkNotNull(branchPath, "branchPath");
		checkArgument(storageKey > NO_STORAGE_KEY, "Storage key should be a positive integer.");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER))), Occur.MUST);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(storageKey))), Occur.MUST);
		query.add(new TermQuery(new Term(REFERENCE_SET_STRUCTURAL, IndexUtils.intToPrefixCoded(0))), Occur.MUST);
		
		return service.getHitCount(branchPath, query, null) > 0;
	}
	
	@Override
	protected Set<String> getFieldNamesToLoad() {
		return FIELD_NAMES_TO_LOAD;
	}

	@Override
	protected SnomedRefSetIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		final String id = ComponentIdLongField.getString(doc);
		final String label = doc.get(CommonIndexConstants.COMPONENT_LABEL);
		final IndexableField referencedComponentTypeField = doc.getField(REFERENCE_SET_REFERENCED_COMPONENT_TYPE);
		final short referencedComponentType = referencedComponentTypeField.numericValue().shortValue();
		final IndexableField referenceSetTypeField = doc.getField(REFERENCE_SET_TYPE);
		final SnomedRefSetType refSetType = SnomedRefSetType.get(referenceSetTypeField.numericValue().intValue());
		final long storageKey = doc.getField(CommonIndexConstants.COMPONENT_STORAGE_KEY).numericValue().longValue();
		final boolean structural = IndexUtils.getBooleanValue(doc.getField(REFERENCE_SET_STRUCTURAL));
		final String iconId = doc.get(CommonIndexConstants.COMPONENT_ICON_ID);
		final String moduleId = doc.get(COMPONENT_MODULE_ID);
		
		return new SnomedRefSetIndexEntry(
				id, 
				label, 
				iconId,
				moduleId,
				0.0F, 
				storageKey, 
				refSetType, 
				referencedComponentType, 
				structural);
	}

	private Query getRefSetByIdQuery(final String refSetId) {
		final TermQuery idQuery = new ComponentIdLongField(refSetId).toQuery();
		final TermQuery componentTypeQuery = new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, 
				IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER)));
		final BooleanQuery query = new BooleanQuery();
		query.add(idQuery, Occur.MUST);
		query.add(componentTypeQuery, Occur.MUST);
		return query;
	}

	@Override
	public SnomedConceptIndexEntry getTopLevelConcept(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isTerminologyAvailable(final IBranchPath branchPath) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#isSuperTypeOf(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isSuperTypeOf(final IBranchPath branchPath, final SnomedConceptIndexEntry superType, final SnomedConceptIndexEntry subType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#isSuperTypeOfById(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isSuperTypeOfById(final IBranchPath branchPath, final String superTypeId, final String subTypeId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IRefSetBrowser#isReferenced(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReferenced(final IBranchPath branchPath, final String refSetId, final String componentId) {
		final TermQuery referencingRefSetMembersQuery = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, componentId));
		final TermQuery refSetMembersRefSetIdQuery = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)));
		final TermQuery activeQuery = new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1)));
		final BooleanQuery query = new BooleanQuery();
		query.add(referencingRefSetMembersQuery, Occur.MUST);
		query.add(activeQuery, Occur.MUST);
		query.add(refSetMembersRefSetIdQuery, Occur.MUST);
		final TotalHitCountCollector collector = new TotalHitCountCollector();
		service.search(branchPath, query, collector);
		return collector.getTotalHits() > 0;
	}

	@Override
	public Collection<String> getAllQueries(final IBranchPath branchPath, final String refSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(refSetId, "refSetId");
		
		final Collection<String> queries = newHashSet();
		final Query refSetMembersRefSetIdQuery = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)));
		final Query activeQuery = new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1)));
		final BooleanQuery query = new BooleanQuery();
		query.add(refSetMembersRefSetIdQuery, Occur.MUST);
		query.add(activeQuery, Occur.MUST);
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		try {
			
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, MEMBER_QUERY_FIELDS_TO_LOAD);
				final String escgQuery = doc.get(REFERENCE_SET_MEMBER_QUERY);
				if (null != escgQuery) {
					queries.add(escgQuery.trim());
				}
			}
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting all queries for query type reference set.", e);
		}
		
		return queries;
		
	}
	
	@Override
	public LongKeyMap getReferencedConceptIds(final IBranchPath branchPath) {
		
		checkNotNull(branchPath, "branchPath");

		final BooleanQuery membershipQuery = new BooleanQuery(true);
		
		final PrefixQuery refSetMembershipQuery = new PrefixQuery(new Term(CONCEPT_REFERRING_REFERENCE_SET_ID));
		refSetMembershipQuery.setRewriteMethod(CONSTANT_SCORE_FILTER_REWRITE);
		membershipQuery.add(refSetMembershipQuery, SHOULD);
		
		final PrefixQuery mappingMembershipQuery = new PrefixQuery(new Term(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID));
		mappingMembershipQuery.setRewriteMethod(CONSTANT_SCORE_FILTER_REWRITE);
		membershipQuery.add(mappingMembershipQuery, SHOULD);

		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(COMPONENT_ACTIVE, intToPrefixCoded(1))), MUST);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, intToPrefixCoded(CONCEPT_NUMBER))), MUST);
		query.add(membershipQuery, MUST);
		
		final LongKeyMap refSetIdReferencedConceptIds = new LongKeyOpenHashMap();
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		
		try {
			
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			final LongList docIds = new LongArrayList(collector.getDocIDs().size());
			
			//calculate doc IDs for parallel lookup
			while (iterator.next()) {
				docIds.add(iterator.getDocID());
			}
			
			final Object mutex = new Object();
			
			parallelForEach(docIds, new LongSets.LongCollectionProcedure() {
				public void apply(final long docId) {
					
					final Document doc = service.document(branchPath, Ints.checkedCast(docId), REF_SET_MEMBERSHIP_FIELDS_TO_LOAD);
					final long conceptId = ComponentIdLongField.getLong(doc);
					
					final LongSet refSetIds = newLongSet();
					for (final IndexableField field : doc.getFields(CONCEPT_REFERRING_REFERENCE_SET_ID)) {
						refSetIds.add(getLongValue(field));
					}
					
					for (final IndexableField field : doc.getFields(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID)) {
						refSetIds.add(getLongValue(field));
					}
					
					synchronized (mutex) {
						for (final LongIterator itr = refSetIds.iterator(); itr.hasNext(); /**/) {
	
							final long refSetId = itr.next();
							
							LongSet conceptIds = (LongSet) refSetIdReferencedConceptIds.get(refSetId);
							if (conceptIds instanceof LongSet) {
								((LongSet) conceptIds).add(conceptId);
							} else {
								conceptIds = newLongSet();
								conceptIds.add(conceptId);
								refSetIdReferencedConceptIds.put(refSetId, conceptIds);
							}
							
						}
					}
					
				}
			});
			
			return refSetIdReferencedConceptIds;
			
		} catch (final IOException e) {
			throw new IndexException("Error while querying module dependency reference set members.", e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#filterTerminologyBrowser(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFilterClientTerminologyBrowser<SnomedConceptIndexEntry, String> filterTerminologyBrowser(final IBranchPath branchPath, final String expression, final IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getRootConceptStorageKeys(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public LongSet getRootConceptStorageKeys(final IBranchPath branchPath) {
		final Collection<SnomedConceptIndexEntry> concepts = getRootConcepts(branchPath);
		final LongSet $ = new LongOpenHashSet();
		for (final IIndexEntry entry : concepts) {
			$.add(entry.getStorageKey());
		}
		return $;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getSuperTypeStorageKeys(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object)
	 */
	@Override
	public LongSet getSuperTypeStorageKeys(final IBranchPath branchPath, final String concpetId) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getSuperTypeStorageKeys(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public LongSet getSuperTypeStorageKeys(final IBranchPath branchPath, final long storageKey) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#getRootConceptIds(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public Collection<String> getRootConceptIds(final IBranchPath branchPath) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(final IBranchPath branchPath,
			final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#exists(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public boolean exists(final IBranchPath branchPath, final String componentId) {
		return null != getRefSet(branchPath, componentId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.ITerminologyBrowser#exists(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean exists(final IBranchPath branchPath, final String componentId, final String codeSystemShortName) {
		return exists(branchPath, componentId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#hasMapping(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean hasMapping(final IBranchPath branchPath, final String mappingRefSetId, final String sourceId, final String targetId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(mappingRefSetId, "Mapping reference set identifier ID argument cannot be null.");
		Preconditions.checkNotNull(sourceId, "Map source ID argument cannot be null.");
		Preconditions.checkNotNull(targetId, "Map target ID argument cannot be null.");
		
		final Query query = getMappingQuery(mappingRefSetId, sourceId, targetId);
		
		
		return 0 < service.getHitCount(branchPath, query, null);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getMappings(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMappings(final IBranchPath branchPath, final String mappingRefSetId, final String sourceId, final String targetId) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(mappingRefSetId, "Mapping reference set identifier ID argument cannot be null.");
		Preconditions.checkNotNull(sourceId, "Map source ID argument cannot be null.");
		Preconditions.checkNotNull(targetId, "Map target ID argument cannot be null.");
		
		final Query query = getMappingQuery(mappingRefSetId, sourceId, targetId);
		final int hitCount = service.getHitCount(branchPath, query, null);
		if (hitCount < 1) {
			return Collections.emptySet();
		}
		
		final TopDocs topDocs = service.search(branchPath, query, hitCount);
		final SnomedRefSetMemberIndexEntry[] members = new SnomedRefSetMemberIndexEntry[hitCount];
		
		int i = 0;
		for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
			final Document doc = service.document(branchPath, scoreDoc.doc, null/*all fields*/);
			members[i++] = SnomedRefSetMemberIndexEntry.create(doc, branchPath);
		}

		return Collections.unmodifiableCollection(Arrays.asList(members));
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getAllReferenceSets(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public Collection<SnomedRefSetIndexEntry> getAllReferenceSets(final IBranchPath branchPath) {
		final TermQuery query = new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER)));
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		try {
			
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			return Lists.newArrayList(createResultObjects(branchPath, iterator));
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting all reference sets.", e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getActiveReferringMembers(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getActiveReferringMembers(final IBranchPath branchPath, final String conceptId) {
		return getReferringMembers(branchPath, conceptId, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getReferringMembers(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final IBranchPath branchPath, final String conceptId) {
		return getReferringMembers(branchPath, conceptId, false);
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMembersForMapTarget(final IBranchPath branchPath, final String mapTarget, final String mappingRefSetId) {
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(mappingRefSetId))), Occur.MUST);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, mapTarget)), Occur.MUST);
		query.add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
		
		final BooleanQuery mapQuery = new BooleanQuery(true);
		mapQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.COMPLEX_MAP_VALUE))), Occur.SHOULD);
		mapQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.SIMPLE_MAP_VALUE))), Occur.SHOULD);
		query.add(mapQuery, Occur.MUST);
		return getMembers(branchPath, query);
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final IBranchPath branchPath, final String conceptId) {
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, conceptId)), Occur.MUST);
		query.add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
		
		final BooleanQuery mapQuery = new BooleanQuery(true);
		mapQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.COMPLEX_MAP_VALUE))), Occur.SHOULD);
		mapQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.SIMPLE_MAP_VALUE))), Occur.SHOULD);
		query.add(mapQuery, Occur.MUST);
		return getMembers(branchPath, query);
	}
	
	@Override
	public Map<String, Collection<String>> getMapppings(final IBranchPath branchPath, final String refSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(refSetId, "refSetId");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(COMPONENT_ACTIVE, intToPrefixCoded(1))), MUST);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, longToPrefixCoded(refSetId))), MUST);
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		final Map<String, Collection<String>> mappings = newHashMap();
		
		try {
			
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, MAP_TARGET_COMPONENT_ID_FIELDS_TO_LOAD);
				final String mapSource = doc.get(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID);
				final String mapTarget = doc.get(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID);
				if (!isEmpty(mapSource) && !isEmpty(mapTarget)) {
					
					Collection<String> collection = mappings.get(mapSource);
					if (null == collection) {
						collection = newHashSet();
						collection.add(mapTarget);
						mappings.put(mapSource, collection);
					} else {
						collection.add(mapTarget);
					}
					
				}
			}
			
			return mappings;
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting map target IDs.", e);
		}
	}
	
	private Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final IBranchPath branchPath, final String conceptId, final boolean excludeInactive) {
		final Query query;
		final TermQuery queryFragment = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, conceptId));
		
		if (excludeInactive) {
			query = new BooleanQuery(true);
			((BooleanQuery) query).add(queryFragment, Occur.MUST);
			((BooleanQuery) query).add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
		} else {
			query = queryFragment; 
		}
		return getMembers(branchPath, query);
	}
	
	private Query getMappingQuery(final String mappingRefSetId, final String sourceId, final String targetId) {
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(mappingRefSetId))), Occur.MUST);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, sourceId)), Occur.MUST);
		query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, targetId)), Occur.MUST);
		query.add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
		return query;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getMembers(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMembers(final IBranchPath branchPath, final String referenceSetId) {
		return getMembers(branchPath, referenceSetId, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser#getActiveMembers(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getActiveMembers(final IBranchPath branchPath, final String referenceSetId) {
		return getMembers(branchPath, referenceSetId, true);
	}
	
	
	private Collection<SnomedRefSetMemberIndexEntry> getMembers(final IBranchPath branchPath, final String referenceSetId, final boolean excludeInactive) {
		
		final Query query;
		final TermQuery queryFragment = new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(referenceSetId)));
		
		if (excludeInactive) {
			query = new BooleanQuery(true);
			((BooleanQuery) query).add(queryFragment, Occur.MUST);
			((BooleanQuery) query).add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
		} else {
			query = queryFragment; 
		}

		return getMembers(branchPath, query);
	}
	
	private Collection<SnomedRefSetMemberIndexEntry> getMembers(final IBranchPath branchPath, final Query query) {
		final int hitCount = service.getHitCount(branchPath, query, null);
		if (hitCount < 1) {
			return Collections.emptySet();
		}
		
		final TopDocs topDocs = service.search(branchPath, query, hitCount);
		final List<SnomedRefSetMemberIndexEntry> members = Lists.newArrayListWithExpectedSize(hitCount);
		
		for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
			final Document doc = service.document(branchPath, scoreDoc.doc, null/*all fields*/);
			members.add(SnomedRefSetMemberIndexEntry.create(doc, branchPath));
		}

		return members;
	}
	
}