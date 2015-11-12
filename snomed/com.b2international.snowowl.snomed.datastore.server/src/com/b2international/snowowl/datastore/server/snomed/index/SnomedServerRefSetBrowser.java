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
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import bak.pcj.LongIterator;
import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Lucene based reference set browser implementation.
 * 
 */
public class SnomedServerRefSetBrowser extends AbstractSnomedIndexBrowser<SnomedRefSetIndexEntry> implements SnomedRefSetBrowser {

	private static final Set<String> MAPPING_REFERENCE_SET_ID_FIELD = Collections.unmodifiableSet(Sets.newHashSet(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID));
	private static final Set<String> REFERENCE_SET_ID_FIELD = Collections.unmodifiableSet(Sets.newHashSet(CONCEPT_REFERRING_REFERENCE_SET_ID));
	private static final Set<String> COMPONENT_ID_FIELD = SnomedMappings.fieldsToLoad().id().build();
	private static final Set<String> REFERENCE_SET_TYPE_FIELD = Collections.unmodifiableSet(Sets.newHashSet(REFERENCE_SET_TYPE));
	private static final Set<String> FIELDS_TO_LOAD_FOR_EXTENDED_COMPONENT = SnomedMappings.fieldsToLoad().id().label().iconId().build();
	
	private static final Set<String> FIELD_NAMES_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.label()
			.refSetStorageKey()
			.iconId()
			.module()
			.active()
			.released()
			.effectiveTime()
			.field(REFERENCE_SET_REFERENCED_COMPONENT_TYPE)
			.field(REFERENCE_SET_TYPE)
			.field(REFERENCE_SET_STRUCTURAL)
			.build();

	private static final Set<String> MEMBER_QUERY_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().field(REFERENCE_SET_MEMBER_QUERY).build();
	private static final Set<String> MAP_TARGET_COMPONENT_ID_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.memberReferencedComponentId()
			.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID).build();
	
	private static final Set<String> REF_SET_MEMBERSHIP_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().id().field(CONCEPT_REFERRING_REFERENCE_SET_ID)
			.field(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID).build();

	protected final class RefSetTypeToConceptFunction implements Function<SnomedRefSetType, SnomedConceptIndexEntry> {
		private final IBranchPath branchPath;
		RefSetTypeToConceptFunction(final IBranchPath branchPath) {
			this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		}
		@Override public SnomedConceptIndexEntry apply(final SnomedRefSetType type) {
			return getTerminologyBrowser().getConcept(branchPath, SnomedRefSetUtil.getConceptId(type));
		}
	}
	
	private final class RefsetIndexEntryToIdFunction implements Function<SnomedRefSetIndexEntry, String> {
		@Override public String apply(final SnomedRefSetIndexEntry input) {
			return input.getId();
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
		final Query refSetMemberByRefSetIdQuery = SnomedMappings.newQuery().memberRefSetId(refSetId).matchAll();
		service.search(branchPath, refSetMemberByRefSetIdQuery, collector);
		return collector.getTotalHits();
	}
	
	@Override
	public int getActiveMemberCount(final IBranchPath branchPath, final String refSetId) {
		final TotalHitCountCollector collector = new TotalHitCountCollector();
		final Query query = SnomedMappings.newQuery().active().memberRefSetId(refSetId).matchAll();
		service.search(branchPath, query, collector);
		return collector.getTotalHits();
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getMemberConcepts(final IBranchPath branchPath, final String refSetId) {
		final Query query = SnomedMappings.newQuery().active().memberRefSetId(refSetId).matchAll();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		try {
			final DocIdsIterator scoredDocIdsIterator = collector.getDocIDs().iterator();
			final Set<SnomedConceptIndexEntry> concepts = Sets.newHashSet();
			while (scoredDocIdsIterator.next()) {
				final int docId = scoredDocIdsIterator.getDocID();
				final Document doc = service.document(branchPath, docId, SnomedMappings.fieldsToLoad().memberReferencedComponentId().build());
				final String referencedConceptId = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
				concepts.add(getTerminologyBrowser().getConcept(branchPath, referencedConceptId));
			}
			return concepts;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Collection<String> getMemberConceptIds(final IBranchPath branchPath, final String refSetId) {
		final Query query = SnomedMappings.newQuery().active().memberRefSetId(refSetId).matchAll();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		try {
			final DocIdsIterator scoredDocIdsIterator = collector.getDocIDs().iterator();
			final Set<String> conceptIds = Sets.newHashSet();
			while (scoredDocIdsIterator.next()) {
				final int docId = scoredDocIdsIterator.getDocID();
				final Document doc = service.document(branchPath, docId, SnomedMappings.fieldsToLoad().memberReferencedComponentId().build());
				conceptIds.add(SnomedMappings.memberReferencedComponentId().getValueAsString(doc));
			}
			return conceptIds;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public boolean isActiveMemberOf(final IBranchPath branchPath, final long identifierConceptId, final long conceptId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null");
		final Query query = SnomedMappings.newQuery().type(CONCEPT_NUMBER).id(conceptId).field(CONCEPT_REFERRING_REFERENCE_SET_ID, Long.valueOf(identifierConceptId)).matchAll();
		return service.getHitCount(branchPath, query, null) > 0;
	}
	
	@Override
	public SnomedRefSetIndexEntry getRefSet(final IBranchPath branchPath, final String refSetId) {
		return getConcept(branchPath, createRefSetByIdQuery(refSetId));
	}

	@Override
	public Iterable<SnomedRefSetIndexEntry> getRefsSets(final IBranchPath branchPath) {
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath, createRefSetTypeQuery(), collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying all statements.", e);
		}
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypesWithActiveMembers(final IBranchPath branchPath, final String refsetId, final String... excludedIds) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(refsetId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		
		final Collection<SnomedConceptIndexEntry> subTypes = getTerminologyBrowser().getSubTypesById(branchPath, refsetId);
		final ImmutableSet<String> existingRefsetIds = FluentIterable.from(getRefsSets(branchPath)).transform(new RefsetIndexEntryToIdFunction()).toSet();
		final Set<String> excludedRefsetIds = newHashSet(excludedIds);
		
		final List<SnomedConceptIndexEntry> result = newArrayList();
		
		for (final SnomedConceptIndexEntry entry : subTypes) {
			if (!excludedRefsetIds.contains(entry.getId()) && existingRefsetIds.contains(entry.getId())) {
				result.add(entry);
			}
		}
		
		return result;
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypesWithActiveMembers(final IBranchPath branchPath, final String refsetId, final String... excludedIds) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(refsetId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		
		final Collection<SnomedConceptIndexEntry> allSubTypes = getTerminologyBrowser().getAllSubTypesById(branchPath, refsetId);
		final ImmutableSet<String> existingRefsetIds = FluentIterable.from(getRefsSets(branchPath)).transform(new RefsetIndexEntryToIdFunction()).toSet();
		final Set<String> excludedRefsetIds = newHashSet(excludedIds);
		
		final List<SnomedConceptIndexEntry> result = newArrayList();
		
		for (final SnomedConceptIndexEntry entry : allSubTypes) {
			if (!excludedRefsetIds.contains(entry.getId()) && existingRefsetIds.contains(entry.getId())) {
				result.add(entry);
			}
		}
		
		return result;
	}
	
	@Override
	public int getTypeOrdinal(final IBranchPath branchPath, final String refSetId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(refSetId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		final TopDocs topDocs = service.search(branchPath, createRefSetByIdQuery(refSetId), 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1;
		}
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, REFERENCE_SET_TYPE_FIELD);
		return Mappings.intField(REFERENCE_SET_TYPE).getValue(doc);
	}
	
	private Query createRefSetTypeQuery() {
		return SnomedMappings.newQuery().refSet().matchAll();
	}

	private Query createRefSetByIdQuery(final String refSetId) {
		return SnomedMappings.newQuery().refSet().id(refSetId).matchAll();
	}

	@Override
	public Collection<String> getAllRefSetIds(final IBranchPath branchPath) {
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, createRefSetTypeQuery(), collector);
		
		final Collection<String> $ = Sets.newHashSet();
		try {
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, COMPONENT_ID_FIELD);
				$.add(SnomedMappings.id().getValueAsString(doc));
			}
		} catch (final IOException e) {
			throw new IndexException("Error while getting all reference set identifier concept IDs.", e);
		}
		
		return Collections.unmodifiableCollection($);
	}
	
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
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().storageKey().build());
		return Mappings.storageKey().getValue(doc);
	}
	
	@Override
	public long getStorageKey(final IBranchPath branchPath, final String identifierConceptId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Concept ID argument cannot be null.");
		
		final TopDocs topDocs = service.search(branchPath, createRefSetByIdQuery(identifierConceptId), 1);
		
		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1L;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().refSetStorageKey().build());
		return SnomedMappings.refSetStorageKey().getValue(doc);
	}
	
	@Override
	public String getIdentifierId(final IBranchPath branchPath, final long storageKey) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final Query query = SnomedMappings.newQuery().refSet().refSetStorageKey(storageKey).matchAll();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return null;
		}
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		final Document doc = service.document(branchPath, scoreDoc.doc, COMPONENT_ID_FIELD);
		return SnomedMappings.id().getValueAsString(doc);
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getRootConcepts(final IBranchPath branchPath) {
		final int size = SnomedRefSetType.values().length;
		final Collection<SnomedConceptIndexEntry> roots = Lists.newArrayListWithExpectedSize(size);
		final RefSetTypeToConceptFunction typeToConceptFunction = new RefSetTypeToConceptFunction(branchPath);
		roots.addAll(FluentIterable.from(SnomedRefSetUtil.getTypesForUI()).transform(typeToConceptFunction).filter(Predicates.notNull()).toList());
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

	@Override
	public Collection<String> getContainerRefSetIds(final IBranchPath branchPath, final String conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");
		
		//if not a concept ID, we do nothing.
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
			return Collections.emptySet();
		}
		
		final Query query = SnomedMappings.newQuery().active().type(CONCEPT_NUMBER).id(conceptId).matchAll();
		
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
	
	@Override
	public Collection<String> getContainerMappingRefSetIds(final IBranchPath branchPath, final String conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");
		
		//if not a concept ID, we do nothing.
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId)) {
			return Collections.emptySet();
		}
		
		final Query query = SnomedMappings.newQuery().active().type(CONCEPT_NUMBER).id(conceptId).matchAll();
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
		

		final BooleanQuery sourceModuleQuery = new BooleanQuery(true);
		sourceModuleQuery.add(SnomedMappings.newQuery().module(id).matchAll(), Occur.MUST);
		sourceModuleQuery.add(SnomedMappings.newQuery().field(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME, EffectiveTimes.UNSET_EFFECTIVE_TIME).matchAll(), Occur.MUST_NOT);
		
		final BooleanQuery targetModuleQuery = new BooleanQuery(true);
		targetModuleQuery.add(SnomedMappings.newQuery().memberReferencedComponentId(id).matchAll(), Occur.MUST);
		targetModuleQuery.add(SnomedMappings.newQuery().field(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME, EffectiveTimes.UNSET_EFFECTIVE_TIME).matchAll(), Occur.MUST_NOT);
		
		final Query moduleQuery = SnomedMappings.newQuery().and(sourceModuleQuery).and(targetModuleQuery).matchAny();
		final Query query = SnomedMappings.newQuery().memberRefSetId(Concepts.REFSET_MODULE_DEPENDENCY_TYPE).and(moduleQuery).matchAll();
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		
		final LongSet storageKeys = new LongOpenHashSet();

		try {
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, SnomedMappings.fieldsToLoad().storageKey().build());
				storageKeys.add(Mappings.storageKey().getValue(doc));
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
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypesById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypes(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypesById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSubTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSubTypeCountById(final IBranchPath branchPath, final String id) {
		return getTerminologyBrowser().getSubTypeCountById(branchPath, id);
	}

	@Override
	public int getAllSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCount(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCountById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSuperTypeCountById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCountById(final IBranchPath branchPath, final String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	@Override
	public ExtendedComponent getExtendedComponent(final IBranchPath branchPath, final long storageKey) {
		checkNotNull(branchPath, "branchPath");
		checkArgument(storageKey > CDOUtils.NO_STORAGE_KEY);
		final TopDocs topDocs = service.search(branchPath, SnomedMappings.newQuery().refSet().refSetStorageKey(storageKey).matchAll(), 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, FIELDS_TO_LOAD_FOR_EXTENDED_COMPONENT);
		return new ExtendedComponentImpl(
				SnomedMappings.id().getValueAsString(doc), 
				Mappings.label().getValue(doc),
				Mappings.iconId().getValue(doc), 
				SnomedTerminologyComponentConstants.REFSET_NUMBER);
	}

	@Override
	public boolean isRegularRefSet(final IBranchPath branchPath, final long storageKey) {
		checkNotNull(branchPath, "branchPath");
		checkArgument(storageKey > NO_STORAGE_KEY, "Storage key should be a positive integer.");
		final Query query = SnomedMappings.newQuery().refSetStorageKey(storageKey).refSet().field(REFERENCE_SET_STRUCTURAL, 0).matchAll();
		return service.getHitCount(branchPath, query, null) > 0;
	}
	
	@Override
	protected Set<String> getFieldNamesToLoad() {
		return FIELD_NAMES_TO_LOAD;
	}

	@Override
	protected SnomedRefSetIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		return SnomedRefSetIndexEntry.builder(doc).build();
	}

	@Override
	public SnomedConceptIndexEntry getTopLevelConcept(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isTerminologyAvailable(final IBranchPath branchPath) {
		return true;
	}

	@Override
	public boolean isSuperTypeOf(final IBranchPath branchPath, final SnomedConceptIndexEntry superType, final SnomedConceptIndexEntry subType) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isSuperTypeOfById(final IBranchPath branchPath, final String superTypeId, final String subTypeId) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isReferenced(final IBranchPath branchPath, final String refSetId, final String componentId) {
		final Query query = SnomedMappings.newQuery().active().memberReferencedComponentId(componentId).memberRefSetId(refSetId).matchAll();
		final TotalHitCountCollector collector = new TotalHitCountCollector();
		service.search(branchPath, query, collector);
		return collector.getTotalHits() > 0;
	}

	@Override
	public Collection<String> getAllQueries(final IBranchPath branchPath, final String refSetId) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(refSetId, "refSetId");
		
		final Collection<String> queries = newHashSet();
		final Query query = SnomedMappings.newQuery().active().memberRefSetId(refSetId).matchAll();
		
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

		final PrefixQuery refSetMembershipQuery = new PrefixQuery(new Term(CONCEPT_REFERRING_REFERENCE_SET_ID));
		refSetMembershipQuery.setRewriteMethod(CONSTANT_SCORE_FILTER_REWRITE);
		
		final PrefixQuery mappingMembershipQuery = new PrefixQuery(new Term(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID));
		mappingMembershipQuery.setRewriteMethod(CONSTANT_SCORE_FILTER_REWRITE);
		
		final Query query = SnomedMappings.newQuery()
				.active()
				.and(SnomedMappings.newQuery()
						.and(refSetMembershipQuery)
						.and(mappingMembershipQuery)
						.matchAny())
				.matchAll();
		
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
				@Override
				public void apply(final long docId) {
					
					final Document doc = service.document(branchPath, Ints.checkedCast(docId), REF_SET_MEMBERSHIP_FIELDS_TO_LOAD);
					final long conceptId = SnomedMappings.id().getValue(doc);
					
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
	
	@Override
	public IFilterClientTerminologyBrowser<SnomedConceptIndexEntry, String> filterTerminologyBrowser(final IBranchPath branchPath, final String expression, final IProgressMonitor monitor) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<String> getRootConceptIds(final IBranchPath branchPath) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(final IBranchPath branchPath,
			final SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean exists(final IBranchPath branchPath, final String componentId) {
		return null != getRefSet(branchPath, componentId);
	}
	
	@Override
	public boolean hasMapping(final IBranchPath branchPath, final String mappingRefSetId, final String sourceId, final String targetId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(mappingRefSetId, "Mapping reference set identifier ID argument cannot be null.");
		Preconditions.checkNotNull(sourceId, "Map source ID argument cannot be null.");
		Preconditions.checkNotNull(targetId, "Map target ID argument cannot be null.");
		final Query query = getMappingQuery(mappingRefSetId, sourceId, targetId);
		return 0 < service.getHitCount(branchPath, query, null);
	}
	
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

	@Override
	public Collection<SnomedRefSetIndexEntry> getAllReferenceSets(final IBranchPath branchPath) {
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, createRefSetTypeQuery(), collector);
		try {
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			return Lists.newArrayList(createResultObjects(branchPath, iterator));
		} catch (final IOException e) {
			throw new IndexException("Error while getting all reference sets.", e);
		}
		
	}

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
		final Query query = SnomedMappings.newQuery()
				.active()
				.memberRefSetId(mappingRefSetId)
				.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, mapTarget)
				.and(getMapQuery()).matchAll();
		return getMembers(branchPath, query);
	}

	private Query getMapQuery() {
		return SnomedMappings.newQuery().memberRefSetType(SnomedRefSetType.COMPLEX_MAP).memberRefSetType(SnomedRefSetType.SIMPLE_MAP).matchAny();
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final IBranchPath branchPath, final String conceptId) {
		final Query query = SnomedMappings.newQuery().active().memberReferencedComponentId(conceptId).and(getMapQuery()).matchAll();
		return getMembers(branchPath, query);
	}
	
	@Override
	public Map<String, Collection<String>> getMapppings(final IBranchPath branchPath, final String refSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(refSetId, "refSetId");
		
		final Query query = SnomedMappings.newQuery().active().memberRefSetId(refSetId).matchAll();
		
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		final Map<String, Collection<String>> mappings = newHashMap();
		
		try {
			
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final int docID = iterator.getDocID();
				final Document doc = service.document(branchPath, docID, MAP_TARGET_COMPONENT_ID_FIELDS_TO_LOAD);
				final String mapSource = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
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
		final SnomedQueryBuilder query;
		final SnomedQueryBuilder queryFragment = SnomedMappings.newQuery().memberReferencedComponentId(conceptId);
		
		if (excludeInactive) {
			query = SnomedMappings.newQuery();
			query.active().and(queryFragment.matchAll());
		} else {
			query = queryFragment; 
		}
		return getMembers(branchPath, query.matchAll());
	}
	
	private Query getMappingQuery(final String mappingRefSetId, final String sourceId, final String targetId) {
		return SnomedMappings.newQuery()
				.active()
				.memberRefSetId(mappingRefSetId)
				.memberReferencedComponentId(sourceId)
				.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, targetId)
				.matchAll();
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMembers(final IBranchPath branchPath, final String referenceSetId) {
		return getMembers(branchPath, referenceSetId, false);
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getActiveMembers(final IBranchPath branchPath, final String referenceSetId) {
		return getMembers(branchPath, referenceSetId, true);
	}
	
	
	private Collection<SnomedRefSetMemberIndexEntry> getMembers(final IBranchPath branchPath, final String referenceSetId, final boolean excludeInactive) {
		final SnomedQueryBuilder query;
		final SnomedQueryBuilder queryFragment = SnomedMappings.newQuery().memberRefSetId(referenceSetId);
		
		if (excludeInactive) {
			query = SnomedMappings.newQuery().active().and(queryFragment.matchAll());
		} else {
			query = queryFragment; 
		}

		return getMembers(branchPath, query.matchAll());
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
