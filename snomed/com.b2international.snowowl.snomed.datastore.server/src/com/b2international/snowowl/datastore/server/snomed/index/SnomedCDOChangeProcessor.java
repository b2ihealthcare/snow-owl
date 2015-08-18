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

import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.asLong;
import static com.b2international.snowowl.snomed.mrcm.core.ConceptModelUtils.getBottomMostPredicate;
import static com.b2international.snowowl.snomed.mrcm.core.ConceptModelUtils.getContainerConstraint;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Long.parseLong;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.commons.pcj.LongCollections;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentIdAndLabel;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.LifecycleUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.AbstractIndexUpdater;
import com.b2international.snowowl.datastore.index.IDocumentUpdater;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.DefinitionType;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexQueries;
import com.b2international.snowowl.snomed.datastore.index.AbstractPredicateIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyEdge;
import com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyNode;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptDocumentMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptModelMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemIndexMappingStrategy;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

/**
 * Change processor implementation for SNOMED&nbsp;CT ontology. This class is responsible for updating indexes based on the new, dirty and detached components 
 * after a successful backend commit.
 * @see ICDOChangeProcessor
 * @see ICDOCommitChangeSet
 */
public class SnomedCDOChangeProcessor implements ICDOChangeProcessor {

	private static final Set<String> MEMBER_FIELD_TO_LOAD = 
		Collections.unmodifiableSet(Sets.newHashSet(
				SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID,
				SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID,
				SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE));

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedCDOChangeProcessor.class);
	
	/**{@code true} if the container reference set of the given member belongs to the {@link SnomedRefSetType#SIMPLE simple} type.*/
	private static final Predicate<SnomedRefSetMember> SIMPLE_TYPE_PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember member) {
			return SnomedRefSetType.SIMPLE.equals(member.getRefSet().getType());
		}
	};

	/**{@code true} if the container reference set of the given member belongs to the {@link SnomedRefSetType#SIMPLE_MAP simple map} type.*/
	private static final Predicate<SnomedRefSetMember> SIMPLE_MAP_TYPE_PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember member) {
			return SnomedRefSetType.SIMPLE_MAP.equals(member.getRefSet().getType());
		}
	};
	
	/**{@code true} if the container reference set of the given member belongs to the {@link SnomedRefSetType#ATTRIBUTE_VALUE attribute value} type.*/
	private static final Predicate<SnomedRefSetMember> ATTRIBUTE_VALUE_TYPE_PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember member) {
			return SnomedRefSetType.ATTRIBUTE_VALUE.equals(member.getRefSet().getType());
		}
	};
	
	/**Predicate for active SNOMED&nbsp;CT concepts.*/
	private static final Predicate<Concept> ACTIVE_CONCEPT_PREDICATE = new Predicate<Concept>() {
		@Override public boolean apply(final Concept concept) {
			return concept.isActive();
		}
	};

	/**Function for evaluating a concept into its unique SNOMED&nbspCT ID.*/
	private static final Function<Concept, String> CONCEPT_ID_FUNCTION = new Function<Concept, String>() {
		@Override public String apply(final Concept concept) {
			return concept.getId();
		}
	};

	/**Predicate for active SNOMED&nbsp;CT reference set members.*/
	private static final Predicate<SnomedRefSetMember> ACTIVE_MEMBER_PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember member) {
			return member.isActive();
		}
	};

	/**Predicate for SNOMED&nbsp;CT reference set members referencing a SNOMED&nbsp;CT concept.*/
	private static final Predicate<SnomedRefSetMember> CONCEPT_MEMBER_PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember member) {
			return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == member.getReferencedComponentType();
		}
	};

	/**Predicate returning {@code true} if the container reference set is a regular SNOMED&nbsp;CT reference set.*/
	private static final Predicate<SnomedRefSetMember> REGULAR_MEMBER_PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember input) {
			return input.getRefSet() instanceof SnomedRegularRefSet;
		}
	};
	
	private static Function<Component, String> GET_SCT_ID_FUNCTION = new Function<Component, String>() {
		@Override public String apply(final Component component) {
			return Preconditions.checkNotNull(component, "Component argument cannot be null.").getId();
		}
	};

	private static final Iterable<SnomedRefSetType> REFERRING_MEMBER_TYPES = Lists.newArrayList(SnomedRefSetType.SIMPLE, SnomedRefSetType.ATTRIBUTE_VALUE);
	private static final Iterable<SnomedRefSetType> MAPPING_MEMBER_TYPES = Lists.newArrayList(SnomedRefSetType.SIMPLE_MAP);
	
	private final Set<Concept> newConcepts = Sets.newHashSet();
	private final Set<Entry<CDOID, EClass>> deletedConcepts = Sets.newHashSet();
	private final Set<Concept> dirtyConcepts = Sets.newHashSet();

	private final Set<Relationship> newRelationships = Sets.newHashSet();
	private final Set<Entry<CDOID, EClass>> deletedRelationships = Sets.newHashSet();
	private final Set<Relationship> dirtyRelationships = Sets.newHashSet();
	
	private final Set<SnomedRefSet> newRefSets = Sets.newHashSet();
	private final Set<Entry<CDOID, EClass>> deletedRefSets = Sets.newHashSet();
	private final Set<SnomedRefSet> dirtyRefSets = Sets.newHashSet();
	
	private final Set<SnomedRefSetMember> newRefSetMembers = Sets.newHashSet();
	private final Set<CDOID> deletedRefSetMembers = Sets.newHashSet();
	private final Set<SnomedRefSetMember> dirtyRefSetMembers = Sets.newHashSet();
	
	private final Set<AttributeConstraint> newConstraints = Sets.newHashSet();
	private final Set<AttributeConstraint> deletedConstraints = Sets.newHashSet();
	private final Set<AttributeConstraint> dirtyConstraints = Sets.newHashSet();
	
	private final Set<Description> newDescriptions = Sets.newHashSet();
	private final Set<Entry<CDOID, EClass>> deletedDescriptions = Sets.newHashSet();
	private final Set<Description> dirtyDescriptions = Sets.newHashSet();
	
	private final Set<CodeSystem> newCodeSystems = newHashSet();
	private final Set<CodeSystemVersion> newCodeSystemVersions = newHashSet();
	private final Set<CodeSystemVersion> dirtyCodeSystemVersions = newHashSet();
	
	//the collection of the new artefacts in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> newConceptLogEntries = Collections.synchronizedSet(Sets.<ComponentIdAndLabel>newHashSet());
	
	//the collection of the deleted artefacts in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> deletedConceptLogEntries = Sets.newHashSet();
	
	//the collection of the changed artefacts in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> changedConceptLogEntries = Sets.newHashSet();
	
	//the collection of the new refset in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> newRefsetLogEntries = Sets.newHashSet();
	
	//the collection of the changed refset in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> changedRefSetLogEntries = Sets.newHashSet();
	
	//the collection of the deleted refset in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> deletedRefSetLogEntries = Sets.newHashSet();
	
	//the collection of the deleted constraints in a minimalistic form. Used for logging.
	private final Set<ComponentIdAndLabel> deletedConstraintLogEntries = Sets.newHashSet();
	
	/**Concepts that has to have {@link CommonIndexConstants#COMPONENT_COMPARE_UNIQUE_KEY compare unique key} on the {@link Document}.*/
	private final LongSet dirtyConceptIdsWithCompareUpdate = newLongSet();
	
	/**Reference sets that has to have {@link CommonIndexConstants#COMPONENT_COMPARE_UNIQUE_KEY compare unique key} on the {@link Document}.*/
	private final LongSet dirtyRefSetIdsWithCompareUpdate = newLongSet();
	
	/**
	 * A map representing reference set membership changes.
	 * <ul>
	 * <li>Keys: SNOMED&nbsp;CT concept IDs.</li>
	 * <li>Values: a set of {@link RefSetMemberChange reference set member changes}. Can be {@code null}.</li>
	 * </ul> 
	 */
	private final LongKeyMap memberChanges = new LongKeyOpenHashMap();

	private final SnomedIndexServerService indexUpdater;
	private final IBranchPath branchPath;
	private final Supplier<ISnomedTaxonomyBuilder> newTaxonomyBuilderSupplier;
	private final Supplier<ISnomedTaxonomyBuilder> previousTaxonomyBuilderSupplier;
	/**Supplies the different between the previous and current state of the ontology.
	 *<br>Assumes initialized taxonomy builders.*/
	private final Supplier<Pair<LongSet,LongSet>> differenceSupplier;
	/**Supplies a label provider for SNOMED CT concepts.
	 *<br>Assumes {@link #processNew(EObject)} and {@link #processDirty(CDOObject)} invocations. 
	 */
	private final Supplier<ConceptLabelProvider> conceptLabelProviderSupplier;
	/**Supplies a provider instance to get information about new reference set memberships of a concept.
	 *<br>Assumes {@link #processNew(EObject)} and {@link #processDirty(CDOObject)} invocations.  
	 */
	private Supplier<RefSetMembershipProvider> refSetMembershipProviderSupplier;

	/**Supplies the identifier concept IDs of all available SNOMED&nbsp;CT reference set. */
	private final Supplier<Collection<String>> allRefSetIdsSupplier;
	/**
	 * Flag indicating whether the {@link StoreThreadLocal#getSession()} can be copied or not. 
	 */
	private final boolean canCopyThreadLocal;
	
	/**Set for storing deleted concept IDs.*/
	private final LongSet deletedConceptIds = new LongOpenHashSet();
	
	/**Represents the change set.*/
	private ICDOCommitChangeSet commitChangeSet;
	
	/**Flag indicating if the commit change set effects SNOMED&nbsp;CT. 
	 * <br>{@code true} only and if only at least one SNOMED CT core component, reference set or member document has to be updated.
	 * Otherwise {@code false}.*/
	private boolean hasRelatedChanges = false;

	/**Map for storing concept IDs and predicate keys that has to be added to concept documents.*/
	private final LongKeyMap newConceptPredicateKeys = new LongKeyOpenHashMap();

	/**Map for storing concept IDs and the associated predicate keys that has to be removed from the concept document.*/
	private final LongKeyMap detachedConceptPredicateKeys = new LongKeyOpenHashMap();

	/**Map for storing reference set identifier concept IDs and predicate keys that has to be added to reference set documents.*/
	private final LongKeyMap newRefSetPredicateKeys = new LongKeyOpenHashMap();

	/**Map for storing reference set identifier concept IDs and the associated predicate keys that has to be removed from the reference set document.*/
	private final LongKeyMap detachedRefSetPredicateKeys = new LongKeyOpenHashMap();

	/**A collection of CDO view. Used when objects have to be loaded due to detachment.*/
	private final Collection<CDOView> views = Sets.newHashSet();
	
	/**
	 * Creates a new change processor for the SNOMED&nbsp;CT ontology. 
	 * @param indexUpdater the index updater for the SNOMED&nbsp;CT ontology.
	 * @param branchPath the branch path where the changes has to be calculated and processed.
	 * @param canCopyThreadLocal 
	 */
	public SnomedCDOChangeProcessor(final IIndexUpdater<SnomedIndexEntry> indexUpdater, final IBranchPath branchPath, final boolean canCopyThreadLocal) {
		
		Preconditions.checkNotNull(indexUpdater, "Index service argument cannot be null.");
		Preconditions.checkArgument(indexUpdater instanceof AbstractIndexUpdater, "Index updater must be instance of " + AbstractIndexUpdater.class + ". Was " + indexUpdater.getClass());

		this.indexUpdater = (SnomedIndexServerService) indexUpdater; 
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.canCopyThreadLocal = canCopyThreadLocal;
		
		newTaxonomyBuilderSupplier = Suppliers.memoize(new Supplier<ISnomedTaxonomyBuilder>() {
			@Override public ISnomedTaxonomyBuilder get() {
				return new SnomedTaxonomyBuilder(branchPath);
			}
		});
		
		previousTaxonomyBuilderSupplier = Suppliers.memoize(new Supplier<ISnomedTaxonomyBuilder>() {
			@Override public ISnomedTaxonomyBuilder get() {
				final SnomedTaxonomyBuilder newBuilder = (SnomedTaxonomyBuilder) newTaxonomyBuilderSupplier.get();
				final SnomedTaxonomyBuilder copy = SnomedTaxonomyBuilder.newInstance(newBuilder);
				return copy;
			}
		});
		
		differenceSupplier = Suppliers.memoize(new Supplier<Pair<LongSet, LongSet>>() {
			@Override public Pair<LongSet, LongSet> get() {
				
				LOGGER.info("Calculating taxonomic differences...");
				final Pair<LongSet, LongSet> difference = newTaxonomyBuilderSupplier.get().difference(previousTaxonomyBuilderSupplier.get());
				LOGGER.info("Calculating taxonomic differences successfully finished.");
				
				return difference;
			}
		});
		
		conceptLabelProviderSupplier = Suppliers.memoize(new Supplier<ConceptLabelProvider>() {
			@Override public ConceptLabelProvider get() {
				return new ConceptLabelProvider(branchPath, Iterables.concat(dirtyRefSetMembers, newRefSetMembers), dirtyConcepts);
			}
		});
		
		refSetMembershipProviderSupplier = Suppliers.memoize(new Supplier<RefSetMembershipProvider>() {
			@Override public RefSetMembershipProvider get() {
				return getRefSetMembershipProviderForNewConcepts();
			}
		});
		
		allRefSetIdsSupplier = Suppliers.memoize(new Supplier<Collection<String>>() {
			@Override public Collection<String> get() {
				final LongSet $ = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getAllRefSetIds(branchPath);
				return Collections.unmodifiableSet(LongSets.toStringSet($));
			}
		});
		
	}
	
/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#process(com.b2international.snowowl.datastore.ICDOCommitChangeSet)
	 */
	@Override
	public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {

		this.commitChangeSet = commitChangeSet;
		try {
		
			Preconditions.checkNotNull(commitChangeSet, "CDO commit change set argument cannot be null.");
			this.commitChangeSet = commitChangeSet;
			
			// XXX: populate newConcepts from newComponents Iterable in advance, so related dirty concepts can check it
			Iterables.addAll(newConcepts, Iterables.filter(commitChangeSet.getNewComponents(), Concept.class));
			
			for (final CDOObject newObject : commitChangeSet.getNewComponents()) {
				processNew(newObject);
			}
			
			for (final CDOObject dirtyObject : commitChangeSet.getDirtyComponents()) {
				processDirty(dirtyObject);
			}
			
			for (final Entry<CDOID, EClass> detachedEntry : commitChangeSet.getDetachedComponents().entrySet()) {
				processDetached(detachedEntry);
			}
			
			postProcessAttributeConstraints();
			
			if (hasRelatedChanges) {
				updateDocuments();
			}
			
		} finally {

			//dispose all resources
			LifecycleUtils.deactivate(views);
			
		}
		
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#prepareCommit()
	 */
	@Override
	public void prepareCommit() throws SnowowlServiceException {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.CDOChangeProcessor#commit()
	 */
	@Override
	public void commit() throws SnowowlServiceException {
		LOGGER.info("Persisting changes...");
		indexUpdater.commit(branchPath);
		LOGGER.info("Changes have been successfully persisted.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#afterCommit()
	 */
	@Override
	public void afterCommit() {
		//does nothing
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#hadChangesToProcess()
	 */
	@Override
	public boolean hadChangesToProcess() {
		return hasRelatedChanges;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getChangeDescription()
	 */
	@Override
	public String getChangeDescription() {
		
		final StringBuilder sb = new StringBuilder("SNOMED CT Changes: ");
		if (!newConceptLogEntries.isEmpty()) {
			if (newConceptLogEntries.size() > 100) {
				sb.append("number of new concepts: ");
				sb.append(newConceptLogEntries.size());
			} else {
				sb.append("new concepts added: ");
				createLogEntry(sb, newConceptLogEntries);
			}
		}
		
		if (!deletedConceptLogEntries.isEmpty()) {
			sb.append("deleted concepts: ");
			createLogEntry(sb, deletedConceptLogEntries);
		}

		//reasoner can change lots of concepts. Do not log.
		if (!changedConceptLogEntries.isEmpty() && changedConceptLogEntries.size() <= 100) {
			sb.append("changed concepts: ");
			createLogEntry(sb, changedConceptLogEntries);
		} else if (changedConceptLogEntries.size() > 100) {
			sb.append("number of changed concepts: ");
			sb.append(changedConceptLogEntries.size());
		}
		
		if (!newRefsetLogEntries.isEmpty()) {
			sb.append("new reference sets: ");
			createLogEntry(sb, newRefsetLogEntries);
		}
		
		if (!deletedRefSetLogEntries.isEmpty()) {
			sb.append("deleted reference sets: ");
			createLogEntry(sb, deletedRefSetLogEntries);
		}
		
		if (!changedRefSetLogEntries.isEmpty()) {
			sb.append("changed reference sets: ");
			createLogEntry(sb, changedRefSetLogEntries);
		}
		
		if (!deletedConstraintLogEntries.isEmpty()) {
			sb.append("deleted constraint: ");
			createLogEntry(sb, deletedConstraintLogEntries);
		}
		
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getUserId()
	 */
	@Override
	public String getUserId() {
		return commitChangeSet.getUserId();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getBranchPath()
	 */
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.CDOChangeProcessor#rollback()
	 */
	@Override
	public void rollback() throws SnowowlServiceException {
		indexUpdater.rollback(branchPath);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.CDOChangeProcessor#getName()
	 */
	@Override
	public String getName() {
		return "SNOMED CT Terminology";
	}

	/*updates the documents in the indexes based on the dirty, detached and new components.*/
	private void updateDocuments() {
		
		//this could happen before anything.
		new Thread(new Runnable() {
			@Override public void run() {
				conceptLabelProviderSupplier.get();
			}
		}, "Concept label provider initializer").start();
		
		new Thread(new Runnable() {
			@Override public void run() {
				refSetMembershipProviderSupplier.get();
			}
		}, "Reference set membership provider initializer").start();
		
		LOGGER.info("Processing and updating changes...");

		prepareTaxonomyBuilders();
		
		new Thread(new Runnable() {
			@Override public void run() {
				differenceSupplier.get();
			}
		}, "Taxonomy difference processor").start();
		

		final Set<String> synonymAndDescendantIds = 
				ApplicationContext.getInstance().getService(ISnomedComponentService.class).getSynonymAndDescendantIds(branchPath);
		
		//represents the previous state of the ontology
		final SnomedPredicateBrowser predicateBrowser = ApplicationContext.getInstance().getService(SnomedPredicateBrowser.class);
		
		//map for storing concept IDs and associated new labels.
		//basically this map will contain PTs as values.
		//we will track only new language reference set member with preferred acceptability.
		final Map<String, String> conceptIdNewLabelMap = calculateComponentNewLabels();
		
		for (Iterator<SnomedRefSetMember> iterator = dirtyRefSetMembers.iterator(); iterator.hasNext();) {
			final SnomedRefSetMember member = iterator.next();
			if (deletedRefSetMembers.contains(member.cdoID())) {
				iterator.remove();
			}
		}
		
		for (final Entry<CDOID, EClass> detachedConcept : deletedConcepts) {

			indexUpdater.delete(branchPath, asLong(detachedConcept.getKey()));

			//save the deleted concepts for logging them later - they are still there until the updater commits
			final ComponentIdAndLabel componentIdAndLabel = new SnomedConceptLookupService().getComponentIdAndLabel(branchPath, CDOIDUtils.asLong(detachedConcept.getKey()));
			deletedConceptLogEntries.add(componentIdAndLabel);
			deletedConceptIds.add(parseLong(componentIdAndLabel.getId()));
		}
		
		
		for (final CDOID detachedMember : deletedRefSetMembers) {
			indexUpdater.delete(branchPath, asLong(detachedMember));
		}

		for (final Entry<CDOID, EClass> detachedRelationship : deletedRelationships) {
			indexUpdater.delete(branchPath, asLong(detachedRelationship.getKey()));
		}

		for (final Entry<CDOID, EClass> detachedRefSet : deletedRefSets) {
			final long cdoId = asLong(detachedRefSet.getKey());
			indexUpdater.delete(branchPath, cdoId);
			
			//save the deleted refsets for logging them later - they are still there until the updater commits
			final ComponentIdAndLabel componentIdAndLabel = new SnomedRefSetLookupService().getComponentIdAndLabel(branchPath, cdoId);
			deletedRefSetLogEntries.add(componentIdAndLabel);
		}
		
		for (final Entry<CDOID, EClass> detachedDescription : deletedDescriptions) {
			indexUpdater.delete(branchPath, asLong(detachedDescription.getKey()));
		}
		
		for (final AttributeConstraint detachedConstraint : deletedConstraints) {
			final ConceptModelPredicate predicate = getBottomMostPredicate(detachedConstraint.getPredicate());
			indexUpdater.delete(branchPath, new Term(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid()));

			deletedConstraintLogEntries.add(new ComponentIdAndLabel(
					String.valueOf(detachedConstraint.getUuid()), 
					Strings.nullToEmpty(detachedConstraint.getDescription())));
		}
		
		for (final CodeSystem newCodeSystem : newCodeSystems) {
			indexUpdater.index(branchPath, new CodeSystemIndexMappingStrategy(newCodeSystem));
		}
		
		for (final CodeSystemVersion newCodeSystemVersion : newCodeSystemVersions) {
			indexUpdater.index(branchPath, new CodeSystemVersionIndexMappingStrategy(newCodeSystemVersion));
		}
		
		for (final CodeSystemVersion dirtyCodeSystemVersion : dirtyCodeSystemVersions) {
			indexUpdater.index(branchPath, new CodeSystemVersionIndexMappingStrategy(dirtyCodeSystemVersion));
		}
		
		//new concepts
		ConcurrentCollectionUtils.forEach(newConcepts, new Procedure<Concept>() {

			@Override protected void doApply(final Concept newConcept) {
				//hierarchy changes cannot be updated for new concepts, as it is not persisted, yet
				final ISnomedTaxonomyBuilder taxonomyBuilder = getAndCheckNewTaxonomyBuilder();
				
				final boolean active = newConcept.isActive();
				final String conceptId = newConcept.getId();
				
				final LongSet parentConceptIds;
				final LongSet ancestorConceptIds;
				
				//can happen after the following use case:
				//create a new concept (time_1)
				//inactivate concept (time_2)
				//delete concept (time_3)
				//revert repository state to (time_2)
				if (!active) {
					
					parentConceptIds = LongCollections.emptySet();
					ancestorConceptIds = LongCollections.emptySet();
					
				} else {

					parentConceptIds = taxonomyBuilder.getAncestorNodeIds(conceptId);
					ancestorConceptIds = taxonomyBuilder.getAllIndirectAncestorNodeIds(newConcept.getId());
					
				}
				
				//this point we have to find the first parent concept that is in the previous state of the taxonomy to specify the icon ID
				final String iconId = !active ? Concepts.ROOT_CONCEPT : getImageId(newConcept.getId());
				
				final Collection<String> referringRefSetIds = refSetMembershipProviderSupplier.get().getContainerRefSetIds(conceptId, REFERRING_MEMBER_TYPES);
				final Collection<String> mappingRefSetIds = refSetMembershipProviderSupplier.get().getContainerRefSetIds(conceptId, MAPPING_MEMBER_TYPES);
				
				final String label = conceptIdNewLabelMap.get(conceptId);
				indexUpdater.addDocument(branchPath, new SnomedConceptModelMappingStrategy(
						newConcept,
						label,
						synonymAndDescendantIds, 
						parentConceptIds, 
						ancestorConceptIds, 
						SnomedConceptModelMappingStrategy.DEFAULT_DOI,
						Collections.<String>emptySet(), 
						iconId, 
						referringRefSetIds,
						mappingRefSetIds,
						true).createDocument());
				
				//save the changes for logging them later
				newConceptLogEntries.add(new ComponentIdAndLabel(label, conceptId));
				
			}
		});
		
		//new relationships
		ConcurrentCollectionUtils.forEach(newRelationships, new Procedure<Relationship>() {
			@Override protected void doApply(final Relationship newRelationship) {
				indexUpdater.addDocument(branchPath, new SnomedRelationshipIndexMappingStrategy(newRelationship).createDocument());
			}
		});
		
		//new refsets
		for (final SnomedRefSet newRefSet : newRefSets) {
			
			final long iconId = getIconId(newRefSet);
			
			final String identifierId = newRefSet.getIdentifierId();
			final Collection<String> predicateKeys = predicateBrowser.getRefSetPredicateKeys(branchPath, identifierId);
			
			indexUpdater.addDocument(branchPath, new SnomedRefSetIndexMappingStrategy(newRefSet, iconId, predicateKeys, true).createDocument());
			
			//for logging, save the new refset
			newRefsetLogEntries.add(new ComponentIdAndLabel(conceptIdNewLabelMap.get(identifierId), identifierId));
			
		}
		
		ConcurrentCollectionUtils.forEach(newDescriptions, new Procedure<Description>() {
			@Override protected void doApply(final Description newDescription) {
				indexUpdater.addDocument(branchPath, new SnomedDescriptionIndexMappingStrategy(newDescription).createDocument());
			}
		});
		
		for (final AttributeConstraint newConstraint : newConstraints) {
			
			final AbstractIndexMappingStrategy mappingStrategy = AbstractPredicateIndexMappingStrategy.createMappingStrategy(newConstraint);
			
			if (!AbstractPredicateIndexMappingStrategy.NULL_PREDICATE_INDEX_MAPPING_STARTEGY.equals(mappingStrategy)) {
				final Document document = mappingStrategy.createDocument();
				final String uuid = getBottomMostPredicate(newConstraint.getPredicate()).getUuid();
				indexUpdater.index(branchPath, document, new Term(SnomedIndexBrowserConstants.PREDICATE_UUID, uuid));
			}
			
		}
		
		for (final Relationship dirtyRelationship : dirtyRelationships) {
			indexUpdater.index(branchPath, new SnomedRelationshipIndexMappingStrategy(dirtyRelationship));
		}
		
		for (final Concept dirtyConcept : dirtyConcepts) {
			
			final ISnomedTaxonomyBuilder previousTaxonomyBuilder = getAndCheckPreviousTaxonomyBuilder();
			final String conceptId = dirtyConcept.getId();
			final long conceptIdL = parseLong(conceptId);
			
			//if concept is not active we should not access any kind of taxonomic information from taxonomy builder.
			final boolean active = dirtyConcept.isActive();

			final LongSet parentConceptIds;
			final LongSet ancestorConceptIds;
			
			if (!active) { //this is the easier way. concept is inactive no parentage information
				
				parentConceptIds = LongCollections.emptySet();
				ancestorConceptIds = LongCollections.emptySet();
				
			} else {
				
				//this is the trickier way as it might happen from 2.7 that user revert inactivation
				//that will end up a dirty active concept that did not exist in the previous taxonomy
				//this case use the new one
				if (!previousTaxonomyBuilder.containsNode(conceptId)) {
					
					final ISnomedTaxonomyBuilder newTaxonomyBuilder = getAndCheckNewTaxonomyBuilder();
					parentConceptIds = newTaxonomyBuilder.getAncestorNodeIds(conceptId);
					ancestorConceptIds = newTaxonomyBuilder.getAllIndirectAncestorNodeIds(conceptId);
					
				} else { //fall back the normal way
					
					parentConceptIds = previousTaxonomyBuilder.getAncestorNodeIds(conceptId);
					ancestorConceptIds = previousTaxonomyBuilder.getAllIndirectAncestorNodeIds(conceptId);
				}

				/*
				 * XXX: concepts can sometimes be both ancestors and direct parents. Don't remove 
				 * the intersection of the two sets from the ancestor set.
				 */
			}

			final Collection<String> predicateKeys = Sets.newHashSet(predicateBrowser.getPredicateKeys(branchPath, conceptId));
			
			//update predicate keys. merge with the changes contained by the current change set.
			//remove detached ones first
			if (detachedConceptPredicateKeys.size() > 0) {
				final Object detachedPredicateKeys = detachedConceptPredicateKeys.get(conceptIdL);
				
				//remove the detached ones (if any)
				if (detachedPredicateKeys instanceof Set) {
					
					for (final Object detachedPredicateKey : (Set<?>) detachedPredicateKeys) {
						
						predicateKeys.remove(String.valueOf(detachedPredicateKey));
						
					}
					
				}
			}

			//add new ones
			if (newConceptPredicateKeys.size() > 0) {
				final Object newPredicateKeys = newConceptPredicateKeys.get(conceptIdL);
				
				if (newPredicateKeys instanceof Set) {
					
					//add new ones (if any)
					for (final Object newPredicateKey : (Set<?>) newPredicateKeys) {
						
						predicateKeys.add(String.valueOf(newPredicateKey));
						
					}
					
				}
			}
			
			final float conceptDoi = getTerminologyBrowser().getConceptDoi(branchPath, conceptId);

			final String iconId;
			
			if (dirtyConcept.isActive()) {
				
				//again. as we consider concept re-activation we have to check whether the concept exists in the previous state or not
				if (previousTaxonomyBuilder.containsNode(conceptId)) {
					//default method for getting concept ID
					iconId = SnomedIconProvider.getInstance().getIconComponentId(conceptId, getAndCheckNewTaxonomyBuilder());
					
				} else {
					//act in case of new concepts
					iconId = getImageId(conceptId);
					
				}
				
			} else {
				iconId = Concepts.ROOT_CONCEPT;
			}
			
			final Collection<String> referencingRefSetIds = Sets.newHashSet(getRefSetBrowser().getContainerRefSetIds(branchPath, conceptId));
			final Collection<String> referencingMappingRefSetIds = Sets.newHashSet(getRefSetBrowser().getContainerMappingRefSetIds(branchPath, conceptId));
			
			String label = conceptIdNewLabelMap.get(conceptId);
			
			if (null == label) {
				
				label = conceptLabelProviderSupplier.get().getConceptLabel(conceptId);
				
			}

			//XXX aaaaa
			final String copyLabel = label;
			
			//workaround to cache concept that has a changes status.
			indexUpdater.updateConcept(branchPath, conceptIdL, new IDocumentUpdater() {
				@Override public IIndexMappingStrategy updateDocument(final Document document) {
					
					return new SnomedConceptModelMappingStrategy(
							dirtyConcept,
							copyLabel,
							synonymAndDescendantIds, 
							parentConceptIds, 
							ancestorConceptIds, 
							conceptDoi, 
							predicateKeys, 
							iconId, 
							referencingRefSetIds,
							referencingMappingRefSetIds,
							indexConceptAsRelevantForCompare(dirtyConcept));
				}

			});
			
			//log as a change on the concept
			final ComponentIdAndLabel componentIdAndLabel = new ComponentIdAndLabel(copyLabel, conceptId);
			changedConceptLogEntries.add(componentIdAndLabel);
		}
		
		for (final SnomedRefSet dirtyRefSet : dirtyRefSets) {
			final long iconId = getIconId(dirtyRefSet);
			final Collection<String> predicateKeys = Sets.newHashSet(predicateBrowser.getRefSetPredicateKeys(branchPath, dirtyRefSet.getIdentifierId()));

			//merge predicate keys with the detached and new ones
			
			final long identifierId = parseLong(dirtyRefSet.getIdentifierId());
			
			if (detachedRefSetPredicateKeys.size() > 0) {
				final Object detachedPredicateKeys = detachedRefSetPredicateKeys.get(identifierId);
				
				//remove the detached ones (if any)
				if (detachedPredicateKeys instanceof Set) {
					
					for (final Object detachedPredicateKey : (Set<?>) detachedPredicateKeys) {
						
						predicateKeys.remove(String.valueOf(detachedPredicateKey));
						
					}
					
				}
			}

			if (newRefSetPredicateKeys.size() > 0) {
				final Object newPredicateKeys = newRefSetPredicateKeys.get(identifierId);
				
				if (newPredicateKeys instanceof Set) {
					
					//add new ones (if any)
					for (final Object newPredicateKey : (Set<?>) newPredicateKeys) {
						
						predicateKeys.add(String.valueOf(newPredicateKey));
						
					}
					
				}
			}
			
			indexUpdater.index(branchPath, new SnomedRefSetIndexMappingStrategy(dirtyRefSet, iconId, predicateKeys, indexConceptAsRelevantForCompare(dirtyRefSet)));
			final ComponentIdAndLabel componentIdAndLabel = new SnomedRefSetLookupService().getComponentIdAndLabel(branchPath, asLong(dirtyRefSet.cdoID()));
			changedRefSetLogEntries.add(componentIdAndLabel);
		}
		
		for (final Description dirtyDescription : dirtyDescriptions) {
			indexUpdater.index(branchPath, new SnomedDescriptionIndexMappingStrategy(dirtyDescription));
		}
		
		for (@SuppressWarnings("unused") final AttributeConstraint dirtyConstraint : dirtyConstraints) {
			//TODO implement index mapping strategy for attribute constraints
		}

		//XXX postpone reference set member change processing as long as possible since we are waiting for labels...
		//new refset members
		
		//reuse language reference set members acceptability label if possible
		final ConceptLabelProvider conceptLabelProvider = conceptLabelProviderSupplier.get();
		final String acceptableLabel = conceptLabelProvider.getConceptLabel(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
		final String preferredLabel = conceptLabelProvider.getConceptLabel(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
		
		final Map<String, String> $ = Maps.newHashMap();
		if (!StringUtils.isEmpty(acceptableLabel)) {
			$.put(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE, acceptableLabel);
		}
	
		if (!StringUtils.isEmpty(preferredLabel)) {
			$.put(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED, preferredLabel);
		}
		
		final Map<String, String> idLabelCache = Collections.unmodifiableMap($);
		
		ConcurrentCollectionUtils.forEach(newRefSetMembers, new Procedure<SnomedRefSetMember>() {
			@Override protected void doApply(final SnomedRefSetMember newMember) {

				if (newMember instanceof SnomedConcreteDataTypeRefSetMember) {
					indexUpdater.addDocument(branchPath, new SnomedRefSetMemberIndexMappingStrategy(newMember, idLabelCache).createDocument());
					
				} else {
					
					String label = conceptIdNewLabelMap.get(newMember.getReferencedComponentId());
					
					if (null == label) {
						label = conceptLabelProviderSupplier.get().getConceptLabel(newMember.getReferencedComponentId());
					}
					
					indexUpdater.addDocument(branchPath, new SnomedRefSetMemberIndexMappingStrategy(newMember, label, idLabelCache).createDocument());
				}
			
			}
		});
		
		for (final SnomedRefSetMember dirtyMember : dirtyRefSetMembers) {
			
			if (dirtyMember instanceof SnomedConcreteDataTypeRefSetMember) {
				
				indexUpdater.index(branchPath, new SnomedRefSetMemberIndexMappingStrategy(dirtyMember, idLabelCache));
				
			} else {
				
				String label = conceptIdNewLabelMap.get(dirtyMember.getReferencedComponentId());
				
				if (null == label) {
					label = conceptLabelProviderSupplier.get().getConceptLabel(dirtyMember.getReferencedComponentId());
				}
				
				indexUpdater.index(branchPath, new SnomedRefSetMemberIndexMappingStrategy(dirtyMember, label, idLabelCache));
				
			}
		}
		
		if (!memberChanges.isEmpty()) {
			
			LOGGER.info("Updating reference set membership changes...");
			
			
			for (final LongKeyMapIterator itr = memberChanges.entries(); itr.hasNext(); /* nothing */) {
				
				itr.next();
				
				final long conceptId = itr.getKey();
				final Object value = itr.getValue();
				
				if (value instanceof Set) {
					
					@SuppressWarnings("unchecked")
					final Set<RefSetMemberChange> memberChanges = (Set<RefSetMemberChange>) value;

					//nothing to merge and update
					if (CompareUtils.isEmpty(memberChanges)) {
						continue;
					}

					if (!getAndCheckNewTaxonomyBuilder().containsNode(Long.toString(conceptId))) {
						continue; //concept has been deleted, no need to update.
					}
					
					indexUpdater.updateConcept(branchPath, conceptId, new IDocumentUpdater() {
						@Override public IIndexMappingStrategy updateDocument(final Document doc) {

							//get reference set membership fields
							final IndexableField[] referenceSetIdfields = doc.getFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID);
							
							//get the reference set IDs
							final Set<String> referencingRefSetIds = Sets.newHashSet(Iterables.transform(Arrays.asList(referenceSetIdfields), new Function<IndexableField, String>() {
								@Override public String apply(final IndexableField field) {
									return field.stringValue();
								}
							}));
							
							//get reference set mapping membership fields
							final IndexableField[] mappingReferenceSetIdfields = doc.getFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID);
							
							//get the mapping reference set IDs
							final Set<String> mappingReferencingRefSetIds = Sets.newHashSet(Iterables.transform(Arrays.asList(mappingReferenceSetIdfields), new Function<IndexableField, String>() {
								@Override public String apply(final IndexableField field) {
									return field.stringValue();
								}
							}));
							

							//remove all fields
							doc.removeFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID);
							//mapping fields as well
							doc.removeFields(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID);
							
							//merge reference set membership with the changes extracted from the transaction, if any.
							for (final RefSetMemberChange change : memberChanges) {
								
								switch (change.changeKind) {
									
									case ADDED:
										
										if (SnomedRefSetType.SIMPLE.equals(change.type) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(change.type)) {
											
											referencingRefSetIds.add(change.refSetId);
											
										} else if (SnomedRefSetType.SIMPLE_MAP.equals(change.type)) {
											
											mappingReferencingRefSetIds.add(change.refSetId);
											
										}
										
										break;
										
									case REMOVED:
										
										if (SnomedRefSetType.SIMPLE.equals(change.type) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(change.type)) {
											
											referencingRefSetIds.remove(change.refSetId);
											
										} else if (SnomedRefSetType.SIMPLE_MAP.equals(change.type)) {
											
											mappingReferencingRefSetIds.remove(change.refSetId);
											
										}
										
										break;
										
									default:
										
										throw new IllegalArgumentException("Unknown reference set member change kind: " + change.changeKind);
									
								}
								
							}
							
							//re-add reference set membership fields
							for (final String refSetId : referencingRefSetIds) {
								
								doc.add(new LongField(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID, parseLong(refSetId), Store.YES));
								
							}
							
							//re-add mapping reference set membership fields
							for (final String refSetId : mappingReferencingRefSetIds) {
								
								doc.add(new LongField(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, parseLong(refSetId), Store.YES));
								
							}
							
							
							return new SnomedConceptDocumentMappingStrategy(doc, indexConceptAsRelevantForCompare(conceptId));
						}
					});

					
				}
				
			}
			
		}
		
		LOGGER.info("Updating taxonomy...");
		handleDetachedRelationship(differenceSupplier.get().getB());
		handleNewRelationship(differenceSupplier.get().getA());
			
		LOGGER.info("Processing and updating changes successfully finished.");
	}

	/*calculates the new label for concepts, if any*/
	//we are ignoring detached language members 
	//first: according to SNOMED CT TIG we have to create a new member either the previous one is retired/detached
	//second: makes no sense to remove the PT from the concept 
	private Map<String, String> calculateComponentNewLabels() {
		
		final Iterable<SnomedLanguageRefSetMember> languageMembers = Iterables.filter(newRefSetMembers, SnomedLanguageRefSetMember.class);
		if (CompareUtils.isEmpty(languageMembers)) {
			return Collections.<String, String>emptyMap();
		}
		
		//initialize lazily
		final Supplier<Map<String, Description>> descriptionSupplier = Suppliers.memoize(new Supplier<Map<String, Description>>() {
			@Override public Map<String, Description> get() {
				//map all new and dirty descriptions by their ID
				return Maps.uniqueIndex(Iterables.concat(dirtyDescriptions, newDescriptions), new Function<Description, String>() {
					@Override public String apply(final Description description) {
						return Preconditions.checkNotNull(description, "Description argument cannot be null").getId();
					}
				});
			}
		});
		
		final Map<String, String> $ = Maps.newHashMap();
		
		for (final SnomedLanguageRefSetMember member : languageMembers) {
			
			final String descriptionId = member.getReferencedComponentId();
			Description description = descriptionSupplier.get().get(descriptionId);
			//could happen that description has not changed in transaction
			if (null == description) {
				description = new  SnomedDescriptionLookupService().getComponent(descriptionId, member.cdoView());
			}
			
			Preconditions.checkNotNull(description, "Cannot find description. ID: " + descriptionId);
			
			$.put(descriptionId, description.getTerm());

			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId())) {
				//we are ignoring FSNs, as they could replace real PT descriptions for concepts.
				if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) {
					continue;
				}
				
				final String conceptId = description.getConcept().getId();
				$.put(conceptId, description.getTerm());
			}
		}
		
		return $;
	}
	
	/*returns with the icon ID for the given SNOMED CT reference set*/
	private long getIconId(final SnomedRefSet refSet) {
		
		if (SnomedRefSetType.LANGUAGE.equals(refSet.getType())) {
			
			return parseLong(refSet.getIdentifierId());
			
		} else {
			
			return parseLong(getImageId(refSet.getIdentifierId()));
			
		}
		
	}

	private boolean indexConceptAsRelevantForCompare(final long conceptId) {
		return dirtyConceptIdsWithCompareUpdate.contains(conceptId);
	}
	
	private boolean indexConceptAsRelevantForCompare(final Concept concept) {
		return dirtyConceptIdsWithCompareUpdate.contains(parseLong(concept.getId()));
	}
	
	private boolean indexConceptAsRelevantForCompare(final SnomedRefSet refSet) {
		return dirtyRefSetIdsWithCompareUpdate.contains(parseLong(refSet.getIdentifierId()));
	}
	
	private RefSetMembershipProvider getRefSetMembershipProviderForNewConcepts() {
		
		final Collection<String> activeConceptIds = getActiveConceptIds();
		final Iterable<SnomedRefSetMember> relatedMembers = getMembersForNewRefSetMembership();
		
		return new RefSetMembershipProvider(activeConceptIds, relatedMembers);
		
	}

	/*returns with an iterable of SNOMED CT reference set members that are fulfill each of the following conditions:
	 * - reference set member either new or dirty in the currently processed commit change set data
	 * - reference set member's referenced component type is a SNOMED CT concept
	 * - reference set member is *NOT* retired
	 * - reference set is *NOT* a structural reference set (e.g.: concept inactivation indication)
	 * - container reference set belongs to one of the following reference set type:
	 *   -- simple type
	 *   -- attribute value type
	 *   -- simple map type
	 **/
	@SuppressWarnings("unchecked")
	private Iterable<SnomedRefSetMember> getMembersForNewRefSetMembership() {
		
		//new and dirty members
		Iterable<SnomedRefSetMember> members = Iterables.concat(newRefSetMembers, dirtyRefSetMembers);
		
		//referencing to a concept
		members = Iterables.filter(members, CONCEPT_MEMBER_PREDICATE);
		
		//active members
		members = Iterables.filter(members, ACTIVE_MEMBER_PREDICATE);
		
		//regular reference set members
		members = Iterables.filter(members, REGULAR_MEMBER_PREDICATE);
		
		return Iterables.filter(members, Predicates.or(SIMPLE_TYPE_PREDICATE, SIMPLE_MAP_TYPE_PREDICATE, ATTRIBUTE_VALUE_TYPE_PREDICATE));
	}

	/*returns with the IDs of all new active SNOMED&nbsp;CT concepts from the change set*/
	private Collection<String> getActiveConceptIds() {
		return Sets.newHashSet(Iterables.transform(Iterables.filter(newConcepts, ACTIVE_CONCEPT_PREDICATE), CONCEPT_ID_FUNCTION));
	}

	/*returns with the reference set browser service. note: this contains the previous state of the ontology.*/
	private SnomedRefSetBrowser getRefSetBrowser() {
		return ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class);
	}
	
	/**
	 * Prepares the taxonomy builder. One for representing the previous state of the ontology.
	 * One for the new state.   
	 */
	private void prepareTaxonomyBuilders() {
		LOGGER.info("Retrieving taxonomic information from store.");
		final ISnomedTaxonomyBuilder previousBuilder = getPreviousTaxonomyBuilder(); //this will trigger the 'new one' instantiation.
		final ISnomedTaxonomyBuilder newBuilder = getNewTaxonomyBuilder(); //this could happen on the same thread. cloning is 10-20 ms.

		final Runnable previousStateBuilderRunnable = new Runnable() {
			@Override public void run() {
				LOGGER.info("Building taxonomic information.");
				previousBuilder.build();
			}
		};
		
		//if not change processing is triggered without CDO update and notification.
		//e.g.: on task synchronization
		final AtomicReference<IStoreAccessor> accessorReference = new AtomicReference<IStoreAccessor>();
		if (canCopyThreadLocal) {
			
			final String uuid = getConnection().getUuid();
			accessorReference.set(CDOServerUtils.getAccessorByUuid(uuid));
			
		}
		
		final Runnable newStateBuilderRunnable = new Runnable() {
			@Override public void run() {
				
				try {
						
					//if not change processing is triggered without CDO update and notification.
					//e.g.: on task synchronization
					if (canCopyThreadLocal) {
						
						StoreThreadLocal.setAccessor(accessorReference.get());
						
					}
					
					LOGGER.info("Processing changes taxonomic information.");
					
					//here we have to consider changes triggered by repository state revert
					//this point the following might happen:
					//SNOMED CT concept and/or relationship will be contained by both deleted and new collections
					//with same business (SCT ID) but different primary ID (CDO ID) [this is the way how we handle object resurrection]
					//we decided, to order changes by primary keys. as primary IDs are provided in sequence, one could assume
					//that the larger primary ID happens later, and that is the truth
	
					//but as deletion always happens later than addition, we only have to take care of deletion
					//so if the deletion is about to erase something that has the same SCT ID but more recent (larger) 
					//primary key, we just ignore it when building the taxonomy.
					
					//SCT ID - relationships
					final Map<String, Relationship> _newRelationships = Maps.newHashMap(Maps.uniqueIndex(newRelationships, GET_SCT_ID_FUNCTION));

					//SCT ID - concepts
					final Map<String, Concept> _newConcepts = Maps.newHashMap(Maps.uniqueIndex(newConcepts, GET_SCT_ID_FUNCTION));
					
					for (final Relationship newRelationship : newRelationships) {
						newBuilder.addEdge(createEdge(newRelationship));
					}
					
					for (final Relationship dirtyRelationship : dirtyRelationships) {
						newBuilder.addEdge(createEdge(dirtyRelationship));
					}
					
					for (final Entry<CDOID, EClass> detachedRelationshipEntry : deletedRelationships) {
						
						final long cdoId = CDOIDUtils.asLong(detachedRelationshipEntry.getKey());
						final SnomedRelationshipIndexQueryAdapter queryAdapter = SnomedRelationshipIndexQueryAdapter.findByStorageKey(cdoId);
						final Iterable<SnomedRelationshipIndexEntry> results = getIndexService().search(branchPath, queryAdapter, 2);

						Preconditions.checkState(!CompareUtils.isEmpty(results), "No relationships were found with unique storage key: " + cdoId);
						Preconditions.checkState(Iterables.size(results) < 2, "More than one relationships were found with unique storage key: " + cdoId);

						final SnomedRelationshipIndexEntry relationship = Iterables.getOnlyElement(results);
						final String relationshipId = relationship.getId();
						
						//same relationship as new and detached
						if (_newRelationships.containsKey(relationshipId)) {
							
							final Relationship newRelationship = _newRelationships.get(relationshipId);
							final String typeId = newRelationship.getType().getId();
							
							//ignore everything but IS_As
							if (Concepts.IS_A.equals(typeId)) {
								
								//check source and destination as well
								if (relationship.getObjectId().equals(newRelationship.getSource().getId())
										&& relationship.getValueId().equals(newRelationship.getDestination().getId())) {
									
									//and if the new relationship has more recent (larger CDO ID), ignore deletion
									if (CDOIDUtils.asLong(newRelationship.cdoID()) > cdoId) {
										
										continue;
										
									}
									
								}
								
								
							}
							
						}
						
						newBuilder.removeEdge(createEdge(relationship));
						
					}
					
					for (final Concept newConcept : newConcepts) {
						newBuilder.addNode(createNode(newConcept));
					}
					
					for (final Entry<CDOID, EClass> detachedConceptEntry : deletedConcepts) {
						
						//consider the same as for relationship
						//we have to decide if deletion is the 'stronger' modification or not
						final long cdoId = CDOIDUtils.asLong(detachedConceptEntry.getKey());
						final SnomedConceptIndexQueryAdapter queryAdapter = SnomedConceptReducedQueryAdapter.findByStorageKey(cdoId);
						final Iterable<SnomedConceptIndexEntry> results = getIndexService().search(branchPath, queryAdapter, 2);

						Preconditions.checkState(!CompareUtils.isEmpty(results), "No concepts were found with unique storage key: " + cdoId);
						Preconditions.checkState(Iterables.size(results) < 2, "More than one concepts were found with unique storage key: " + cdoId);

						final SnomedConceptIndexEntry concept = Iterables.getOnlyElement(results);
						final String conceptId = concept.getId();
						
						//same concept as addition and deletion
						if (_newConcepts.containsKey(conceptId)) {
							
							final Concept newConcept = _newConcepts.get(conceptId);
							
							//check whether new concept has more recent (larger CDO ID) or not, ignore deletion
							if (CDOIDUtils.asLong(newConcept.cdoID()) > cdoId) {
								
								continue;
								
							}
							
						}
						
						//else delete it
						newBuilder.removeNode(createNode(concept));
					}
					
					for (final Concept dirtyConcept : dirtyConcepts) {

						if (!dirtyConcept.isActive()) { //we do not need this concept. either it was deactivated now or sometime earlier.
							
							//nothing can be dirty and new at the same time
							newBuilder.removeNode(createNode(getTerminologyBrowser().getConcept(branchPath, dirtyConcept.getId())));
							
						} else { //consider reverting inactivation
							
							if (!newBuilder.containsNode(dirtyConcept.getId())) {
								
								newBuilder.addNode(createNode(dirtyConcept));
								
							}
							
						}
						
					}
					
					LOGGER.info("Rebuilding taxonomic information based on the changes.");
					newBuilder.build();
				
				} finally {
					
					if (canCopyThreadLocal) {
					
						StoreThreadLocal.release();
						
					}
					
				}
				
			}
		};
		
		ForkJoinUtils.runInParallel(newStateBuilderRunnable, previousStateBuilderRunnable);
		
	}

	/*process the changes on the detached IS_A relationships given as a set of relationship IDs*/
	private void handleDetachedRelationship(final LongSet detachedIsARelationshipsIds) {
		
		final ISnomedTaxonomyBuilder newTaxonomyBuilder = getAndCheckNewTaxonomyBuilder();
		final ISnomedTaxonomyBuilder previousTaxonomyBuilder = getAndCheckPreviousTaxonomyBuilder();
		
		for (final LongIterator relationshipIdItr = detachedIsARelationshipsIds.iterator(); relationshipIdItr.hasNext(); /*nothing*/) {
			
			final long relatonsipId = relationshipIdItr.next();
			final long sourceConceptId = parseLong(previousTaxonomyBuilder.getSourceNodeId(Long.toString(relatonsipId)));
			final long destinationConceptId = parseLong(previousTaxonomyBuilder.getDestinationNodeId(Long.toString(relatonsipId)));

			final LongSet ancestorIds = previousTaxonomyBuilder.getSelfAndAllAncestorNodeIds(/*value ID*/destinationConceptId);
			

			//if the concept has been deleted we do not have to update its taxonomic informations, document will be deleted
			if (!deletedConceptIds.contains(sourceConceptId)) {
				removeTaxonomyField(sourceConceptId, destinationConceptId, CommonIndexConstants.COMPONENT_PARENT);
				final LongOpenHashSet copyAncestorIds = new LongOpenHashSet(ancestorIds);
				if (newTaxonomyBuilder.containsNode(Long.toString(sourceConceptId))) {
					copyAncestorIds.removeAll(newTaxonomyBuilder.getSelfAndAllAncestorNodeIds(sourceConceptId));
				}
				removeTaxonomyField(sourceConceptId, copyAncestorIds, SnomedIndexBrowserConstants.CONCEPT_ANCESTOR);
			}
			
			final LongSet sourceAllSubTypeIds = previousTaxonomyBuilder.getAllDescendantNodeIds(Long.toString(sourceConceptId));
			for (final LongIterator itr = sourceAllSubTypeIds.iterator(); itr.hasNext(); /* nothing */) {
				
				final LongOpenHashSet copyAncestorIds = new LongOpenHashSet(ancestorIds);
				final long conceptId = itr.next();
				
				if (deletedConceptIds.contains(conceptId)) {
					continue; //deleted concept no need to update taxonomy
				}
				
				if (newTaxonomyBuilder.containsNode(Long.toString(conceptId))) { //concept is a new one, it did not exist in the previous state
					copyAncestorIds.removeAll(newTaxonomyBuilder.getSelfAndAllAncestorNodeIds(conceptId));
				}
				
				removeTaxonomyField(conceptId, copyAncestorIds, SnomedIndexBrowserConstants.CONCEPT_ANCESTOR); //consider multiple ancestor fields with same parent
			}
			
			
		}
	}
	
	/*process the changes on the new IS_A relationships given as a set of relationship IDs*/
	private void handleNewRelationship(final LongSet newIsARelationshipsIds) {
		
		final ISnomedTaxonomyBuilder newTaxonomyBuilder = getAndCheckNewTaxonomyBuilder();
		final ISnomedTaxonomyBuilder previousTaxonomyBuilder = getAndCheckPreviousTaxonomyBuilder();
		
		for (final LongIterator relationshipIdItr = newIsARelationshipsIds.iterator(); relationshipIdItr.hasNext(); /*nothing*/) {
			
			final long relatonsipId = relationshipIdItr.next();
			final long sourceConceptId = parseLong(newTaxonomyBuilder.getSourceNodeId(Long.toString(relatonsipId)));
			final long destinationConceptId = parseLong(newTaxonomyBuilder.getDestinationNodeId(Long.toString(relatonsipId)));
			
//			final long objectStorageKey = newTaxonomyBuilder.getStorageKey(sourceConceptId);
			final LongSet ancestorIds = newTaxonomyBuilder.getSelfAndAllAncestorNodeIds(/*value ID*/destinationConceptId);
			addTaxonomyField(sourceConceptId, destinationConceptId, CommonIndexConstants.COMPONENT_PARENT);
			addTaxonomyField(sourceConceptId, newTaxonomyBuilder.getAllAncestorNodeIds(Long.toString(destinationConceptId)), SnomedIndexBrowserConstants.CONCEPT_ANCESTOR);
			
			final LongSet objectAllSubTypeIds = newTaxonomyBuilder.getAllDescendantNodeIds(Long.toString(sourceConceptId));
			for (final LongIterator itr = objectAllSubTypeIds.iterator(); itr.hasNext(); /* nothing */) {
				
				final LongOpenHashSet copyAncestorIds = new LongOpenHashSet(ancestorIds);
				final long conceptId = itr.next();
				if (previousTaxonomyBuilder.containsNode(Long.toString(conceptId))) {
					copyAncestorIds.removeAll(previousTaxonomyBuilder.getSelfAndAllAncestorNodeIds(conceptId));
				}
				
				addTaxonomyField(conceptId, copyAncestorIds, SnomedIndexBrowserConstants.CONCEPT_ANCESTOR); //consider multiple ancestor fields with same parent
			}
			
			
		}
	}

	/*processes the given attribute constraint changes, marks the related concepts and reference sets as dirty*/
	private void postProcessAttributeConstraints(final Set<AttributeConstraint> attributeConstraints, final Procedure<AttributeConstraint> function, 
			final LongKeyMap conceptPredicateKeys, final LongKeyMap refSetPredicateKeys) {
		
		Collections3.forEach(attributeConstraints, function);
		
		if (!CompareUtils.isEmpty(attributeConstraints)) {
			
			
			try {
			
				if (canCopyThreadLocal) {
					
					final String uuid = getConnection().getUuid();
					StoreThreadLocal.setAccessor(CDOServerUtils.getAccessorByUuid(uuid));
					
				}
				
				//either for new or detached constraints, we have to have a dirty concept model, get the view from there
				final CDOView view = Iterables.getLast(commitChangeSet.getDirtyComponents()).cdoView();
				
				for (final LongKeyMapIterator itr = conceptPredicateKeys.entries(); itr.hasNext(); /**/) {
					
					itr.next();
					
					final long conceptId = itr.getKey();
					final Object value = itr.getValue();
					if (value instanceof Iterable) {
						
						final Concept concept = new SnomedConceptLookupService().getComponent(Long.toString(conceptId), view);
						if (null != concept) { //concept is referenced by MRCM rule, but concept does not exist.
							dirtyConcepts.add(concept);
						}
						
					}
					
				}
				
				for (final LongKeyMapIterator itr = refSetPredicateKeys.entries(); itr.hasNext(); /**/) {
					
					itr.next();
					
					final long refSetId = itr.getKey();
					final Object value = itr.getValue();
					if (value instanceof Iterable) {
						
						final SnomedRefSet refSet = new SnomedRefSetLookupService().getComponent(Long.toString(refSetId), view);
						if (null != refSet) { //reference set is referenced by MRCM rule but concept does not even exist
							dirtyRefSets.add(refSet);
						}
						
					}
					
			}
			
			} finally {
				
				if (canCopyThreadLocal) {
					
					//XXX intentionally not released, we did not start a new thread like we do for taxonomy building
					
				}
				
			}
			
		}
		
	}
	
	/*marks the proper concepts and reference sets if the transaction contains attribute constraint changes*/
	private void postProcessAttributeConstraints() {

		final Procedure<AttributeConstraint> newConstraintProcessor = new Procedure<AttributeConstraint>() {
			@Override protected void doApply(final AttributeConstraint constraint) {
				processNewConstraint(constraint);
			}
		};
		
		postProcessAttributeConstraints(newConstraints, newConstraintProcessor, 
				newConceptPredicateKeys, newRefSetPredicateKeys);
		
		final Procedure<AttributeConstraint> detachedConstraintProcessor = new Procedure<AttributeConstraint>() {
			@Override protected void doApply(final AttributeConstraint constraint) {
				processDetachedConstraint(constraint);
			}
		};
		
		postProcessAttributeConstraints(deletedConstraints, detachedConstraintProcessor, 
				detachedConceptPredicateKeys, detachedRefSetPredicateKeys);
		
	}

	/*processes the attribute constraints, stores the concept and reference set changes.*/
	private void processConstraint(final AttributeConstraint attributeConstraint, final LongKeyMap conceptPredicateKeys, final LongKeyMap refSetPredicateKeys) {

		final ConceptSetDefinition domain = attributeConstraint.getDomain();
		final ConceptModelPredicate predicate = getBottomMostPredicate(attributeConstraint.getPredicate());
		
		final String uuid = predicate.getUuid();
		
		final Collection<ConstraintDomain> constraintDomains = PredicateUtils.processConstraintDomain(uuid, domain);
		for (final ConstraintDomain constraintDomain : constraintDomains) {
			
			final long componentId = constraintDomain.getComponentId();
			final String predicateKey = constraintDomain.getPredicateKey();
			final DefinitionType type = constraintDomain.getType();
			
			switch (type) {
				
				case CONCEPT:
					
					@SuppressWarnings("unchecked")
					Set<String> _conceptPredicateKeys = (Set<String>) conceptPredicateKeys.get(componentId);
					if (null == _conceptPredicateKeys) {
						
						_conceptPredicateKeys = Sets.newHashSet(predicateKey);
						conceptPredicateKeys.put(componentId, _conceptPredicateKeys);
						
					} else {

						_conceptPredicateKeys.add(predicateKey);
						
					}
					
					break;
					
				case REFSET:
					
					@SuppressWarnings("unchecked")
					Set<String> _refSetPredicateKeys = (Set<String>) refSetPredicateKeys.get(componentId);
					if (null == _refSetPredicateKeys) {
						
						_refSetPredicateKeys = Sets.newHashSet(predicateKey);
						refSetPredicateKeys.put(componentId, _refSetPredicateKeys);
						
					} else {
						
						_refSetPredicateKeys.add(predicateKey);
						
					}
					
					break;
					
				default: 
					throw new IllegalArgumentException("Unknown definition type: " + type);
				
			}
			
		}
		
	
		
	}
	
	/*processes the new attribute constraints*/
	private void processNewConstraint(final AttributeConstraint newConstraint) {
		processConstraint(newConstraint, newConceptPredicateKeys, newRefSetPredicateKeys);
	}
	
	/*processes the detached attribute constraints*/
	private void processDetachedConstraint(final AttributeConstraint detachedConstraint) {
		processConstraint(detachedConstraint, detachedConceptPredicateKeys, detachedRefSetPredicateKeys);
	}

	/*returns with the taxonomy builder instance representing the latest state of the current ontology*/
	private ISnomedTaxonomyBuilder getNewTaxonomyBuilder() {
		return newTaxonomyBuilderSupplier.get();
	}

	/*this method tries to get the first matching parent concept ID for the specified concept from the previous state of the taxonomy*/
	private String getImageId(final String conceptId) {
		
		//the concept ID has a matching file resource. nothing to do.
		if (SnomedIconProvider.getInstance().getAvailableIconIds().contains(conceptId)) {
			return conceptId;
		}
		
		final ISnomedTaxonomyBuilder newTaxonomyBuilder = getAndCheckNewTaxonomyBuilder();
		final ISnomedTaxonomyBuilder previousTaxonomyBuilder = getAndCheckPreviousTaxonomyBuilder();
		
		if (!newTaxonomyBuilder.containsNode(conceptId)) {
			return Concepts.ROOT_CONCEPT;
		}
		
		final LongSet paretntConceptIds = newTaxonomyBuilder.getAncestorNodeIds(conceptId);
		
		if (LongSets.isEmpty(paretntConceptIds)) {
			return Concepts.ROOT_CONCEPT;
		}
		
		//basically we should not care about multiple parentage
		//if the concept has multiple parent from multiple top level concepts, we just choose one, as we cannot decide which icon should be used
		
		final long parentConceptId = paretntConceptIds.iterator().next();
		
		if (previousTaxonomyBuilder.containsNode(Long.toString(parentConceptId))) {
			return SnomedIconProvider.getInstance().getIconId(parentConceptId, branchPath);
		} else {
			
			return getImageId(Long.toString(parentConceptId));
			
		}
		
	}
	
	/*returns with the previous taxonomy builder instance. also checks it's state.*/
	private ISnomedTaxonomyBuilder getAndCheckPreviousTaxonomyBuilder() {
		final ISnomedTaxonomyBuilder taxonomyBuilder = getPreviousTaxonomyBuilder();
		Preconditions.checkState(!taxonomyBuilder.isDirty(), "Builder for representing the previous state of the taxonomy has dirty state.");
		return taxonomyBuilder;
	}
	
	/*returns with the new taxonomy builder instance. also checks it's state.*/
	private ISnomedTaxonomyBuilder getAndCheckNewTaxonomyBuilder() {
		final ISnomedTaxonomyBuilder taxonomyBuilder = getNewTaxonomyBuilder();
		Preconditions.checkState(!taxonomyBuilder.isDirty(), "Builder for representing the new state of the taxonomy has dirty state.");
		return taxonomyBuilder;
	}
	
	/*returns with the taxonomy builder representing the state of the ontology before the commit that is currently being processed*/
	private ISnomedTaxonomyBuilder getPreviousTaxonomyBuilder() {
		return previousTaxonomyBuilderSupplier.get();
	}
	
	/*removes a fieldValue from a document field (fieldName) from a document identified by the concept ID)*/
	private void removeTaxonomyField(final long conceptId, final long fieldValue, final String fieldName) {

		indexUpdater.updateConcept(branchPath, conceptId, new IDocumentUpdater() {
			@Override
			public IIndexMappingStrategy updateDocument(final Document document) {
				for (final Iterator<IndexableField> fieldItr = document.iterator(); fieldItr.hasNext(); /* nothing */) {
					final IndexableField field = fieldItr.next();
					if (fieldName.equals(field.name()) && Long.toString(fieldValue).equals(field.stringValue())) {
						fieldItr.remove();
					}
				}

				return new SnomedConceptDocumentMappingStrategy(document, indexConceptAsRelevantForCompare(conceptId));
			}
		});
	}
	
	/*removes the fields storing taxonomy information for a document identified by the unique concept ID.
	 *fieldValues: values to remove
	 *fieldName: the field name to remove*/
	private void removeTaxonomyField(final long conceptId, final LongSet fieldValues, final String fieldName) {

		indexUpdater.updateConcept(branchPath, conceptId, new IDocumentUpdater() {
			@Override
			public IIndexMappingStrategy updateDocument(final Document document) {
				
				for (final LongIterator itr = fieldValues.iterator(); itr.hasNext(); /* nothing */) {
					
					final long fieldValue = itr.next();
					
					for (final Iterator<IndexableField> fieldItr = document.iterator(); fieldItr.hasNext(); /* nothing */) {
						final IndexableField field = fieldItr.next();
						if (fieldName.equals(field.name()) && Long.toString(fieldValue).equals(field.stringValue())) {
							fieldItr.remove();
						}
					}

				}
				return new SnomedConceptDocumentMappingStrategy(document, indexConceptAsRelevantForCompare(conceptId));
			}
		});
	}
	
	/**
	 * Adds a new field with a specified value to a document identified by the unique concept ID.
	 * @param conceptId the unique concept ID of a particular concept who's index document has to be updated.
	 * @param fieldValue the value to add as a new field to the document. (SCT ID as long)
	 * @param fieldName the name of the field on the document.
	 */
	private void addTaxonomyField(final long conceptId, final long fieldValue, final String fieldName) {
		
		indexUpdater.updateConcept(branchPath, conceptId, new IDocumentUpdater() {
			@Override
			public IIndexMappingStrategy updateDocument(final Document document) {
				document.add(new StoredField(fieldName, fieldValue));
				return new SnomedConceptDocumentMappingStrategy(document, indexConceptAsRelevantForCompare(conceptId));
			}
		});
	}
	
	/**
	 * Adds new fields with a specified values to a document identified by the unique concept ID.
	 * @param conceptId the unique concept ID of a particular concept who's index document has to be updated.
	 * @param fieldValues a set of values to add as a new field to the document. (SCT ID as long)
	 * @param fieldName the name of the field on the document.
	 */
	private void addTaxonomyField(final long conceptId, final LongSet fieldValues, final String fieldName) {
		
		indexUpdater.updateConcept(branchPath, conceptId, new IDocumentUpdater() {
			@Override
			public IIndexMappingStrategy updateDocument(final Document document) {
				for (final LongIterator itr = fieldValues.iterator(); itr.hasNext(); /* nothing */) {
					document.add(new StoredField(fieldName, itr.next()));
				}
				return new SnomedConceptDocumentMappingStrategy(document, indexConceptAsRelevantForCompare(conceptId));
			}
		});
	}
	
	/*returns with a set of reference set member changes for a concept given by its ID.*/
	@SuppressWarnings("unchecked")
	private Set<RefSetMemberChange> getMemberChanges(final long conceptId) {
		final Object object = memberChanges.get(conceptId);
		return null == object ? Sets.<RefSetMemberChange>newHashSet() : (Set<RefSetMemberChange>) object;
	}

	/*process the new object from the commit change set data*/
	private void processNew(final EObject newObject) {
		final SnomedRefSetMemberLookupService lookupService = new SnomedRefSetMemberLookupService();
		
		if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(newObject.eClass())) {
			newCodeSystems.add((CodeSystem) newObject);
			hasRelatedChanges = true;
		} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(newObject.eClass())) {
			newCodeSystemVersions.add((CodeSystemVersion) newObject);
			hasRelatedChanges = true;
		} else if (MrcmPackage.eINSTANCE.getAttributeConstraint().equals(newObject.eClass())) {
			newConstraints.add((AttributeConstraint) newObject);
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getConcept().equals(newObject.eClass())) {
			// XXX: We collected all new concepts before calling processNew(newObject)
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getRelationship().equals(newObject.eClass())) {
			newRelationships.add((Relationship) newObject);
			hasRelatedChanges = true;
		} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(newObject.eClass())) {
			newRefSets.add((SnomedRefSet) newObject);
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getDescription().equals(newObject.eClass())) {
			newDescriptions.add((Description) newObject);
			hasRelatedChanges = true;
		} else if (MrcmPackage.eINSTANCE.getAttributeConstraint().equals(newObject.eClass())) {
			newConstraints.add((AttributeConstraint) newObject);
			hasRelatedChanges = true;
		} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(newObject.eClass())) {
			final SnomedRefSetMember newRefSetMember = (SnomedRefSetMember) newObject;
			newRefSetMembers.add(newRefSetMember);
			hasRelatedChanges = true;
			dirtyRefSetIdsWithCompareUpdate.add(parseLong(newRefSetMember.getRefSetIdentifierId()));
			if (SnomedRefSetPackage.eINSTANCE.getSnomedLanguageRefSetMember().isSuperTypeOf(newObject.eClass())) {
				
				final SnomedLanguageRefSetMember member = (SnomedLanguageRefSetMember) newObject;
				
				if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId())) {
					if (member.eContainer() instanceof Description) {
						final Description description = (Description) member.eContainer();
						if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) {
							final Concept relatedConcept = description.getConcept();
							
							//if the concept is new we do not have to mark it as dirty. PT processing is done when processing new concept
							if (!newConcepts.contains(relatedConcept)) {
								dirtyConcepts.add(relatedConcept);
								dirtyConceptIdsWithCompareUpdate.add(parseLong(relatedConcept.getId()));
								
								final String conceptId = relatedConcept.getId();
								if (allRefSetIdsSupplier.get().contains(conceptId)) {
									dirtyRefSets.add(new SnomedRefSetLookupService().getComponent(conceptId, relatedConcept.cdoView()));
								}
								final Collection<SnomedRefSetMemberIndexEntry> referringMembers = ApplicationContext.getServiceForClass(SnomedIndexService.class).search(getBranchPath(), SnomedRefSetMembershipIndexQueryAdapter.createFindReferencingMembers(conceptId));
								for (SnomedRefSetMemberIndexEntry entry : referringMembers) {
									final SnomedRefSetMember entryMember = lookupService.getComponent(entry.getId(), relatedConcept.cdoView());
									if (entryMember != null) {
										dirtyRefSetMembers.add(entryMember);
									}
								}
								
							}
						}
					}
				}
				
			} else if (isInfluenceConceptDocument(newObject.eClass()) && isReferencingConcept((SnomedRefSetMember) newObject)) { 
				
				final SnomedRefSetMember member = (SnomedRefSetMember) newObject;
				
				if (member.isActive()) { //if new member is inactive we do not care
					
					final long conceptId = parseLong(member.getReferencedComponentId());
					
					final RefSetMemberChange change = new RefSetMemberChange(member.getRefSetIdentifierId(), MemberChangeKind.ADDED, member.getRefSet().getType());
					
					if (memberChanges.containsKey(conceptId)) {
						
						getMemberChanges(conceptId).add(change);
						
					} else {
						
						final Set<RefSetMemberChange> changes = Sets.newTreeSet();
						changes.add(change);
						memberChanges.put(conceptId, changes);
						
					}
					
				}
				
				
			}
			
		}
	}

	/*creates a taxonomy edge instance based on the given SNOMED CT relationship*/
	private TaxonomyEdge createEdge(final Relationship relationship) {
		return new TaxonomyEdge() {
			@Override public boolean isCurrent() {
				return relationship.isActive();
			}
			@Override public String getId() {
				return relationship.getId();
			}
			@Override public boolean isValid() {
				return Concepts.IS_A.equals(relationship.getType().getId());
			}
			@Override public String getSoureId() {
				return relationship.getSource().getId();
			}
			@Override public String getDestinationId() {
				return relationship.getDestination().getId();
			}
		};
	}

	/*creates a taxonomy edge instance based on the given SNOMED CT relationship*/
	private TaxonomyEdge createEdge(final SnomedRelationshipIndexEntry relationship) {
		return new TaxonomyEdge() {
			@Override public boolean isCurrent() {
				return relationship.isActive();
			}
			@Override public String getId() {
				return relationship.getId();
			}
			@Override public boolean isValid() {
				return Concepts.IS_A.equals(relationship.getAttributeId());
			}
			@Override public String getSoureId() {
				return relationship.getObjectId();
			}
			@Override public String getDestinationId() {
				return relationship.getValueId();
			}
		};
	}
	
	/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
	private TaxonomyNode createNode(final Concept concept) {
		return new TaxonomyNode() {
			@Override public boolean isCurrent() {
				return concept.isActive();
			}
			@Override public String getId() {
				return concept.getId();
			}
		};
	}

	/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
	private TaxonomyNode createNode(final SnomedConceptIndexEntry concept) {
		return new TaxonomyNode() {
			@Override public boolean isCurrent() {
				return concept.isActive();
			}
			@Override public String getId() {
				return concept.getId();
			}
		};
	}
	
	/*process the detached component from the commit change set data*/
	private void processDetached(final Entry<CDOID, EClass> detachedObjectType) {
		if (MrcmPackage.eINSTANCE.getAttributeConstraint().equals(detachedObjectType.getValue())) {
			
			//XXX will not be called due to https://github.com/b2ihealthcare/snowowl/issues/702
			//workaround is available at process dirty ConceptModel case
			//this point we have to load the object with the latest non-detached state
			
			final ICDOConnection connection = getConnection();
			final CDOBranch branch = connection.getBranch(branchPath);
			final CDOBranchPoint head = branch.getHead();
			final CDOID id = detachedObjectType.getKey();
			final List<CDORevision> detachedConstraints = CDOServerUtils.getObjectRevisions(head, id, 1);
			Preconditions.checkState(!CompareUtils.isEmpty(detachedConstraints), "Cannot load attribute constraint revision. Branch point: " + head + " CDO ID: " + id + ".");
			
			final CDORevision revision = Iterables.getOnlyElement(detachedConstraints);
			final CDOView view = connection.createView(revision.getBranch(), revision.getTimeStamp());
			views.add(view); //we will clean up in the very end of document update
			
			final CDOObject object = CDOUtils.getObjectIfExists(view, id);
			Preconditions.checkNotNull(object, "Cannot load attribute constraint. Revision: " + revision +  " view: " + view);

			deletedConstraints.add((AttributeConstraint) object);
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getConcept().equals(detachedObjectType.getValue())) {
			deletedConcepts.add(detachedObjectType);
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getRelationship().equals(detachedObjectType.getValue())) {
			deletedRelationships.add(detachedObjectType);
			hasRelatedChanges = true;
		} else if(SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(detachedObjectType.getValue())) {
			deletedRefSets.add(detachedObjectType);
			hasRelatedChanges = true;
		} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(detachedObjectType.getValue())) {
			deletedRefSetMembers.add(detachedObjectType.getKey());
			hasRelatedChanges = true;

			final Document doc = getDocumentForDetachedMember(detachedObjectType.getKey());
			dirtyRefSetIdsWithCompareUpdate.add(IndexUtils.getLongValue(doc.getField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID)));
			
			if (isInfluenceConceptDocument(detachedObjectType.getValue())) {
				
				final String referencedComponentId = Preconditions.checkNotNull(doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID), 
						"Cannot get referenced component ID for document: " + doc + " Member CDO ID: " + detachedObjectType.getKey());
				
				if (isConceptId(referencedComponentId)) {
					
					final String refSetId = Preconditions.checkNotNull(doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID), 
							"Cannot get reference set ID for document: " + doc + " Member CDO ID: " + detachedObjectType.getKey());
					
					final int typeOrdinal = IndexUtils.getIntValue(doc.getField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE));
					
					final long conceptId = parseLong(referencedComponentId);
					
					final RefSetMemberChange change = new RefSetMemberChange(refSetId, MemberChangeKind.REMOVED, getType(typeOrdinal));
					
					if (memberChanges.containsKey(conceptId)) {
						
						getMemberChanges(conceptId).add(change);
						
					} else {
						
						final Set<RefSetMemberChange> changes = Sets.newTreeSet();
						changes.add(change);
						memberChanges.put(conceptId, changes);
						
					}
					
				}
				
			}
			
		} else if(SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(detachedObjectType.getValue())) {
			deletedRefSets.add(detachedObjectType);
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getDescription().equals(detachedObjectType.getValue())) {
			deletedDescriptions.add(detachedObjectType);
			hasRelatedChanges = true;
		}
	}

	/*process the dirty object from the commit change set data*/
	private void processDirty(final CDOObject component) {
		
		if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(component.eClass())) {
			checkAndSetCodeSystemLastUpdateTime(component);
			hasRelatedChanges = true;
		} else if (MrcmPackage.eINSTANCE.getAttributeConstraint().equals(component.eClass())) {
			dirtyConstraints.add((AttributeConstraint) component);
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getConcept().equals(component.eClass())) {
			final Concept concept = (Concept) component;
			dirtyConcepts.add(concept);
			if (!hasOnlyInboundRelationshipChanges(concept)) {
				dirtyConceptIdsWithCompareUpdate.add(parseLong(concept.getId()));
			}
			
			final String conceptId = concept.getId();
			if (allRefSetIdsSupplier.get().contains(conceptId)) {
				dirtyRefSets.add(new SnomedRefSetLookupService().getComponent(conceptId, concept.cdoView()));
				dirtyRefSetIdsWithCompareUpdate.add(parseLong(conceptId));
			}
			
			hasRelatedChanges = true;
		} else if (SnomedPackage.eINSTANCE.getRelationship().equals(component.eClass())) {
			dirtyRelationships.add((Relationship) component);
			hasRelatedChanges = true;
		} else if(SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(component.eClass())) {
			dirtyRefSets.add((SnomedRefSet) component);
			dirtyRefSetIdsWithCompareUpdate.add(parseLong(((SnomedRefSet) component).getIdentifierId()));
			hasRelatedChanges = true;
		} else if (MrcmPackage.eINSTANCE.getAttributeConstraint().isSuperTypeOf(component.eClass())) {
			dirtyConstraints.add((AttributeConstraint) component);
			hasRelatedChanges = true;
		} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(component.eClass())) {
			final SnomedRefSetMember dirtyRefSetMember = (SnomedRefSetMember) component;
			final SnomedRefSet refSet = dirtyRefSetMember.getRefSet();
			if (!isStructural(refSet)) {
				dirtyRefSetIdsWithCompareUpdate.add(parseLong(dirtyRefSetMember.getRefSetIdentifierId()));
				dirtyRefSets.add(refSet);
			}
			dirtyRefSetMembers.add(dirtyRefSetMember);
			hasRelatedChanges = true;
			
			if (isInfluenceConceptDocument(dirtyRefSetMember.eClass()) && isReferencingConcept(dirtyRefSetMember)) { 
				
				final Concept referencedConcept = getReferencedConcept(dirtyRefSetMember);
				dirtyConcepts.add(referencedConcept);
				
				final SnomedRefSetMember member = (SnomedRefSetMember) component;
				
				final long conceptId = parseLong(member.getReferencedComponentId());
				
				final RefSetMemberChange change = new RefSetMemberChange(
						member.getRefSetIdentifierId(), 
						member.isActive() 
							? MemberChangeKind.ADDED 
							: MemberChangeKind.REMOVED,
						member.getRefSet().getType());
				
				if (memberChanges.containsKey(conceptId)) {
					
					getMemberChanges(conceptId).add(change);
					
				} else {
					
					final Set<RefSetMemberChange> changes = Sets.newTreeSet();
					changes.add(change);
					memberChanges.put(conceptId, changes);
					
				}
				
				
			}
			
		} else if (SnomedPackage.eINSTANCE.getDescription().equals(component.eClass())) {
			dirtyDescriptions.add((Description) component);
			hasRelatedChanges = true;
		} else if (MrcmPackage.eINSTANCE.getConceptModel().equals(component.eClass())) {
			//XXX workaround for https://github.com/b2ihealthcare/snowowl/issues/702
			
			final ConceptModel currentConceptModel = (ConceptModel) component;
			final ConceptModel previousConceptModel = loadPreviousState(currentConceptModel);
			
			final Function<ConstraintBase, String> extractUuidFunction = new Function<ConstraintBase, String>() {
				@Override public String apply(final ConstraintBase constraint) {
					return constraint.getUuid();
				}
			};
			
			final Map<String, ConstraintBase> previousConstraints = 
					Maps.uniqueIndex(previousConceptModel.getConstraints(), extractUuidFunction);
			
			final Map<String, ConstraintBase> currentConstraints = 
					Maps.uniqueIndex(currentConceptModel.getConstraints(), extractUuidFunction);
			
			for (final Entry<String, ConstraintBase> previousEntry : previousConstraints.entrySet()) {
				
				//deleted between the previous and the current state
				if (!currentConstraints.containsKey(previousEntry.getKey())) {
					
					if (previousEntry.getValue() instanceof AttributeConstraint) {
						
						deletedConstraints.add((AttributeConstraint) previousEntry.getValue());
						hasRelatedChanges = true;
						
					}
					
				}
				
			}
			
		} else if (MrcmPackage.eINSTANCE.getConceptSetDefinition().isSuperTypeOf(component.eClass())) {
			
			final AttributeConstraint constraint = getContainerConstraint((ConceptSetDefinition) component);
			if (null != constraint) {
				
				newConstraints.add(constraint);
				
				//treat the previous revision as the deleted one
				
				deletedConstraints.add(loadPreviousState(constraint));
				hasRelatedChanges = true;
				
			}
			
		} else if (MrcmPackage.eINSTANCE.getConceptModelPredicate().isSuperTypeOf(component.eClass())) {
			
			final AttributeConstraint constraint = getContainerConstraint((ConceptModelPredicate) component);
			if (null != constraint) {
				
				newConstraints.add(constraint);
				
				//treat previous revision as the deleted one
				deletedConstraints.add(loadPreviousState(constraint));
				hasRelatedChanges = true;
				
			}
			
		}
		
	}

	@SuppressWarnings("restriction")
	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component) {
		final CodeSystemVersion codeSystemVersion = (CodeSystemVersion) component;
		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID()).getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta).setValue(new Date(commitChangeSet.getTimestamp()));
			((InternalCDORevision) component.cdoRevision()).set(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate(), CDOStore.NO_INDEX, new Date(commitChangeSet.getTimestamp()));
			dirtyCodeSystemVersions.add(codeSystemVersion);
		}		
	}

	private boolean hasOnlyInboundRelationshipChanges(final Concept concept) {
		final CDORevisionDelta revisionDelta = commitChangeSet.getRevisionDeltas().get(concept.cdoID());
		if (null == revisionDelta) {
			return false;
		}
		final CDOFeatureDelta featureDelta = revisionDelta.getFeatureDelta(SnomedPackage.eINSTANCE.getConcept_InboundRelationships());
		if (null == featureDelta) {
			return false;
		}
		return 1 == revisionDelta.getFeatureDeltas().size();
	}
	
	private boolean isStructural(final SnomedRefSet refSet) {
		return refSet instanceof SnomedStructuralRefSet;
	}

	/*returns true if the specified SNOMED CT component ID is a concept ID. otherwise it returns with false.*/
	private boolean isConceptId(final String componentId) {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER 
				== SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId);
	}
	
	/*returns with the index document of a detached reference set member identified by its unique storage key.*/
	private Document getDocumentForDetachedMember(final CDOID id) {
		
		final Query query = new TermQuery(new Term(CommonIndexConstants.COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(CDOIDUtils.asLong(id))));
		
		final TopDocs topDocs = indexUpdater.search(branchPath, query, 1);
		
		Preconditions.checkNotNull(topDocs, "Cannot find detached reference set member with its unique storage key: " + id);
		Preconditions.checkState(!CompareUtils.isEmpty(topDocs.scoreDocs), "Cannot find detached reference set member with its unique storage key: " + id);
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		final Document doc = indexUpdater.document(branchPath, scoreDoc.doc, MEMBER_FIELD_TO_LOAD);
		
		return Preconditions.checkNotNull(doc, "Cannot find detached reference set member with its unique storage key: " + id);
		
	}
	
	/*returns with the referenced SNOMED CT concept for specified reference set member. referenced component must be a concept.*/
	private Concept getReferencedConcept(final SnomedRefSetMember member) {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Referenced component must be a SNOMED CT concept for reference set member: ");
		sb.append(member);
		sb.append(" UUID: ");
		sb.append(member.getUuid());
		sb.append(" Was: ");
		sb.append(member.getReferencedComponentType());
		
		Preconditions.checkState(SnomedTerminologyComponentConstants.CONCEPT_NUMBER == member.getReferencedComponentType(), 
				sb.toString()); 
		
		return new SnomedConceptLookupService().getComponent(member.getReferencedComponentId(), member.cdoView());
	}
	
	/*returns true if the referenced component of the reference set member is a concept. otherwise returns false.*/
	private boolean isReferencingConcept(final SnomedRefSetMember member) {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == member.getReferencedComponentType();
	}
	
	/*returns with the reference set type based on the type ordinal*/
	private SnomedRefSetType getType(final int typeOrdinal) {
		return SnomedRefSetType.get(typeOrdinal);
	}
	
	/*returns true if the class represents one of the following reference set members:
	 * - simple type
	 * - attribute value
	 * - simple map 
	 * reference set member*/
	private boolean isInfluenceConceptDocument(final EClass eClass) {
		return SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().equals(eClass) 
				|| SnomedRefSetPackage.eINSTANCE.getSnomedAttributeValueRefSetMember().equals(eClass)
				|| SnomedRefSetPackage.eINSTANCE.getSnomedSimpleMapRefSetMember().equals(eClass);
	}
	
	/*returns with the terminology browser service. always represents the previous state of the SNOMED CT ontology*/
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*returns with index service for SNOMED CT ontology*/
	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

	private void createLogEntry(final StringBuilder sb, final Set<ComponentIdAndLabel> logEntries) {
		for (final ComponentIdAndLabel logEntry : logEntries) {
			sb.append("[");
			sb.append(logEntry.getId());
			sb.append(":");
			sb.append(logEntry.getLabel());
			sb.append("], ");
		}
	}

	/*loads the previous state of the object and returns with it. this method caches the view for the loaded object.
	 *throws runtime exception if the object does not have previous revision.*/
	@SuppressWarnings("unchecked")
	private <T extends CDOObject> T loadPreviousState(final T object) {
		
		CDOUtils.check(object);
		Preconditions.checkState(!FSMUtil.isTransient(object), "Object was transient. " + object);
		Preconditions.checkState(!FSMUtil.isNew(object), "Object was new. " + object);
		
		final List<CDORevision> revisions = CDOServerUtils.getObjectRevisions(object.cdoRevision(), object.cdoID(), 1);
		
		Preconditions.checkState(!CompareUtils.isEmpty(revisions), "Cannot load previous revision of object. " + object);
		
		final CDORevision previousRevision = Iterables.getOnlyElement(revisions);
		final ICDOConnection connection = getConnection();
		final CDOView view = connection.createView(previousRevision.getBranch(), previousRevision.getTimeStamp());
		views.add(view); //cache view for previous revision. we will clean up in the end of index processing
		
		final CDOObject $ = CDOUtils.getObjectIfExists(view, previousRevision.getID());
		Preconditions.checkNotNull($, "Cannot load concept model. ID: " + previousRevision.getID() + " view:" + view);
		return (T) $;
		
	}
	
	/**returns with the CDO connection for SNOMED CT*/
	private ICDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}

	/**
	 * Provides information of any reference set membership for a particular SNOMED&nbsp;CT concept.
	 */
	private static final class RefSetMembershipProvider {
		
		private final Map<SnomedRefSetType, Collection<String>> typeToRefSetIds;
		private final Map<String, Multiset<String>> identifierIdToConceptIds;

		private RefSetMembershipProvider(final Collection<String> newConcepts, final Iterable<SnomedRefSetMember> members) {
			
			typeToRefSetIds = Maps.newEnumMap(SnomedRefSetType.class);
			identifierIdToConceptIds = Maps.newHashMap();
			
			for (final Iterator<SnomedRefSetMember> itr = members.iterator(); itr.hasNext(); /* */) {
				
				final SnomedRefSetMember member = itr.next();
				final String conceptId = member.getReferencedComponentId();
				
				if (newConcepts.contains(conceptId)) {
					
					final SnomedRefSet refSet = member.getRefSet();
					final SnomedRefSetType type = refSet.getType();
					final String identifierId = refSet.getIdentifierId();
					
					Collection<String> identifierIds = typeToRefSetIds.get(type);
					if (null == identifierIds) {
						
						identifierIds = Sets.newHashSet();
						identifierIds.add(identifierId);
						typeToRefSetIds.put(type, identifierIds);
						
					} else {
						
						identifierIds.add(identifierId);
						
					}
					
					
					Multiset<String> conceptIds = identifierIdToConceptIds.get(identifierId);
					if (null == conceptIds) {
						
						conceptIds = HashMultiset.create();
						conceptIds.add(conceptId);
						identifierIdToConceptIds.put(identifierId, conceptIds);
						
					} else {
						
						conceptIds.add(conceptId);
						
					}
					
				}
				
			}
			
		}
		
		/*returns with the container reference set IDs. returning collection may contain duplicates. consider map type reference sets.*/
		private Collection<String> getContainerRefSetIds(final String conceptId, final Iterable<SnomedRefSetType> types) {
			
			final Multiset<String> containerIds = HashMultiset.create();
			
			for (final SnomedRefSetType type : types) {
				
				final Collection<String> identifierIds = typeToRefSetIds.get(type);
				
				if (null != identifierIds) {
				
					for (final String identifierId : identifierIds) {
						
						final Multiset<String> conceptIds = identifierIdToConceptIds.get(identifierId);
						
						if (null != conceptIds) {
							
							final int count = conceptIds.count(conceptId);
							
							if (count > 0) {
								
								containerIds.add(identifierId, count);
								
							}
							
						}
						
					}
					
				}
				
			}
			
			return containerIds;
			
		}
		
		
	} 
	
	/**
	 * Class for representing a reference set member change.
	 * Specifies what is the change kind in which reference set.
	 */
	private static final class RefSetMemberChange implements Comparable<RefSetMemberChange> {
		
		private final String refSetId;
		private final MemberChangeKind changeKind;
		private final SnomedRefSetType type;
		
		private RefSetMemberChange(final String refSetId, final MemberChangeKind changeKind, final SnomedRefSetType type) {
			this.type = Preconditions.checkNotNull(type, "SNOMED CT reference set type argument cannot be null.");
			this.refSetId = Preconditions.checkNotNull(refSetId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
			this.changeKind = Preconditions.checkNotNull(changeKind, "Reference set member change king argument cannot be null.");
			
			Preconditions.checkState(
					SnomedRefSetType.SIMPLE.equals(type)
					|| SnomedRefSetType.ATTRIBUTE_VALUE.equals(type)
					|| SnomedRefSetType.SIMPLE_MAP.equals(type), "Unsupported reference set type: " + type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((changeKind == null) ? 0 : changeKind.hashCode());
			result = prime * result + ((refSetId == null) ? 0 : refSetId.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof RefSetMemberChange))
				return false;
			final RefSetMemberChange other = (RefSetMemberChange) obj;
			if (changeKind != other.changeKind)
				return false;
			if (refSetId == null) {
				if (other.refSetId != null)
					return false;
			} else if (!refSetId.equals(other.refSetId))
				return false;
			if (type != other.type)
				return false;
			return true;
		}



		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final RefSetMemberChange o) {
			return changeKind.compareTo(o.changeKind);
		}
		
	}
	
	/**
	 * Enumeration indicating whether a reference set member has been added or detached from its container
	 * reference set. 
	 *
	 */
	private static enum MemberChangeKind {
		ADDED,
		REMOVED;
	}
	
	/**
	 * Concept label provider.
	 */
	private final class ConceptLabelProvider implements IConceptLabelProviderStrategy {
		
		/**
		 * Threshold which modifies the current {@link ConceptLabelProvider concept label provider} behavior.
		 * <br>Threshold: {@value}. 
		 */
		private static final int THRESHOLD = 1000; 
		
		/**
		 * Predicate for returning {@code true} only and if only the processed SNOMED&nbsp;CT reference set
		 * member is *NOT* concrete data type and the referenced component is a SNOMED&nbsp;CT concept.
		 */
		private final Predicate<SnomedRefSetMember> PREDICATE = new Predicate<SnomedRefSetMember>() {
			@Override public boolean apply(final SnomedRefSetMember member) {
				
				Preconditions.checkNotNull(member, "SNOMED CT reference set member argument cannot be null.");
				
				
				if (member instanceof SnomedConcreteDataTypeRefSetMember) {
					
					return false;
					
				}
				
				return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == member.getReferencedComponentType();
			}
		};
		
		private final IConceptLabelProviderStrategy strategy;
		
		/* (non-Javadoc)
		 * @see com.b2international.snowowl.datastore.server.snomed.index.SnomedCDOChangeProcessor.ILabelProviderStrategy#getConceptLabel(java.lang.String)
		 */
		@Override
		public String getConceptLabel(final String conceptId) {
			return strategy.getConceptLabel(conceptId);
		}

		private ConceptLabelProvider(final IBranchPath branchPath, final Iterable<SnomedRefSetMember> members, final Iterable<Concept> concepts) {
			
			Preconditions.checkNotNull(members, "SNOMED CT reference set members argument cannot be null.");
			Preconditions.checkNotNull(concepts, "SNOMED CT concepts argument cannot be null.");
			strategy = initStartegy(Iterables.filter(members, PREDICATE), concepts);
			
		}

		/*initialize the strategy based on the given reference set members and the concepts*/
		private IConceptLabelProviderStrategy initStartegy(final Iterable<SnomedRefSetMember> members, final Iterable<Concept> concepts) {
			
			final int size = Iterables.size(members) + Iterables.size(concepts);
			if (size < THRESHOLD) {
				
				//initialize a strategy that simply delegates to SNOMED CT component service for a label
				return createDelegateStrategy();
				
			} else {
				
				final LongSet conceptIds = new LongOpenHashSet(size);
				
				for (final SnomedRefSetMember member : members) {
					conceptIds.add(parseLong(member.getReferencedComponentId()));
				}
				
				for (final Concept concept : concepts) {
					conceptIds.add(parseLong(concept.getId()));
				}
				
				final SnomedComponentLabelCollector collector = new SnomedComponentLabelCollector(conceptIds);
				
				//get labels
				indexUpdater.search(branchPath, SnomedIndexQueries.CONCEPT_TYPE_QUERY, collector);
				
				return new IConceptLabelProviderStrategy() {
					
					private final Supplier<Map<String, String>> idToLabelMap = Suppliers.memoize(new Supplier<Map<String, String>>() {
						@Override public Map<String, String> get() {
							
							final LongKeyMap idLabels = collector.getIdLabelMapping();
							final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idLabels.size());
							
							for (final LongKeyMapIterator itr = idLabels.entries(); itr.hasNext(); /**/) {
								
								itr.next();
								
								$.put(
										Long.toString(itr.getKey()), //ID
										String.valueOf(itr.getValue())); //label
								
							}
							
							return $;
						}
					});
					
					
					
					private final IConceptLabelProviderStrategy fallBackStrategy = createDelegateStrategy();
					
					@Override public String getConceptLabel(final String conceptId) {
						final String label = idToLabelMap.get().get(conceptId);
						return null == label ? fallBackStrategy.getConceptLabel(conceptId) : label;
					}
				};
				
				
			}
			
		}

		/*creates anew returns with a simple delegate strategy that uses the SNOMED CT component service for label lookup*/
		private IConceptLabelProviderStrategy createDelegateStrategy() {
			return new IConceptLabelProviderStrategy() {
				@Override public String getConceptLabel(final String conceptId) {
					return getService().getLabels(branchPath, conceptId)[0]; 
				}
				private ISnomedComponentService getService() { return ApplicationContext.getInstance().getService(ISnomedComponentService.class); }
			};
		}
		
	}
	
	/**Interface for providing concept labels against concept IDs.*/
	private interface IConceptLabelProviderStrategy {
		/**Returns with the concept preferred term.*/
		@Nullable String getConceptLabel(final String conceptId);
	}
	
	
}