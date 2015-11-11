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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.commons.Pair.SerializablePair.of;
import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.commons.pcj.LongSets.newLongSetWithMurMur3Hash;
import static com.b2international.commons.pcj.LongSets.toStringSet;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_LABEL_SORT_KEY;
import static com.b2international.snowowl.datastore.index.DocIdCollector.create;
import static com.b2international.snowowl.datastore.index.IndexUtils.getLongValue;
import static com.b2international.snowowl.datastore.index.IndexUtils.isEmpty;
import static com.b2international.snowowl.datastore.index.IndexUtils.longToPrefixCoded;
import static com.b2international.snowowl.datastore.index.IndexUtils.parallelForEachDocId;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.DEFINING_CHARACTERISTIC_TYPES;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.deserializeValue;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getDataType;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.isMapping;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.toMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Multimaps.synchronizedMultimap;
import static com.google.common.collect.Sets.newConcurrentHashSet;
import static com.google.common.collect.Sets.newHashSet;
import static java.text.NumberFormat.getIntegerInstance;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.pcj.LongCollections;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.IPostStoreUpdateListener;
import com.b2international.snowowl.datastore.IPostStoreUpdateListener2;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.NamespaceMapping;
import com.b2international.snowowl.datastore.server.snomed.index.ReducedConcreteDomainFragmentCollector;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedComponentLabelCollector;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.SnomedConceptInactivationIdCollector;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.UncheckedExecutionException;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;
import bak.pcj.list.LongListIterator;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongChainedHashSet;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;
import bak.pcj.set.UnmodifiableLongSet;

/**
 * Service singleton for the SNOMED&nbsp;CT core components.
 * <p>This class is an implementation of {@link IPostStoreUpdateListener}. 
 * All the cached and pre-calculated data structure gets updated after lightweight store updates.<br>
 * E.g.: This service implementation caches all Synonym concept and its all descendants. This cached structure gets updated after 
 * each RDBMS, index and all other ephemeral store update after a CDO invalidation event.
 * 
 * @see IPostStoreUpdateListener
 */
@SuppressWarnings("unchecked")
public class SnomedComponentService implements ISnomedComponentService, IPostStoreUpdateListener2 {

	private static final Set<String> MEMBER_REF_SET_ID_FILDS_TO_LOAD = SnomedMappings.fieldsToLoad().memberReferenceSetId().build();
	private static final Set<String> COMPONENT_ID_MODULE_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().id().module().build();
	private static final Set<String> MEMBER_REFERENCED_COMPONENT_ID_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().memberReferencedComponentId().build();
	private static final Set<String> MEMBER_VALUE_ID_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().field(REFERENCE_SET_MEMBER_VALUE_ID).build();
	private static final Set<String> MEMBER_ID_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().field(REFERENCE_SET_MEMBER_UUID).active().memberReferencedComponentId().build();
	private static final Set<String> FSN_DESCRIPTION_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().id().label().descriptionConcept().build();
	private static final Set<String> DATA_TYPE_VALUE_AND_REFERENCED_COMPONENT_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().memberReferencedComponentId().field(REFERENCE_SET_MEMBER_SERIALIZED_VALUE).build();
	private static final Set<String> MODULE_MEMBER_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().storageKey().module().memberReferencedComponentId()
			.field(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME).field(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME).build();
	
	private static final Set<String> COMPONENT_ID_KEY_TO_LOAD = SnomedMappings.fieldsToLoad().id().build();
	private static final Set<String> COMPONENT_ID_STORAGE_KEY_TO_LOAD = SnomedMappings.fieldsToLoad().id().storageKey().build();
	private static final Set<String> MEMBER_UUID_STORAGE_KEY_TO_LOAD = SnomedMappings.fieldsToLoad().storageKey().field(REFERENCE_SET_MEMBER_UUID).build();
	private static final long DESCRIPTION_TYPE_ROOT_CONCEPT_ID = Long.valueOf(Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT);
	private static final long SYNONYM_CONCEPT_ID = Long.valueOf(Concepts.SYNONYM);
	private static final Set<String> COMPONENT_LABEL_TO_LOAD = SnomedMappings.fieldsToLoad().label().build();
	private static final Set<String> COMPONENT_STATUS_TO_LOAD = SnomedMappings.fieldsToLoad().active().build();
	private static final Set<String> COMPONENT_ICON_ID_TO_LOAD = SnomedMappings.fieldsToLoad().iconId().build();
	
	private static final Set<String> RELATIONSHIP_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.relationshipType()
			.field(RELATIONSHIP_OBJECT_ID)
			.field(RELATIONSHIP_VALUE_ID)
			.field(RELATIONSHIP_DESTINATION_NEGATED).build();
	
	private static final Set<String> DESCRIPTION_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().descriptionType().descriptionConcept().label().build();
	
	private static final Set<String> DESCRIPTION_EXTENDED_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.effectiveTime()
			.storageKey()
			.label()
			.descriptionConcept()
			.descriptionType()
			.build();
	
	private static final Function<String, Boolean> ALWAYS_FALSE_FUNC = new Function<String, Boolean>() {
		@Override
		public Boolean apply(final String input) {
			return false;
		}
	};
	
	private static final Query PREFERRED_LANGUAGE_QUERY = new TermQuery(new Term(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, longToPrefixCoded(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)));
	private static final Query ACCEPTED_LANGUAGE_QUERY = new TermQuery(new Term(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, longToPrefixCoded(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE)));
	private static final Query DESCRIPTION_INACTIVATION_REFSET_QUERY = SnomedMappings.newQuery().memberRefSetId(REFSET_DESCRIPTION_INACTIVITY_INDICATOR).matchAll();
	private static final Query ALL_CORE_COMPONENTS_QUERY;
	
	static {
		final Query typeQuery = SnomedMappings.newQuery().type(CONCEPT_NUMBER).type(DESCRIPTION_NUMBER).type(RELATIONSHIP_NUMBER).matchAny();
		ALL_CORE_COMPONENTS_QUERY = SnomedMappings.newQuery().and(typeQuery).matchAll();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedComponentService.class);
	private final LoadingCache<IBranchPath, LoadingCache<CacheKeyType, Object>> cache;
	
	private final Map<IBranchPath, Job> jobMap = Maps.newHashMap();
	
	/**
	 * A sorted set of the top most relationship type concept IDs.
	 * <p>
	 * <b>NOTE:&nbsp;</b>Just for estimation.
	 */
	public static final Set<String> TOP_MOST_RELATIONSHIP_TYPE_IDS = ImmutableSortedSet.<String>of(
			Concepts.IS_A,
			Concepts.FINDING_SITE,
			Concepts.HAS_ACTIVE_INGREDIENT,
			Concepts.METHOD,
			Concepts.MORPHOLOGY,
			Concepts.PART_OF,
			Concepts.HAS_DOSE_FORM,
			Concepts.PROCEDURE_SITE_DIRECT,
			Concepts.INTERPRETS,
			Concepts.CAUSATIVE_AGENT	
	);

	/**
	 * A sorted set of the top most relationship type concept IDs.
	 * <p>
	 * <b>NOTE:&nbsp;</b>Just for estimation.
	 */
	public static final LongCollection TOP_MOST_RELATIONSHIP_TYPE_IDS_AS_LONG = new UnmodifiableLongSet(new LongChainedHashSet(new long[] {
			Long.parseLong(Concepts.IS_A),
			Long.parseLong(Concepts.FINDING_SITE),
			Long.parseLong(Concepts.HAS_ACTIVE_INGREDIENT),
			Long.parseLong(Concepts.METHOD),
			Long.parseLong(Concepts.MORPHOLOGY),
			Long.parseLong(Concepts.PART_OF),
			Long.parseLong(Concepts.HAS_DOSE_FORM),
			Long.parseLong(Concepts.PROCEDURE_SITE_DIRECT),
			Long.parseLong(Concepts.INTERPRETS),
			Long.parseLong(Concepts.CAUSATIVE_AGENT)
	}));
				
	
	/**
	 * Populates the cache.
	 */
	public SnomedComponentService() {
		cache = CacheBuilder.newBuilder()
			.build(new CacheLoader<IBranchPath, LoadingCache<CacheKeyType, Object>>() {
				@Override
				public LoadingCache<CacheKeyType, Object> load(final IBranchPath branchPath) throws Exception {
					return CacheBuilder.newBuilder()
							.build(new CacheLoader<CacheKeyType, Object>() {
								@Override
								public Object load(final CacheKeyType key) throws Exception {
									return loadValue(branchPath, key);
								}
							});
				}
			});
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IPostStoreUpdateListener2#getRepositoryUuid()
	 */
	@Override
	public String getRepositoryUuid() {
		final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
		return connection.getUuid();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IPostStoreUpdateListener#storeUpdated(java.lang.Object)
	 */
	@Override
	public void storeUpdated(final CDOCommitInfo commitInfo) {
		if (commitInfo == null)
			return;
		
		final IBranchPath branchPath = BranchPathUtils.createPath(commitInfo.getBranch());
		
		synchronized (branchPath) {
			Job job = null;
			if (jobMap.containsKey(branchPath)) {
				job = jobMap.get(branchPath);
			} else {
				job = new BranchCacheLoadingJob(branchPath);
				jobMap.put(branchPath, job);
			}
			job.schedule();
		}
	}

	/**
	 * Returns with a set of SNOMED&nbsp;CT concept containing the 'Synonym' concept (ID:&nbsp;900000000000013009) and all descendants.
	 * @return the 'Synonym' concept and all descendants.
	 */
	@Override
	public Set<String> getSynonymAndDescendantIds(final IBranchPath branchPath) {
		checkAndJoin(branchPath, null);
		try {
			return (Set<String>) cache.get(branchPath).get(CacheKeyType.SYNONYM_AND_DESCENDATNTS);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while getting 'Synonym' concept and all descendants.", e);
			throw new UncheckedExecutionException(e);
		}
	}
	
	/**
	 * Returns with the available concrete domain data type labels for a specified concrete domain data type.
	 * @param dataType the data type. E.g.: {@code BOOLEAN} or {@code DECIMAL}.
	 * @return a set of concrete domain data type labels for a specified data type.
	 */
	@Override
	public Set<String> getAvailableDataTypeLabels(final IBranchPath branchPath, final DataType dataType) {
		checkAndJoin(branchPath, null);
		try {
			return ((Map<DataType, Set<String>>) cache.get(branchPath).get(CacheKeyType.DATA_TYPE_LABELS)).get(dataType);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while getting available concrete domain data type labels for " + dataType, e);
			throw new UncheckedExecutionException(e);
		}
	}
	
	/**
	 * Returns with a set of allowed SNOMED&nbsp;CT concepts' ID.<br>Concept is allowed as preferred description type concept if 
	 * has an associated active description type reference set member and is the 'Synonym' concept or one of its descendant.
	 * @return a set of SNOMED&nbsp;CT description type concept identifier that can act as a preferred term of a concept.
	 */
	@Override
	public Set<String> getAvailablePreferredTermIds(final IBranchPath branchPath) {
		checkAndJoin(branchPath, null);
		try {
			return (Set<String>) cache.get(branchPath).get(CacheKeyType.AVAILABLE_PREFERRED_TERM_IDS);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while getting 'Synonym' concept and all descendants.", e);
			throw new UncheckedExecutionException(e);
		}
	}
	
	@Override
	public Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> getPredicates(final IBranchPath branchPath) {
		checkAndJoin(branchPath, null);
		try {
			return (Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>>) cache.get(branchPath).get(CacheKeyType.PREDICATE_TYPES);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while getting available MRCM predicates on '" + branchPath + "' branch.", e);
			throw new UncheckedExecutionException(e);
		}
	}
	
	@Override
	public Map<CDOID, String> getRefSetCdoIdIdMapping(final IBranchPath branchPath) {
		checkAndJoin(branchPath, null);
		try {
			return (Map<CDOID, String>) cache.get(branchPath).get(CacheKeyType.REFERENCE_SET_CDO_IDS);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while getting reference set CDO ID to ID mapping on '" + branchPath + "' branch.", e);
			throw new UncheckedExecutionException(e);
		}
	}
	
	/**
	 * Returns with all the IDs and the allowed term length of the description type SNOMED&nbsp;CT concepts. ID is included in the return set if concept fulfills the followings:
	 * <ul>
	 * <li>Concept is descendant of the {@code Description type (core metadata concept)} concept.</li>
	 * <li>Concept is active</li>
	 * <li>Concept has an active description type reference set member</li>
	 * </ul>
	 * @return a set of description type concept IDs and the allowed description term length.
	 */
	@Override
	public Map<String, Integer> getAvailableDescriptionTypeIdsWithLength(final IBranchPath branchPath) {
		checkAndJoin(branchPath, null);
		try {
			return (Map<String, Integer>) cache.get(branchPath).get(CacheKeyType.AVAILABLE_DESCRIPTION_IDS);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while getting 'Synonym' concept and all descendants.", e);
			throw new UncheckedExecutionException(e);
		}
	}
	
	/**
	 * Returns with all the IDs of the description type SNOMED&nbsp;CT concepts. ID is included in the return set if concept fulfills the followings:
	 * <ul>
	 * <li>Concept is descendant of the {@code Description type (core metadata concept)} concept.</li>
	 * <li>Concept is active</li>
	 * <li>Concept has an active description type reference set member</li>
	 * </ul>
	 * @return a set of description type concept IDs.
	 */
	@Override
	public Set<String> getAvailableDescriptionTypeIds(final IBranchPath branchPath) {
		return newHashSet(getAvailableDescriptionTypeIdsWithLength(branchPath).keySet());
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService#getExtensionConceptId(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public long getExtensionConceptId(final IBranchPath branchPath, final String componentId) {
		
		final short componentType = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId);
		
		if (componentType != SnomedTerminologyComponentConstants.CONCEPT_NUMBER
				&& componentType != SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER
				&& componentType != SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER) {
			
			return -1L;
		}
		
		final char format = componentId.charAt(componentId.length() - 3);
		long namespaceId;
		
		LongKeyLongMap nameSpaceIds;
		try {
			nameSpaceIds = (LongKeyLongMap) cache.get(branchPath).get(CacheKeyType.NAMESPACE_IDS);
			if ('0' == format) {
				namespaceId = nameSpaceIds.get(0L);
			} else {
				namespaceId = nameSpaceIds.get(Long.parseLong(componentId.substring(componentId.length() - 10, componentId.length() - 3)));
			}
			
			if (namespaceId < 1) {
				final long extensionNamespaceIdFromMapping = NamespaceMapping.getExtensionNamespaceId(Long.valueOf(componentId));
				if (extensionNamespaceIdFromMapping < 1) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Cannot find extension namespace concept ID for SNOMED CT component: " + componentId);
					}
					return -1L;
				} else {
					return extensionNamespaceIdFromMapping;
				}
			}
			
			return namespaceId;
		} catch (final ExecutionException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	/**
	 * Warms the underlying cache on the specified branch.
	 */
	@Override
	public void warmCache(final IBranchPath branchPath) {

		LOGGER.info("Initializing...");
		
		try {

			// Force loading components on path
			try {
				cache.get(branchPath);
			} catch (final ExecutionException e) {
				LOGGER.error(MessageFormat.format("Caught exception while requesting cache for branch path {0}.", branchPath), e);
				// Continue; the loading job might not have any effect.
			}

			final BranchCacheLoadingJob cacheLoadingJob = new BranchCacheLoadingJob(branchPath);
			jobMap.put(branchPath, cacheLoadingJob);
			cacheLoadingJob.schedule();
			cacheLoadingJob.join();
			
			SnomedEditingContext context = null;
			
			try {
				
				context = new SnomedEditingContext();
				final Concept root = new SnomedConceptLookupService().getComponent(Concepts.ROOT_CONCEPT, context.getTransaction());
				
				if (null == root ) { //SNOMED CT is not available
					return;
				}
				
				for (final Relationship destinationRelationship : root.getInboundRelationships()) {
					
					//just warm top level concepts
					destinationRelationship.getSource().getFullySpecifiedName();
				}
				
				LOGGER.info("The cache warming for SNOMED CT component service successfully finished.");
				
			} catch (final Exception e)  {
				
				// Not much we can do; this is just a cache warmer.
				LOGGER.warn("Exception caught while warming component cache, initialization canceled.");
				
			} finally {
				
				if (null != context) {
					context.close();
				}
			}
			
		} catch (final InterruptedException e) {
			LOGGER.warn("Warming the service cache failed.");
		}
	}
	
	/**
	 * Returns {@code true} if the specified active description is configured as the preferred one for the currently used language.
	 * @param description the SNOMED&nbsp;CT description to check. 
	 * @return {@code true} if the active description is the preferred term, otherwise returns {@code false}.
	 */
	@Override
	public boolean isPreferred(final IBranchPath branchPath, final Description description) {
		
		if (null == description) {
			LOGGER.warn("SNOMED CT description cannot be referenced. Description was null.");
			return false;
		}
		
		if (!CDOUtils.checkObject(description)) {
			LOGGER.warn("Description cannot be referenced. Description ID: " + CDOUtils.getAttribute(description, SnomedPackage.eINSTANCE.getComponent_Id(), String.class));
			return false;
		}

		if (!description.isActive()) { //inactive description cannot be preferred
			return false;
		}
		
		final String languageRefSetId = getLanguageRefSetId();
		
		if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) { //FSN cannot be preferred term
			return false;
		}
		
		for (final SnomedLanguageRefSetMember languageMember : description.getLanguageRefSetMembers()) {
			if (languageMember.isActive()) { //active language reference set member
				
				if (languageRefSetId.equals(languageMember.getRefSet().getIdentifierId())) { //language is relevant for the configured one
					
					if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(languageMember.getAcceptabilityId())) { //language member is preferred
						return true; //got the preferred term
					}
				}
				
			}
		}

		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService#getLabels(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String[])
	 */
	@Override
	public String[] getLabels(final IBranchPath branchPath, final String... componentIds) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentIds, "SNOMED CT component ID argument cannot be null.");
		
		final String[] labels = new String[componentIds.length];
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			for (int i = 0; i < componentIds.length; i++) {
				
				labels[i] = getComponentLabel(branchPath, componentIds[i], indexService, searcher);
				
			}
			
		} catch (final IOException e) {
			
			LOGGER.error("Error while searching for component labels.");
			throw new SnowowlRuntimeException(e);
			
		} finally {
			
			if (null != manager && null != searcher) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
					
				}
				
			}
			
		}
		
		return labels;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService#getDescriptionFragmentsForConcept(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<SnomedDescriptionFragment> getDescriptionFragmentsForConcept(final IBranchPath branchPath, final String conceptId, final String languageRefSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptId, "conceptId");
		checkNotNull(languageRefSetId, "languageRefSetId");
		
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			//array for description properties: ID, term, conceptId, typeId, effective time
			final Set<String[]> descriptionProperties = newHashSet();

			manager = getIndexServerService().getManager(branchPath);
			searcher = manager.acquire();
			final int maxDoc = getIndexServerService().maxDoc(branchPath);
			
			final Query descriptionQuery = SnomedMappings.newQuery().active().descriptionConcept(conceptId).matchAll();
			final DocIdCollector descriptionDocCollector = DocIdCollector.create(maxDoc);
			getIndexServerService().search(branchPath, descriptionQuery, descriptionDocCollector);

			final DocIdsIterator descriptionDocIdsItr = descriptionDocCollector.getDocIDs().iterator();
			while (descriptionDocIdsItr.next()) {
				final Document descriptionDoc = searcher.doc(descriptionDocIdsItr.getDocID(), DESCRIPTION_EXTENDED_FIELDS_TO_LOAD);
				descriptionProperties.add(new String[] {
					SnomedMappings.id().getValueAsString(descriptionDoc),
					Mappings.label().getValue(descriptionDoc),
					SnomedMappings.descriptionConcept().getValueAsString(descriptionDoc),
					SnomedMappings.descriptionType().getValueAsString(descriptionDoc),
					EffectiveTimes.format(SnomedMappings.effectiveTime().getValue(descriptionDoc))
				});
			}
			
			final Collection<SnomedDescriptionFragment> descriptionFragments = newHashSet();
			
			boolean foundPreferredTerm = false;
			for (final String[] descriptionProperty : descriptionProperties) {
				
				final String descriptionId = descriptionProperty[0];
				final String term = descriptionProperty[1];
				final String typeId = descriptionProperty[3];
				final String effectiveTime = descriptionProperty[4];
				
				final boolean fsn = Concepts.FULLY_SPECIFIED_NAME.equals(typeId);
				
				if (fsn) {
					
					final boolean interpetedForLanguage = getIndexServerService().getHitCount(branchPath, createPreferredQuery(descriptionId, languageRefSetId), null) > 0;
					if (interpetedForLanguage) {
						descriptionFragments.add(new SnomedDescriptionFragment(descriptionId, term, conceptId, typeId, false, effectiveTime));
					}
					
				} else {
					
					final boolean interpetedForLanguage = getIndexServerService().getHitCount(branchPath, createAcceptedQuery(descriptionId, languageRefSetId), null) > 0;
					if (interpetedForLanguage) {
						descriptionFragments.add(new SnomedDescriptionFragment(descriptionId, term, conceptId, typeId, false, effectiveTime));
					} else {
					
						if (!foundPreferredTerm) {
							final boolean preferredForLanguage = getIndexServerService().getHitCount(branchPath, createPreferredQuery(descriptionId, languageRefSetId), null) > 0;
							if (preferredForLanguage) {
								descriptionFragments.add(new SnomedDescriptionFragment(descriptionId, term, conceptId, typeId, preferredForLanguage, effectiveTime));
								foundPreferredTerm = true;
							}
						}
						
					}
					
				}
				
			}
			
			return Collections.unmodifiableCollection(descriptionFragments);
			
		} catch (final IOException e) {
			
			throw new IndexException("Error while getting description fragments for concept: " + conceptId + " [" + branchPath + "]", e);
			
		} finally {
			
			if (null != manager) {
				if (null != searcher) {
					
					try {
						manager.release(searcher);
					} catch (final IOException e) {
						try {
							manager.release(searcher);
						} catch (final IOException e1) {
							//intentionally ignored
						}
						throw new IndexException("Error while releasing index searcher on '" + branchPath.getPath() + "'.");
					}
					
				}
			}
			
		}
		
	}
	
	private Query createPreferredQuery(final String descriptionId, final String languageRefSetId) {
		return createLanguageAcceptanceQuery(descriptionId, languageRefSetId, PREFERRED_LANGUAGE_QUERY);
	}
	
	private Query createAcceptedQuery(final String descriptionId, final String languageRefSetId) {
		return createLanguageAcceptanceQuery(descriptionId, languageRefSetId, ACCEPTED_LANGUAGE_QUERY);
	}
	
	private Query createLanguageAcceptanceQuery(final String descriptionId, final String languageRefSetId, final Query acceptanceQuery) {
		return SnomedMappings.newQuery().active().memberRefSetId(languageRefSetId).memberReferencedComponentId(descriptionId).and(acceptanceQuery).matchAll();
	}
	
	@Override
	public String[] getDescriptionProperties(final IBranchPath branchPath, final String descriptionId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(descriptionId, "SNOMED CT description ID argument cannot be null.");
		
		//if not a valid relationship ID
		if (SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(descriptionId)) {
			return null;
		}
		final TopDocs topDocs = getIndexServerService().search(branchPath, SnomedMappings.newQuery().type(DESCRIPTION_NUMBER).id(descriptionId).matchAll(), 1);
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		final Document doc = getIndexServerService().document(branchPath, scoreDoc.doc, DESCRIPTION_FIELDS_TO_LOAD);
		
		final String label = Mappings.label().getValue(doc);
		final String conceptId = SnomedMappings.descriptionConcept().getValueAsString(doc);
		final String typeId = SnomedMappings.descriptionType().getValueAsString(doc);
		return new String[] { conceptId, typeId, label };
	}
	
	@Override
	public String[][] getAllDescriptionProperties(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		final int maxDoc = getIndexServerService().maxDoc(branchPath);

		if (maxDoc < 1) {
			return null;
		}
		
		final TopDocs topDocs = getIndexServerService().search(branchPath, SnomedMappings.newQuery().type(DESCRIPTION_NUMBER).matchAll(), maxDoc);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return null;
		}
		
		final String[][] result = new String[topDocs.totalHits][];
		int i = 0;
		
		for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
			final Document doc = getIndexServerService().document(branchPath, scoreDoc.doc, DESCRIPTION_EXTENDED_FIELDS_TO_LOAD);
			final String descriptionId = SnomedMappings.id().getValueAsString(doc);
			final String label = Mappings.label().getValue(doc);
			final String conceptId = SnomedMappings.descriptionConcept().getValueAsString(doc);
			final String typeId = SnomedMappings.descriptionType().getValueAsString(doc);
			final String storageKey = Mappings.storageKey().getValueAsString(doc);
			result[i++] = new String[] { descriptionId, conceptId, typeId, label, storageKey };
		}
		
		return result;
	}
	
	@Override
	public String[] getRelationshipProperties(final IBranchPath branchPath, final String relationshipId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(relationshipId, "SNOMED CT relationship ID argument cannot be null.");
		
		//if not a valid relationship ID
		if (SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(relationshipId)) {
			return null;
		}
		
		final TopDocs topDocs = getIndexServerService().search(branchPath, SnomedMappings.newQuery().type(RELATIONSHIP_NUMBER).id(relationshipId).matchAll(), 1);
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return null;
		}
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		final Document doc = getIndexServerService().document(branchPath, scoreDoc.doc, RELATIONSHIP_FIELDS_TO_LOAD);
		
		final String sourceId = doc.get(RELATIONSHIP_OBJECT_ID);
		final String typeId = SnomedMappings.relationshipType().getValueAsString(doc);
		final String destinationId = doc.get(RELATIONSHIP_VALUE_ID);
		final String negated = 0 == Mappings.intField(RELATIONSHIP_DESTINATION_NEGATED).getValue(doc) ? "" : "NOT";
		return new String[] { sourceId, typeId, destinationId, negated };
	}
	
	@Override
	public boolean isActive(final IBranchPath branchPath, final long storageKey) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		return isActive(branchPath, new LongArrayList(new long[] { storageKey })).get(0);
	}

	@Override
	public BitSet isActive(final IBranchPath branchPath, final LongList storageKeys) {
		if (null == storageKeys || storageKeys.isEmpty()) {
			return new BitSet(0);
		}
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			final BitSet $ = new BitSet(storageKeys.size());
			
			int i = 0;
			for (final LongListIterator itr = storageKeys.listIterator(); itr.hasNext(); /*not much*/) {
				
				$.set(i++, isActive(itr.next(), indexService, searcher));
				
			}
			return $;
		} catch (final IOException e) {
			LOGGER.error("Error while getting status for components.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
		
	}

	@Override
	public long getDescriptionStorageKey(final IBranchPath branchPath, final String descriptionId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(branchPath, "Concept ID argument cannot be null.");
		
		final TopDocs topDocs = getIndexServerService().search(branchPath, SnomedMappings.newQuery().type(DESCRIPTION_NUMBER).id(descriptionId).matchAll(), 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1L;
		}
		
		final Document doc = getIndexServerService().document(branchPath, topDocs.scoreDocs[0].doc, Mappings.fieldsToLoad().storageKey().build());
		return Mappings.storageKey().getValue(doc);
	}
	
	@Override
	public boolean descriptionExists(final IBranchPath branchPath, final String descriptionId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(descriptionId, "Description ID argument cannot be null.");
		return componentExists(branchPath, descriptionId, SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER);
	}
	
	@Override
	public boolean relationshipExists(final IBranchPath branchPath, final String relationshipId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(relationshipId, "Relationship ID argument cannot be null.");
		return componentExists(branchPath, relationshipId, SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER);
	}

	@Override
	public boolean componentExists(final IBranchPath branchPath, final String componentId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "SNOMED CT core component ID argument cannot be null.");
		
		try {
			final Query query = SnomedMappings.newQuery().id(componentId).matchAll();
			return getIndexServerService().getHitCount(branchPath, query, null) > 0;
		} catch (final NumberFormatException e) {
			LOGGER.warn("Invalid SNOMED CT core component ID. ID: '" + componentId + "'.");
			return false;
		}
		
	}
	
	@Override
	public LongSet getAllReferringMembersStorageKey(final IBranchPath branchPath, final String componentId, final int typeOrdinal, final int... otherTypeOrdinal) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "SNOMED CT component ID argument cannot be null.");
		
		final Set<SnomedRefSetType> types = EnumSet.noneOf(SnomedRefSetType.class);

		

		final SnomedQueryBuilder typeQuery = SnomedMappings.newQuery().memberRefSetType(typeOrdinal);
		types.add(SnomedRefSetType.get(typeOrdinal));
		
		for (final int otherType : otherTypeOrdinal) {
			typeQuery.memberRefSetType(otherType);
			types.add(SnomedRefSetType.get(otherType));
		}
		
		
		final SnomedQueryBuilder idQuery = SnomedMappings.newQuery();
		idQuery.memberReferencedComponentId(componentId);
		
		for (final SnomedRefSetType type : types) {
			final String field = SnomedRefSetUtil.getSpecialComponentIdIndexField(type);
			if (!StringUtils.isEmpty(field)) {
				idQuery.field(field, componentId);
			}
		}
		
		final Query query = SnomedMappings.newQuery()
				.and(typeQuery.matchAny()) // at least one of the type queries have to match
				.and(idQuery.matchAny()) // at least one of the ID queries have to match
				.matchAll();
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		
		final int maxDoc = indexService.maxDoc(branchPath);
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			indexService.search(branchPath, query, collector);
			
			final int hitCount = collector.getDocIDs().size();
			
			if (0 == hitCount) {
				
				return LongCollections.emptySet();
				
			}
			
			final LongSet $ = new LongOpenHashSet(hitCount);

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), Mappings.fieldsToLoad().storageKey().build());
				$.add(Mappings.storageKey().getValue(doc));
			}
			
			return $;
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting storage keys for components.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
	}
	
	@Override
	public Collection<IdStorageKeyPair> getAllComponentIdStorageKeys(final IBranchPath branchPath, final short terminologyComponentId) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		Query query = null;
		Set<String> fieldsToLoad = null;
		String idField = SnomedMappings.id().fieldName();
		IndexField<Long> storageKeyField = Mappings.storageKey();
		
		switch (terminologyComponentId) {
			
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER: //$FALL-THROUGH$
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER: //$FALL-THROUGH$
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER: //$FALL-THROUGH$
				
				query = SnomedMappings.newQuery().type(terminologyComponentId).matchAll();
				fieldsToLoad = COMPONENT_ID_STORAGE_KEY_TO_LOAD;
				break;
			case SnomedTerminologyComponentConstants.REFSET_NUMBER:
				query = SnomedMappings.newQuery().type(terminologyComponentId).matchAll();
				fieldsToLoad = SnomedMappings.fieldsToLoad().id().refSetStorageKey().build();
				storageKeyField = SnomedMappings.refSetStorageKey();
				break;
			case SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER:
				
				query = new PrefixQuery(new Term(REFERENCE_SET_MEMBER_UUID));
				fieldsToLoad = MEMBER_UUID_STORAGE_KEY_TO_LOAD;
				idField = REFERENCE_SET_MEMBER_UUID;
				break;
				
			default:
				throw new IllegalArgumentException("Unknown terminology component ID for SNOMED CT: '" + terminologyComponentId + "'.");
			
				
		}

		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		
		final int maxDoc = indexService.maxDoc(branchPath);
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			indexService.search(branchPath, query, collector);
			
			final int hitCount = collector.getDocIDs().size();
			final IdStorageKeyPair[] $ = new IdStorageKeyPair[hitCount];

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			int i = 0;
			while (itr.next()) {
				
				final Document doc = searcher.doc(itr.getDocID(), fieldsToLoad);
				$[i++] = new IdStorageKeyPair(
						checkNotNull(doc.get(idField), "Cannot get ID field for document. [" + doc + "]"),
						storageKeyField.getValue(doc));
				
			}
			
			return Arrays.asList($);
			
		} catch (final IOException e) {
			
			LOGGER.error("Error while getting component ID and storage keys for components.");
			throw new SnowowlRuntimeException(e);
			
		} finally {
			
			if (null != manager && null != searcher) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
					
				}
				
			}
			
		}
		
		
	}
	
	@Override
	public Collection<IdStorageKeyPair> getAllMemberIdStorageKeys(final IBranchPath branchPath, final int refSetTypeOrdinal) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(SnomedRefSetType.get(refSetTypeOrdinal), "SNOMED CT reference set type was null for ordinal. Ordinal: " + refSetTypeOrdinal);;
		
		final Query query = SnomedMappings.newQuery().memberRefSetType(refSetTypeOrdinal).matchAll();
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		
		final int maxDoc = indexService.maxDoc(branchPath);
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			indexService.search(branchPath, query, collector);
			
			final int hitCount = collector.getDocIDs().size();
			final IdStorageKeyPair[] $ = new IdStorageKeyPair[hitCount];

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			int i = 0;
			while (itr.next()) {
				
				final Document doc = searcher.doc(itr.getDocID(), MEMBER_UUID_STORAGE_KEY_TO_LOAD);
				$[i++] = new IdStorageKeyPair(
						checkNotNull(doc.get(REFERENCE_SET_MEMBER_UUID), "Cannot get UUID field for document. [" + doc + "]"),
						Mappings.storageKey().getValue(doc));
				
			}
			
			return Arrays.asList($);
			
		} catch (final IOException e) {
			
			LOGGER.error("Error while getting component ID and storage keys for components.");
			throw new SnowowlRuntimeException(e);
			
		} finally {
			
			if (null != manager && null != searcher) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
					
				}
				
			}
			
		}
	}
	
	@Override
	public LongSet getAllDescriptionIds(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		
		final int maxDoc = indexService.maxDoc(branchPath);
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			indexService.search(branchPath, SnomedMappings.newQuery().type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER).matchAll(), collector);
			
			final int hitCount = collector.getDocIDs().size();
			
			if (0 == hitCount) {
				return LongCollections.emptySet();
			}
			
			final LongSet $ = new LongOpenHashSet(hitCount);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), COMPONENT_ID_KEY_TO_LOAD);
				$.add(SnomedMappings.id().getValue(doc));
			}
			
			return $;
		} catch (final IOException e) {
			LOGGER.error("Error while getting all description IDs.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
	}
	
	@Override
	public String[] getIconId(final IBranchPath branchPath, final String... conceptIds) {
	
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptIds, "SNOMED CT concept ID argument cannot be null.");
	
		final String[] $ = new String[conceptIds.length];
	
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
	
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
	
		try {
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
	
			for (int i = 0; i < conceptIds.length; i++) {
	
				$[i] = getIconId(conceptIds[i], searcher);
	
			}
		} catch (final IOException e) {
			LOGGER.error("Error while searching for component image IDs.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
		return $;
	}

	@Override
	public LongSet getComponentByRefSetIdAndReferencedComponent(final IBranchPath branchPath, final String refSetId, final short referencedComponentType) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(refSetId, "Reference set ID argument cannot be null.");
		
		IndexSearcher searcher = null;
		ReferenceManager<IndexSearcher> manager = null;
		
		try {
			
			@SuppressWarnings("rawtypes")
			final IndexServerService indexService = getIndexServerService();
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();

			final Set<String> referencedComponentIds = getReferencedComponentIdsByRefSetId(branchPath, indexService, manager, searcher, refSetId);
			final LongSet $ = getComponentStorageKeysByRefSetIdsAndComponentType(branchPath, indexService, manager, searcher, referencedComponentIds, referencedComponentType);

			return $;
		} catch (final IOException e) {
			LOGGER.error("Error while getting reference set member referenced component storage keys.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
	}

	@Override
	public LongSet getAllRefSetIds(final IBranchPath branchPath) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		IndexSearcher searcher = null;
		ReferenceManager<IndexSearcher> manager = null;
		
		try {
			
			@SuppressWarnings("rawtypes")
			final IndexServerService indexService = getIndexServerService();
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();
			
			final int maxDoc = indexService.maxDoc(branchPath);
			final DocIdCollector collector = DocIdCollector.create(maxDoc);
			
			indexService.search(branchPath, SnomedMappings.newQuery().refSet().matchAll(), collector);
			
			final LongSet $ = new LongOpenHashSet();
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			while (itr.next()) {
				Document doc = searcher.doc(itr.getDocID(), COMPONENT_ID_KEY_TO_LOAD);
				$.add(SnomedMappings.id().getValue(doc));
			}
			
			return $;
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting all reference set identifier concept IDs.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
			
		}
	}
	
	@Override
	public LongKeyLongMap getConceptModuleMapping(final IBranchPath branchPath) {
		
		checkNotNull(branchPath, "branchPath");

		final int maxDoc = getIndexServerService().maxDoc(branchPath);
		
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		getIndexServerService().search(branchPath, SnomedMappings.newQuery().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).matchAll(), collector);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			manager = getIndexServerService().getManager(branchPath);
			searcher = manager.acquire();
			final LongKeyLongOpenHashMap ids = new LongKeyLongOpenHashMap();

			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), COMPONENT_ID_MODULE_FIELDS_TO_LOAD);
				ids.put(SnomedMappings.id().getValue(doc), SnomedMappings.module().getValue(doc));
			}
			
			return ids;
			
		} catch (final IOException e) {
			
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e1) {
					e.addSuppressed(e1);
				}
			}
			
			throw new SnowowlRuntimeException("Error while creating concept to module mapping.", e);
		}
		
	}
	
	@Override
	public Map<String, String> getReferencedConceptTerms(final IBranchPath branchPath, final String refSetId, final String... descriptionTypeId) {
	
		@Nullable Stopwatch lapTimer = null;
		@Nullable Stopwatch stopwatch = null;
		
		if (LOGGER.isDebugEnabled()) {
			lapTimer = Stopwatch.createStarted();
			stopwatch = Stopwatch.createStarted();
		}
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(refSetId, "Reference set identifier concept ID argument cannot be null.");
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			@SuppressWarnings("rawtypes") final IndexServerService service = getIndexServerService();
	
			manager = service.getManager(branchPath);
			searcher = manager.acquire();
	
			final TermQuery query = new TermQuery(new Term(CONCEPT_REFERRING_REFERENCE_SET_ID, longToPrefixCoded(refSetId)));
			final SnomedComponentLabelCollector collector = new SnomedComponentLabelCollector();
			
			
			service.search(branchPath, query, collector);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Collecting referenced concept preferred terms. Found preferred terms: " + collector.getIdLabelMapping().size() + ". [" + lapTimer + "]");
				lapTimer.reset();
				lapTimer.start();
			}
			
			final LongKeyMap idLabelMapping = collector.getIdLabelMapping();
			//label ID mapping. initial pessimistic about the capacity
			final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idLabelMapping.size() * descriptionTypeId.length);
			
			//XXX map key is lowercase on purpose
			for (final LongKeyMapIterator itr = idLabelMapping.entries(); itr.hasNext(); /* nothing */) {
				itr.next();
				$.put(String.valueOf(itr.getValue()).toLowerCase(), String.valueOf(itr.getKey()));
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Applying inverse mapping between IDs and labels. [" + lapTimer + "]");
				lapTimer.reset();
				lapTimer.start();
			}
	
			
			for (final String typeId : descriptionTypeId) {
				
				
				final LongSet conceptIds = idLabelMapping.keySet();
				for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /* nothing */) {
					
					final long conceptId = itr.next();
					final Query descriptionQuery = SnomedMappings.newQuery()
							.active()
							.descriptionType(typeId)
							.descriptionConcept(conceptId)
							.matchAll();
					final TopDocs topDocs = searcher.search(descriptionQuery, 1);
					if (!IndexUtils.isEmpty(topDocs)) {
						
						final Document doc = searcher.doc(topDocs.scoreDocs[0].doc, COMPONENT_LABEL_TO_LOAD);
						final String label = Mappings.label().getValue(doc);
						//XXX map key is lowercase on purpose
						$.put(label.toLowerCase(), String.valueOf(conceptId));
						
					}
					
				}
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Collecting additional description type terms. ID: '" + typeId + "'. [" + lapTimer + "]");
					lapTimer.reset();
					
				}
				
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Collecting referenced concept terms. Results: " + $.size() + ". [" + stopwatch + "]");
			}
			return $;
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting all reference set identifier concept IDs.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
			
		}
		
	}

	@Override
	public Pair<String, String> getMemberLabel(final IBranchPath branchPath, final String uuid) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(uuid, "uuid");
		
		final Query query = new TermQuery(new Term(REFERENCE_SET_MEMBER_UUID, uuid));
		final TopDocs topDocs = getIndexServerService().search(branchPath, query, 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		final Document doc = getIndexServerService().document(branchPath, topDocs.scoreDocs[0].doc, MEMBER_LABEL_FIELDS_TO_LOAD);
		return of(
				Mappings.label().getValue(doc), 
				doc.get(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL)
			);
	}

	
	@Override
	public Set<Pair<String, String>> getReferenceSetMemberLabels(final IBranchPath branchPath, final String refSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(refSetId, "refSetId");
		
		final Query query = SnomedMappings.newQuery().memberRefSetId(refSetId).matchAll();
		final int maxDoc = getIndexServerService().maxDoc(branchPath);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			final Set<Pair<String, String>> labels = newHashSet();
			final DocIdCollector collector = create(maxDoc);
			getIndexServerService().search(branchPath, query, collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			manager = getIndexServerService().getManager(branchPath);
			searcher = manager.acquire();

			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), MEMBER_LABEL_FIELDS_TO_LOAD);
				labels.add(of(
						Mappings.label().getValue(doc), 
						doc.get(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL)
					));
			}
			
			return labels;
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting reference set member labels for reference set: " + refSetId);
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
		
	}
	
	@Override
	public Collection<SnomedRefSetMemberFragment> getRefSetMemberFragments(final IBranchPath branchPath, final String refSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(refSetId, "branchPath");
		
		final Query refSetQuery = SnomedMappings.newQuery().refSet().id(refSetId).matchAll();
		final Query memberQuery = SnomedMappings.newQuery().memberRefSetId(refSetId).matchAll();
		final int maxDoc = getIndexServerService().maxDoc(branchPath);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			final TopDocs topDocs = getIndexServerService().search(branchPath, refSetQuery, 1);
			if (IndexUtils.isEmpty(topDocs)) {
				return emptySet();
			}

			manager = getIndexServerService().getManager(branchPath);
			searcher = manager.acquire();
			
			final Document refSetDoc = searcher.doc(topDocs.scoreDocs[0].doc, unmodifiableSet(newHashSet(REFERENCE_SET_TYPE)));
			final SnomedRefSetType refSetType = SnomedRefSetType.get(IndexUtils.getIntValue(refSetDoc.getField(REFERENCE_SET_TYPE)));
			
			final Set<SnomedRefSetMemberFragment> members = newHashSet();
			final DocIdCollector collector = create(maxDoc);
			getIndexServerService().search(branchPath, memberQuery, collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			final Set<String> memberFieldsToLoad = newHashSet(MEMBER_ID_FIELDS_TO_LOAD);
			final String additionalFieldToLoad = SnomedRefSetUtil.getSpecialComponentIdIndexField(refSetType);
			if (!isEmpty(additionalFieldToLoad)) {
				memberFieldsToLoad.add(additionalFieldToLoad);
			}

			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), memberFieldsToLoad);
				members.add(new SnomedRefSetMemberFragment(
						doc.get(REFERENCE_SET_MEMBER_UUID), 
						SnomedMappings.memberReferencedComponentId().getValueAsString(doc), 
						isEmpty(additionalFieldToLoad) ? doc.get(additionalFieldToLoad) : null, 
						SnomedMappings.active().getValue(doc) == 1));
			}
			
			return members;
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting reference set fragments from reference set: " + refSetId);
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
		
	}

	@Override
	public LongSet getAllUnpublishedComponentStorageKeys(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		return getUnpublishedStorageKeys(branchPath, SnomedMappings.newQuery().effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME).matchAll());
	}

	@Override
	public LongSet getAllCoreComponentIds(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.id().fieldName());
		getIndexServerService().search(branchPath, ALL_CORE_COMPONENTS_QUERY, collector);
		return newLongSet(collector.getValues());
	}
	
	@Override
	public Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		
		final Collection<SnomedModuleDependencyRefSetMemberFragment> modules = newHashSet();
		
		try {
			
			final int maxDoc = getIndexServerService().maxDoc(branchPath);
			final DocIdCollector collector = create(maxDoc);
			getIndexServerService().search(branchPath, SnomedMappings.newQuery().active().memberRefSetId(REFSET_MODULE_DEPENDENCY_TYPE).matchAll(), collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			while (itr.next()) {
				final Document doc = getIndexServerService().document(branchPath, itr.getDocID(), MODULE_MEMBER_FIELDS_TO_LOAD);
				modules.add(createModuleMember(doc));
			}
			
		} catch (final IOException e) {
			final String message = "Error while resolving dependencies between existing modules.";
			LOGGER.error(message, e);
			throw new SnowowlRuntimeException(message, e);
		}
		
		return modules;
	}
	
	@Override
	public Map<String, Date> getExistingModulesWithEffectiveTime(final IBranchPath branchPath) {
		final ImmutableSet<SnomedModuleDependencyRefSetMemberFragment> existingModules = ImmutableSet.copyOf(getExistingModules(branchPath));

		final Set<String> existingModuleIds = newHashSet();
		for (final SnomedModuleDependencyRefSetMemberFragment fragment : existingModules) {
			existingModuleIds.add(fragment.getModuleId());
			existingModuleIds.add(fragment.getReferencedComponentId());
		}

		final Map<String, Date> modules = newHashMap();

		for (final String moduleId : existingModuleIds) {
			Date date = null;
			for (final SnomedModuleDependencyRefSetMemberFragment fragment : existingModules) {
				if (null != fragment.getSourceEffectiveTime() && fragment.getModuleId().equals(moduleId)) {
					date = date == null ? fragment.getSourceEffectiveTime() : fragment.getSourceEffectiveTime().compareTo(date) > 0 ? fragment.getSourceEffectiveTime() : date;
				} else if (null != fragment.getTargetEffectiveTime() && fragment.getReferencedComponentId().equals(moduleId)) {
					date = date == null ? fragment.getTargetEffectiveTime() : fragment.getTargetEffectiveTime().compareTo(date) > 0 ? fragment.getTargetEffectiveTime() : date;
				}
			}
			modules.put(moduleId, date);
		}
		return modules;
	}
	
	@Override
	public LongSet getSelfAndAllSubtypeStorageKeysForInactivation(final IBranchPath branchPath, final String... focusConceptIds) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(focusConceptIds, "focusConceptIds");
		final SnomedConceptInactivationIdCollector collector = new SnomedConceptInactivationIdCollector();
		final Collection<String> conceptIds = collector.collectSelfAndDescendantConceptIds(branchPath, focusConceptIds);
		final SnomedTerminologyBrowser terminologyBrowser = getServiceForClass(SnomedTerminologyBrowser.class);
		return newLongSet(LongSets.transform(conceptIds, new LongSets.LongFunction<String>() {
			@Override
			public long apply(final String conceptId) {
				return terminologyBrowser.getStorageKey(branchPath, conceptId);
			}
		}));
	}
	
	private SnomedModuleDependencyRefSetMemberFragment createModuleMember(final Document doc) {
		final SnomedModuleDependencyRefSetMemberFragment module = new SnomedModuleDependencyRefSetMemberFragment();
		module.setModuleId(SnomedMappings.module().getValueAsString(doc));
		module.setReferencedComponentId(SnomedMappings.memberReferencedComponentId().getValueAsString(doc));
		module.setStorageKey(Mappings.storageKey().getValue(doc));
		module.setSourceEffectiveTime(EffectiveTimes.toDate(getLongValue(doc.getField(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME))));
		module.setTargetEffectiveTime(EffectiveTimes.toDate(getLongValue(doc.getField(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME))));
		return module;
	}
	
	@Override
	public <V> Multimap<String, V> getAllConcreteDomainsForName(final IBranchPath branchPath, final String concreteDomainName) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(concreteDomainName, "concreteDomainName");
		
		final Query query = SnomedMappings.newQuery().active().and(new TermQuery(new Term(COMPONENT_LABEL_SORT_KEY, IndexUtils.getSortKey(concreteDomainName)))).matchAll();
		final AtomicReference<IndexSearcher> searcher = new AtomicReference<IndexSearcher>();
		final DocIdCollector collector = DocIdCollector.create(getIndexServerService().maxDoc(branchPath));

		ReferenceManager<IndexSearcher> manager = null;

		try {

			//we have to determine the type of the concrete domain first
			final TopDocs topDocs = getIndexServerService().search(branchPath, query, 1);
			if (isEmpty(topDocs)) {
				return ImmutableMultimap.<String, V>of();
			}

			manager = getIndexServerService().getManager(branchPath);
			searcher.set(manager.acquire());
			
			final Document dataTypeDoc = searcher.get().doc(topDocs.scoreDocs[0].doc, MEMBER_REF_SET_ID_FILDS_TO_LOAD);
			final com.b2international.snowowl.snomed.snomedrefset.DataType dataType = // 
					SnomedRefSetUtil.DATA_TYPE_BIMAP.get(getDataType(SnomedMappings.memberRefSetId().getValueAsString(dataTypeDoc)));
			
			getIndexServerService().search(branchPath, query, collector);

			final Multimap<String, V> componentsToDataTypeValues = synchronizedMultimap(HashMultimap.<String, V>create());
			IndexUtils.parallelForEachDocId(collector.getDocIDs(), new IndexUtils.DocIdProcedure() {
				@Override
				public void apply(final int docId) throws IOException {
					final Document doc = searcher.get().doc(docId, DATA_TYPE_VALUE_AND_REFERENCED_COMPONENT_FIELDS_TO_LOAD);
					final String componentId = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
					final IndexableField[] valueFields = doc.getFields(REFERENCE_SET_MEMBER_SERIALIZED_VALUE);
					final V[] values = (V[]) new Object[valueFields.length];
					int i = 0;
					for (final IndexableField valueField : valueFields) {
						values[i++] = deserializeValue(dataType, valueField.stringValue()); 
					}
					
					componentsToDataTypeValues.putAll(componentId, Arrays.asList(values));
					
				}
			});

			return componentsToDataTypeValues;
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting concrete domains for name '" + concreteDomainName + "'.", e);
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
	public Multimap<String, String> getFullySpecifiedNameToIdsMapping(final IBranchPath branchPath, final String languageRefSetId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(languageRefSetId, "languageRefSetId");

		final Query activePreferredLanguageMembersQuery = SnomedMappings.newQuery().and(PREFERRED_LANGUAGE_QUERY).active().memberRefSetId(languageRefSetId).matchAll();
		final Query activeFsnsQuery = SnomedMappings.newQuery().active().descriptionType(Concepts.FULLY_SPECIFIED_NAME).matchAll();
		
		final Multimap<String, String> fsnToIdsMapping = Multimaps.synchronizedSetMultimap(HashMultimap.<String, String>create());

		final int maxDoc = getIndexServerService().maxDoc(branchPath);
		
		ReferenceManager<IndexSearcher> manager = null;
		final AtomicReference<IndexSearcher> searcher = new AtomicReference<IndexSearcher>();
		
		try {
			
			manager = getIndexServerService().getManager(branchPath);
			searcher.set(manager.acquire());

			final Collection<Long> preferredDescriptionIds = newConcurrentHashSet();
			
			final DocIdCollector preferredMemberDocIdCollector = DocIdCollector.create(maxDoc);
			getIndexServerService().search(branchPath, activePreferredLanguageMembersQuery, preferredMemberDocIdCollector);
			parallelForEachDocId(preferredMemberDocIdCollector.getDocIDs(), new IndexUtils.DocIdProcedure() {
				@Override
				public void apply(final int docId) throws IOException {
					final Document doc = searcher.get().doc(docId, MEMBER_REFERENCED_COMPONENT_ID_FIELDS_TO_LOAD);
					preferredDescriptionIds.add(SnomedMappings.memberReferencedComponentId().getValue(doc));
				}
			});

			final LongSet descriptionIds = newLongSet(preferredDescriptionIds);
			final DocIdCollector fsnDocIdCollector = DocIdCollector.create(maxDoc);
			final LongCollection activeConceptIds = getTerminologyBrowser().getAllActiveConceptIds(branchPath);

			getIndexServerService().search(branchPath, activeFsnsQuery, fsnDocIdCollector);
			parallelForEachDocId(fsnDocIdCollector.getDocIDs(), new IndexUtils.DocIdProcedure() {
				@Override
				public void apply(final int docId) throws IOException {
					final Document doc = searcher.get().doc(docId, FSN_DESCRIPTION_FIELDS_TO_LOAD);
					final long descriptionId = SnomedMappings.id().getValue(doc);
					
					//FSN is applicable for the specified language
					if (descriptionIds.contains(descriptionId)) {
						final long conceptId = SnomedMappings.descriptionConcept().getValue(doc);
						
						if (activeConceptIds.contains(conceptId)) {
							final String label = Mappings.label().getValue(doc);
							fsnToIdsMapping.put(label, Long.toString(conceptId));
						}
					}
					
				}
			});
			
		} catch (final IOException e) {
			throw new IndexException("Error while creating FSN to concept IDs mapping.", e);
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

		return HashMultimap.create(fsnToIdsMapping);
		
	}
	
	@Override
	public Multimap<String, String> getPreferredTermToIdsMapping(final IBranchPath branchPath, final String focusConceptId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(focusConceptId, "focusConceptId");

		final Query subtypeOrSelfQuery = SnomedMappings.newQuery().parent(focusConceptId).ancestor(focusConceptId).id(focusConceptId).matchAny();
		final Query query = SnomedMappings.newQuery().active().type(CONCEPT_NUMBER).and(subtypeOrSelfQuery).matchAll();
		
		final SnomedComponentLabelCollector collector = new SnomedComponentLabelCollector();
		getIndexServerService().search(branchPath, query, collector);

		final Multimap<String, String> ptToIdsMapping = HashMultimap.create();
		
		for (final LongKeyMapIterator itr = collector.getIdLabelMapping().entries(); itr.hasNext(); /**/) {
			itr.next();
			final String id = Long.toString(itr.getKey());
			final String pt = StringUtils.valueOfOrEmptyString(itr.getValue());
			ptToIdsMapping.put(pt, id);
		}
		
		return ptToIdsMapping;
	}
	
	private LongSet getUnpublishedStorageKeys(final IBranchPath branchPath, final Query query) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(query, "query");
		
		final int maxDoc = getIndexServerService().maxDoc(branchPath);
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		getIndexServerService().search(branchPath, query, collector);
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			final LongSet storageKeys = newLongSetWithMurMur3Hash();
			manager = getIndexServerService().getManager(branchPath);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			searcher = manager.acquire();
			
			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), Mappings.fieldsToLoad().storageKey().build());
				storageKeys.add(Mappings.storageKey().getValue(doc));
			}
			
			return storageKeys;
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting unpublished component storage keys.");
			throw new SnowowlRuntimeException(e);
		} finally {
			
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
		
	}
	
	@Override
	public Map<String, Boolean> getDescriptionPreferabilityMap(final IBranchPath branchPath, final String conceptId, final String languageRefSetId) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptId, "conceptId");
		checkNotNull(languageRefSetId, "languageRefSetId");
		
		final Query descriptionQuery = SnomedMappings.newQuery().active().descriptionConcept(conceptId).matchAll();
		
		final Collection<String> descriptionIds = newHashSet(getIndexServerService().searchUnorderedIds(branchPath, descriptionQuery, null));
		if (isEmpty(descriptionIds)) {
			return emptyMap();
		}

		final SnomedQueryBuilder referencedComponentQuery = SnomedMappings.newQuery();
		for (final String descriptionId : descriptionIds) {
			referencedComponentQuery.memberReferencedComponentId(descriptionId);
		}
		
		final Query languageMemberQuery = SnomedMappings.newQuery()
				.active()
				.memberRefSetId(languageRefSetId)
				.and(PREFERRED_LANGUAGE_QUERY)
				.and(referencedComponentQuery.matchAny())
				.matchAll();
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		final Map<String, Boolean> preferabilityMap = newHashMap(toMap(descriptionIds, ALWAYS_FALSE_FUNC));

		try {
			
			final DocIdCollector collector = DocIdCollector.create(getIndexServerService().maxDoc(branchPath));
			getIndexServerService().search(branchPath, languageMemberQuery, collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();

			manager = getIndexServerService().getManager(branchPath);
			searcher = manager.acquire();
			
			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), MEMBER_REFERENCED_COMPONENT_ID_FIELDS_TO_LOAD);
				final String referencedComponentId = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
				preferabilityMap.put(referencedComponentId, Boolean.TRUE);
			}
			
		} catch (final IOException e) {
			LOGGER.error("Error while getting description preferability mapping for concept '" + conceptId + "' on '" + branchPath + "' branch.");
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
				}
			}
		}
		
		return preferabilityMap;
		
	}
	
	@Override
	public String getDescriptionInactivationId(final IBranchPath branchPath, final String descriptionId) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(descriptionId, "descriptionId");
		final Query query = SnomedMappings.newQuery().memberReferencedComponentId(descriptionId).and(DESCRIPTION_INACTIVATION_REFSET_QUERY).matchAll();
		final TopDocs topDocs = getIndexServerService().search(branchPath, query, 1);
		if (isEmpty(topDocs)) {
			return null;
		}
		final Document doc = getIndexServerService().document(branchPath, topDocs.scoreDocs[0].doc, MEMBER_VALUE_ID_FIELDS_TO_LOAD);
		return doc.get(REFERENCE_SET_MEMBER_VALUE_ID);
	}
	
	@Override
	public String getOntologyStatistics(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		
		final int conceptsCount = getServiceForClass(SnomedTerminologyBrowser.class).getAllSubTypeCountById(branchPath, ROOT_CONCEPT);
		
		final int descriptionsCount = getIndexServerService().getHitCount(branchPath, SnomedMappings.newQuery().active().type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER).matchAll(), null);
		final int relationshipsCount = getIndexServerService().getHitCount(branchPath, SnomedMappings.newQuery().active().type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER).matchAll(), null);
		
		final SnomedQueryBuilder definingTypeQuery = SnomedMappings.newQuery();
		for (final String definingCharacteristicTypeId : DEFINING_CHARACTERISTIC_TYPES) {
			definingTypeQuery.relationshipCharacteristicType(definingCharacteristicTypeId);
		}
		final Query activeDefiningRelationshipsQuery = SnomedMappings.newQuery()
				.active()
				.type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER)
				.and(definingTypeQuery.matchAny())
				.matchAll();
		final int definingRelationshipsCount = getIndexServerService().getHitCount(branchPath, activeDefiningRelationshipsQuery, null);
		
		final Query activeConcreteDomainQuery = SnomedMappings.newQuery().active().memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE).matchAll();
		final int concreteDomainCount = getIndexServerService().getHitCount(branchPath, activeConcreteDomainQuery, null);
		
		final int languageCount = getIndexServerService().getHitCount(branchPath, SnomedMappings.newQuery().memberRefSetType(SnomedRefSetType.LANGUAGE).active().matchAll(), null);
		
		final SnomedQueryBuilder mappingTypeQuery = SnomedMappings.newQuery();
		for (final SnomedRefSetType mappingType : filter(SnomedRefSetType.VALUES, new Predicate<SnomedRefSetType>() {
			@Override
			public boolean apply(final SnomedRefSetType type) {
				return isMapping(type);
			}
		})) {
			mappingTypeQuery.memberRefSetType(mappingType.ordinal());
		}
		final Query activeMappingsQuery = SnomedMappings.newQuery().active().and(mappingTypeQuery.matchAny()).matchAll();
		final int mappingCount = getIndexServerService().getHitCount(branchPath, activeMappingsQuery, null);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("SNOMED CT ontology statistics on '");
		sb.append(branchPath.getPath());
		sb.append("':\n");
		
		sb.append("Number of active concepts: ");
		sb.append(getIntegerInstance().format(conceptsCount));
		sb.append("\n");
		
		sb.append("Number of active descriptions: ");
		sb.append(getIntegerInstance().format(descriptionsCount));
		sb.append("\n");
		
		sb.append("Number of active relationships: ");
		sb.append(getIntegerInstance().format(relationshipsCount));
		sb.append("\n");
		
		sb.append("Number of active defining relationships: ");
		sb.append(getIntegerInstance().format(definingRelationshipsCount));
		sb.append("\n");
		
		sb.append("Number of active concrete domains: ");
		sb.append(getIntegerInstance().format(concreteDomainCount));
		sb.append("\n");
		
		sb.append("Number of active language type reference set members: ");
		sb.append(getIntegerInstance().format(languageCount));
		sb.append("\n");
		
		sb.append("Number of active mapping type reference set members: ");
		sb.append(getIntegerInstance().format(mappingCount));
		sb.append("\n");
		
		return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private Set<String> getReferencedComponentIdsByRefSetId(final IBranchPath branchPath, final IndexServerService indexService, 
			final ReferenceManager<IndexSearcher> manager, final IndexSearcher searcher, final String refSetId) throws IOException {
		
		final Query query = SnomedMappings.newQuery().memberRefSetId(refSetId).matchAll();
		
		final int maxDoc = indexService.maxDoc(branchPath);
		final DocIdCollector collector = DocIdCollector.create(maxDoc);
		indexService.search(branchPath, query, collector);
		final int hitCount = collector.getDocIDs().size();

		if (0 == hitCount) {
			return Collections.emptySet();
		}


		final Set<String> referencedComponentIds = newHashSet(); 
		final DocIdsIterator itr = collector.getDocIDs().iterator();
		while (itr.next()) {
			final Document doc = searcher.doc(itr.getDocID(), MEMBER_REFERENCED_COMPONENT_ID_FIELDS_TO_LOAD);
			referencedComponentIds.add(SnomedMappings.memberReferencedComponentId().getValueAsString(doc));
		}
		return referencedComponentIds;
	}

	@SuppressWarnings("rawtypes")
	private LongSet getComponentStorageKeysByRefSetIdsAndComponentType(final IBranchPath branchPath, final IndexServerService indexService, 
			final ReferenceManager<IndexSearcher> manager, final IndexSearcher searcher, final Set<String> referencedComponentIds, final short referencedComponentType) throws IOException {
		
		final LongSet storageKeys = new LongOpenHashSet();
		final int maxDoc = indexService.maxDoc(branchPath);
		
		BooleanQuery query = new BooleanQuery(true);
		for (final String referencedComponentId : referencedComponentIds) {
			query.add(SnomedMappings.newQuery().type(referencedComponentType).id(referencedComponentId).matchAll(), SHOULD);

			if (query.getClauses().length + 1 == BooleanQuery.getMaxClauseCount()) {
				storageKeys.addAll(getStorageKeys(query, indexService, searcher, maxDoc, branchPath));
				query = new BooleanQuery(true);
			}
		}

		if (query.getClauses().length > 0) {
			storageKeys.addAll(getStorageKeys(query, indexService, searcher, maxDoc, branchPath));
		}

		return storageKeys;
	}

	/*
	 * Returns true if the component with the given type exists.
	 */
	private boolean componentExists(final IBranchPath branchPath, final String componentId, final int componentType) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "Component ID argument cannot be null.");
		return getIndexServerService().getHitCount(branchPath, SnomedMappings.newQuery().type(componentType).id(componentId).matchAll(), null) > 0;
	}
	
	/*returns with the server side index service.*/
	@SuppressWarnings("rawtypes")
	private IndexServerService getIndexServerService() {
		return (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}
	

	/*returns true only and if only the SNOMED CT component identified by its unique ID is active. Otherwise false.*/
	private boolean isActive(final long storageKey, final IndexServerService<?> service, final IndexSearcher searcher) throws IOException {
		final Query query = SnomedMappings.newQuery().storageKey(storageKey).matchAll();
		final TopDocs topDocs = searcher.search(query, 1);
		// cannot found matching component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return false;
		}
		
		final Document doc = service.document(searcher, topDocs.scoreDocs[0].doc, COMPONENT_STATUS_TO_LOAD);
		return SnomedMappings.active().getValue(doc) == 1;
		
	}
	
	/*returns with the label of the component if any. this method may return with null.*/
	@Nullable private String getComponentLabel(final IBranchPath branchPath, final String componentId, final IndexServerService<?> service, final IndexSearcher searcher) throws IOException {
		
		Query labelQuery = null;
		
		final short componentType = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId);
		switch (componentType) {
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER: //$FALL-THROUGH$
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER: //$FALL-THROUGH$
				labelQuery = SnomedMappings.newQuery().type(componentType).id(componentId).matchAll();
				break;
				
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				final String[] properties = getRelationshipProperties(branchPath, componentId);
				return isEmpty(properties) ? null : Joiner.on(" - ").join(getLabels(branchPath, properties[0], properties[1], properties[2]));
				
			default:
				//no chance to find SNOMED CT core component label in index, fall back to CDO
				return null;
		}

		final TopDocs topDocs = searcher.search(labelQuery, 1);
		
		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			
			return null;
			
		}
		
		final Document doc = service.document(searcher, topDocs.scoreDocs[0].doc, COMPONENT_LABEL_TO_LOAD);
		
		//could be null
		return Mappings.label().getValue(doc);
	}
	
	private String getIconId(final String conceptId, final IndexSearcher searcher) throws IOException {

		checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");

		final Query query = SnomedMappings.newQuery().type(CONCEPT_NUMBER).id(conceptId).matchAll();
		final TopDocs topDocs = searcher.search(query, 1);
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return null;
		}

		final Document doc = getIndexServerService().document(searcher, topDocs.scoreDocs[0].doc, COMPONENT_ICON_ID_TO_LOAD);

		if (null == doc) {
			return SnomedIconProvider.getInstance().getIconId(conceptId);
		}

		final String iconId = Mappings.iconId().getValue(doc);

		return StringUtils.isEmpty(iconId) ? null : iconId;

	}
	
	/*returns with the identifier concept ID of the currently used language setting specified 
	 * by the selected SNOMED CT language type reference set*/
	private String getLanguageRefSetId() {
		return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
	}
	
	/*loads the value to the cache identified with its unique key*/
	private Object loadValue(final IBranchPath branchPath, final CacheKeyType type) {
		switch (type) {
			case SYNONYM_AND_DESCENDATNTS:
				return getConceptAndItsDescendants(branchPath, getTerminologyBrowser(), Concepts.SYNONYM);
			case AVAILABLE_PREFERRED_TERM_IDS:
				return getAvailablePreferredTypeIds(branchPath);
			case DATA_TYPE_LABELS:
				return getDataTypeLabels(branchPath);
			case AVAILABLE_DESCRIPTION_IDS:
				return getAvailableDescriptionTypes(branchPath);
			case NAMESPACE_IDS:
				return getNameSpaceIds(branchPath);
			case PREDICATE_TYPES:
				return getAllPredicates(branchPath);
			case REFERENCE_SET_CDO_IDS:
				return internalGetRefSetCdoIdIdMapping(branchPath);
			default: 
				throw new IllegalArgumentException("Unknown cache key type: " + type);
		}
	}

	private Map<CDOID, String> internalGetRefSetCdoIdIdMapping(final IBranchPath branchPath) {
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = getIndexServerService();
		final Query refSetTypeQuery = SnomedMappings.newQuery().refSet().matchAll();
		final int hitCount = indexService.getHitCount(branchPath, refSetTypeQuery, null);
		final TopDocs topDocs = getIndexServerService().search(branchPath, refSetTypeQuery, hitCount);
		if (isEmpty(topDocs)) {
			return emptyMap();
		}
		final Map<CDOID, String> cdoIdToIdMap = newHashMap();
		for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
			final Document doc = indexService.document(branchPath, scoreDoc.doc, SnomedMappings.fieldsToLoad().refSetStorageKey().id().build());
			final CDOID cdoId = CDOIDUtil.createLong(SnomedMappings.refSetStorageKey().getValue(doc));
			final String refSetId = SnomedMappings.id().getValueAsString(doc);
			cdoIdToIdMap.put(cdoId, refSetId);
		}
		return unmodifiableMap(cdoIdToIdMap);
	}

	private Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> getAllPredicates(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates = newHashMap();
		
		predicates.put(HierarchyInclusionType.SELF, HashMultimap.<String, PredicateIndexEntry>create());
		predicates.put(HierarchyInclusionType.DESCENDANT, HashMultimap.<String, PredicateIndexEntry>create());
		predicates.put(HierarchyInclusionType.SELF_OR_DESCENDANT, HashMultimap.<String, PredicateIndexEntry>create());
		
		final ReferenceManager<IndexSearcher> manager = null;
		final IndexSearcher searcher = null;
		
		final Map<String, PredicateIndexEntry> predicateMappings = uniqueIndex(getServiceForClass(SnomedPredicateBrowser.class).getAllPredicates(branchPath), new Function<PredicateIndexEntry, String>() {
			@Override
			public String apply(final PredicateIndexEntry predicate) {
				return predicate.getId();
			}
		});
		
		try {
			
			final IndexServerService<?> service = getIndexServerService();
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final PrefixQuery query = new PrefixQuery(new Term(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE));
			query.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
			service.search(branchPath, query, collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			while (itr.next()) {
				final Document doc = service.document(branchPath, itr.getDocID(), SnomedMappings.fieldsToLoad().id().field(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE).build());
				final IndexableField[] predicateFields = doc.getFields(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE);
				final String componentId = SnomedMappings.id().getValueAsString(doc);
				for (final IndexableField field : predicateFields) {
					final String[] split = field.stringValue().split(PredicateUtils.PREDICATE_SEPARATOR, 2);
					Preconditions.checkState(!isEmpty(split), "");
					final String predicateStorageKey = split[0];
					final String key = split[1];
					final PredicateIndexEntry predicate = predicateMappings.get(predicateStorageKey);
					final HierarchyInclusionType type = HierarchyInclusionType.get(key);
					if (type != null) {
						predicates.get(type).put(componentId, predicate);	
					} else if (SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(key) == CONCEPT_NUMBER) {
						predicates.get(HierarchyInclusionType.SELF).put(predicateStorageKey + "#" + componentId, predicate);	
					} else if (PredicateUtils.REFSET_PREDICATE_KEY_PREFIX.equals(key)) {
						predicates.get(HierarchyInclusionType.SELF).put(componentId, predicate);
					} else {
						throw new IllegalArgumentException("Cannot parse component referring predicate field: " + field.stringValue());
					}
				}
			}
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting components referenced by MRCM predicates on '" + branchPath + "' branch.", e);
		} finally {
			if (null != manager && null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					try {
						manager.release(searcher);
					} catch (final IOException e1) {
						e.addSuppressed(e1);
					}
					throw new IndexException("Error while releasing index searcher.", e);
				}
			}
		}
		
		return unmodifiableMap(predicates);
	}

	/*checks the cache refreshing job. joins to the job if the job state is not NONE. makes the flow synchronous.*/
	private void checkAndJoin(final IBranchPath branchPath, @Nullable final String message) {
		if (jobMap.containsKey(branchPath)) {
			final Job cacheLoadingJob = jobMap.get(branchPath);
			// wait for the cache refreshing process, if it's already in progress on the specified branch
			if (Job.NONE != cacheLoadingJob.getState()) {
				try {
					cacheLoadingJob.join();
				} catch (final InterruptedException e) {
					LOGGER.error(null == message ? "Error while refreshing the cache." : message, e);
				}
			}
		}
	}
	
	/*returns with the concept hierarchy browser service for the SNOMED CT terminology*/
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*returns with a set of concept identified by the specified SNOMED CT concept ID and all the descendant of the specified concept*/
	private Set<String> getConceptAndItsDescendants(final IBranchPath branchPath, final SnomedTerminologyBrowser browser, final String conceptId) {
		if (!browser.exists(branchPath, conceptId)) {
			return new HashSet<String>(); //intentionally not collections#emptySet as it may act as a producer
		}
		final Set<String> concepts = LongSets.toStringSet(browser.getAllSubTypeIds(branchPath, Long.parseLong(conceptId)));
		concepts.add(conceptId);
		return concepts;
	}
	
	/*initialize and returns with of map of data types and the available concrete domain data type labels.*/
	private Map<DataType, Set<String>> getDataTypeLabels(final IBranchPath branchPath) {
	
		final Map<DataType, Set<String>> dataTypeLabelMap = Maps.newHashMapWithExpectedSize(DataType.values().length);
		
		@SuppressWarnings("rawtypes")
		final IndexServerService service = (IndexServerService) getRefSetIndexService();
		
		final ReducedConcreteDomainFragmentCollector collector = new ReducedConcreteDomainFragmentCollector();
		service.search(branchPath, SnomedMappings.newQuery().memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE).matchAll(), collector);

		for (final DataType  type : DataType.VALUES) {
			
			dataTypeLabelMap.put(type, Sets.<String>newHashSet());
			final BytesRefHash labelRefHash = collector.getLabels(type);
			
			if (labelRefHash.size() < 1) {
				continue;
			}
			
			//execute a fake sort, wich does nothing but exposes the containing bytes ref IDs
			//as #compact visibility is reduced from Lucene 4.3
			final int[] compact = labelRefHash.sort(new Comparator<BytesRef>() {
				@Override public int compare(final BytesRef o1, final BytesRef o2) { return 0; }
			});
			
			final BytesRef spare = new BytesRef();
			
			for (final int i : compact) {
				
				if (i < 0) {
					continue;
				}
				
				labelRefHash.get(i, spare);
				dataTypeLabelMap.get(type).add(spare.utf8ToString());
			}
		}
		
		return dataTypeLabelMap;
	}

	/*initialize a map of namspace IDs and the associated extension namespance concept IDs*/
	private LongKeyLongMap getNameSpaceIds(/*ignored*/final IBranchPath branchPath) {
		
		final LongKeyLongMap map = new LongKeyLongOpenHashMap();
		
		for (final SnomedConceptIndexEntry concept : getTerminologyBrowser().getAllSubTypesById(branchPath, Concepts.NAMESPACE_ROOT)) {
			
			String namespaceId = null;
			
			//XXX SG extension namespace concept PTs does not contain namespace IDs
			if (Concepts.SINGAPORE_NATIONAL_EXTENSION.equals(concept.getId())) {
				
				namespaceId = "1000132";
				
			} else if (Concepts.SINGAPORE_DRUG_DICTIONARY_EXTENSION.equals(concept.getId())) {
				
				namespaceId = "1000133";

			} else if (Concepts.B2I_NAMESPACE.equals(concept.getId())) {

				namespaceId = "1000154";
				
			} else {

				namespaceId = CharMatcher.DIGIT.retainFrom(concept.getLabel());
				
			}
			
			if (StringUtils.isEmpty(namespaceId)) {
				namespaceId = "0"; //represents the core IHTSDO namespace
			}
			
			map.put(Long.parseLong(namespaceId), Long.parseLong(concept.getId()));
			
		}
		
		return map;
	}

	
	/*returns with the index service for SNOMED CT reference sets*/
	private SnomedIndexService getRefSetIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}
	
	/**returns with a set of allowed concepts' ID. concept is allowed as preferred description type concept if 
	 * has an associated active description type reference set member and is synonym or descendant of the synonym */
	private Set<String> getAvailablePreferredTypeIds(final IBranchPath branchPath) {
		final SnomedTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
		final LongSet preferredDescripitons = terminologyBrowser.getAllSubTypeIds(branchPath, SYNONYM_CONCEPT_ID);
		preferredDescripitons.add(SYNONYM_CONCEPT_ID);
		final SnomedRefSetMemberIndexQueryAdapter adapter = new SnomedRefSetMemberIndexQueryAdapter(Concepts.REFSET_DESCRIPTION_TYPE, null);
		final Collection<SnomedRefSetMemberIndexEntry> descriptionTypeMembers = getRefSetIndexService().searchUnsorted(branchPath, adapter);
		
		final Iterable<SnomedRefSetMemberIndexEntry> filteredMembers = Iterables.filter(descriptionTypeMembers, new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry member) {
				return member.isActive();
			}
		});
		
		final ImmutableSet<String> activeDescriptionTypeIds = ImmutableSet.copyOf(Iterables.transform(filteredMembers, new Function<SnomedRefSetMemberIndexEntry, String>() {
			@Override public String apply(final SnomedRefSetMemberIndexEntry member) {
				return member.getReferencedComponentId();
			}
		}));
		
		//as intersection is not serializable: java.io.NotSerializableException
		return newHashSet(Sets.intersection(activeDescriptionTypeIds, toStringSet(preferredDescripitons)));
	}
	
	/**returns with the FSN concept ID and the SYNONYM and all of descendants' IDs if the concepts are active and all the associated description type
	 * reference set members are active.
	 * @param branchPath
	 */
	private Map<String, Integer> getAvailableDescriptionTypes(final IBranchPath branchPath) {
		final SnomedRefSetMemberIndexQueryAdapter adapter = new SnomedRefSetMemberIndexQueryAdapter(Concepts.REFSET_DESCRIPTION_TYPE, null);
		final Collection<SnomedRefSetMemberIndexEntry> members = getRefSetIndexService().searchUnsorted(branchPath, adapter);
		
		final Iterable<SnomedRefSetMemberIndexEntry> descriptionTypeMembers = Iterables.filter(members, SnomedRefSetMemberIndexEntry.class);
		
		final Iterable<SnomedRefSetMemberIndexEntry> activeDescriptionTypeMembers = Iterables.filter(descriptionTypeMembers, new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry member) {
				return member.isActive();
			}
		});
		
		Set<String> activeDescriptionTypeIds = newHashSet(Iterables.transform(activeDescriptionTypeMembers, new Function<SnomedRefSetMemberIndexEntry, String>() {
			@Override public String apply(final SnomedRefSetMemberIndexEntry member) {
				return member.getReferencedComponentId();
			}
		}));
		
		//get all active description type concept IDs from the ontology
		final SnomedTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
		final LongSet descripitons = terminologyBrowser.getAllSubTypeIds(branchPath, DESCRIPTION_TYPE_ROOT_CONCEPT_ID);
		
		//intersection of the proper active description type concept IDs and the active description type reference set member referenced component IDs
		activeDescriptionTypeIds = Sets.intersection(activeDescriptionTypeIds, toStringSet(descripitons));
		
		final Map<String, Integer> $ = Maps.newHashMap();
		
		//if active description type member has a corresponding active concept as well.
		for (final SnomedRefSetMemberIndexEntry entry : activeDescriptionTypeMembers) {
			
			final String referencedComponentId = entry.getReferencedComponentId();
			if (activeDescriptionTypeIds.contains(referencedComponentId)) {
				$.put(referencedComponentId, entry.getDescriptionLength());
			}
			
		}
		
		return $;
	}

	/*
	 * Executes the given query and returns with the found components storage keys.
	 */
	private LongSet getStorageKeys(final Query query, final IndexServerService<?> indexService, final IndexSearcher searcher, final int maxDoc, final IBranchPath branchPath) throws IOException {
		final LongSet resultSet = new LongOpenHashSet();
		final DocIdCollector componentCollector = DocIdCollector.create(maxDoc);
		indexService.search(branchPath, query, componentCollector);
		final DocIdsIterator componentItr = componentCollector.getDocIDs().iterator();
		while (componentItr.next()) {
			final Document doc = searcher.doc(componentItr.getDocID(), SnomedMappings.fieldsToLoad().storageKey().build());
			resultSet.add(Mappings.storageKey().getValue(doc));
		}
		return resultSet;
	}

	private final class BranchCacheLoadingJob extends Job {
		private final IBranchPath branchPath;

		private BranchCacheLoadingJob(final IBranchPath branchPath) {
			super("Initializing for the '" + branchPath + "' branch.");
			this.branchPath = branchPath;
			setPriority(Job.INTERACTIVE);
			setUser(false);
			setSystem(true);
		}

		@Override protected IStatus run(final IProgressMonitor monitor) {
			monitor.beginTask("Initializing...", IProgressMonitor.UNKNOWN);
			final LoadingCache<CacheKeyType, Object> branchCache = cache.getIfPresent(branchPath);
			
			if (null != branchCache) {
				//synchronously refreshes the cache
				branchCache.invalidateAll();
				
				for (final CacheKeyType type : CacheKeyType.values()) {
					
					try {
						branchCache.getUnchecked(type);
					} catch (final UncheckedExecutionException e) {
						LOGGER.warn("Could not preload cached values for key " + type + ", skipping.");
					}
				}
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * Private enumerations for the cache keys.
	 */
	private static enum CacheKeyType {
		SYNONYM_AND_DESCENDATNTS,
		AVAILABLE_PREFERRED_TERM_IDS,
		AVAILABLE_DESCRIPTION_IDS,
		DATA_TYPE_LABELS,
		PREDICATE_TYPES,
		REFERENCE_SET_CDO_IDS,
		NAMESPACE_IDS;
	}
}
