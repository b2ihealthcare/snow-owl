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
import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.collect.LongSets.newLongSet;
import static com.b2international.commons.collect.LongSets.toStringSet;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.DEFINING_CHARACTERISTIC_TYPES;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.deserializeValue;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.isMapping;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Multimaps.synchronizedMultimap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.hash.Hashing.murmur3_32;
import static java.text.NumberFormat.getIntegerInstance;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongListIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.http.ExtendedLocale;
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
import com.b2international.snowowl.datastore.server.snomed.index.NamespaceMapping;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.UncheckedExecutionException;

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
public class SnomedComponentService implements ISnomedComponentService {

	@Override
	public Set<String> getAvailableDataTypeLabels(final IBranchPath branchPath, final DataType dataType) {
		return Collections.emptySet();
	}
	
	/*returns with the concept hierarchy browser service for the SNOMED CT terminology*/
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*initialize and returns with of map of data types and the available concrete domain data type labels.*/
	private Map<DataType, Set<String>> getDataTypeLabels(final IBranchPath branchPath) {
	
		final Map<DataType, Set<String>> dataTypeLabelMap = Maps.newHashMapWithExpectedSize(DataType.values().length);
		
		@SuppressWarnings("rawtypes")
		final IndexServerService service = (IndexServerService) getRefSetIndexService();
		
		final ReducedConcreteDomainFragmentCollector collector = new ReducedConcreteDomainFragmentCollector();
		service.search(branchPath, SnomedMappings.newQuery().memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE).matchAll(), collector);

		for (final DataType  type : DataType.values()) {
			
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

	/* initialize a map of namespace IDs and the associated extension namespace concept IDs */
	private LongKeyLongMap getNameSpaceIds(/*ignored*/final IBranchPath branchPath) {
		
		final LongKeyLongMap map = PrimitiveMaps.newLongKeyLongOpenHashMap();
		
		for (final SnomedConceptDocument concept : getTerminologyBrowser().getAllSubTypesById(branchPath, Concepts.NAMESPACE_ROOT)) {
			
			String namespaceId = null;
			
			// as of 20160112 the SG namespace identifier concepts have their namespace in the PT
			namespaceId = CharMatcher.DIGIT.retainFrom(concept.getLabel());
			
			if (StringUtils.isEmpty(namespaceId)) {
				namespaceId = "0"; //represents the core IHTSDO namespace
			}
			
			map.put(Long.parseLong(namespaceId), Long.parseLong(concept.getId()));
			
		}
		
		return map;
	}

}
