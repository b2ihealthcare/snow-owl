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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentIdAndLabel;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.ChangeSetProcessor;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.AbstractIndexUpdater;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.DocumentCompositeUpdater;
import com.b2international.snowowl.datastore.index.DocumentUpdater;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.mapping.LongIndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.snomed.index.change.ComponentLabelChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.ConceptChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.ConceptReferringMemberChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.ConstraintChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.DescriptionAcceptabilityChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.DescriptionChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.IconChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.RefSetMapTargetUpdateChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.RefSetMemberChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.RelationshipChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.change.TaxonomyChangeProcessor;
import com.b2international.snowowl.datastore.server.snomed.index.collector.ComponentIdCollector;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder.TaxonomyBuilderEdge;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder.TaxonomyBuilderNode;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilderRunnable;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyProvider;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemIndexMappingStrategy;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import bak.pcj.LongCollection;
import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;
import bak.pcj.set.LongSet;

/**
 * Change processor implementation for SNOMED&nbsp;CT ontology. This class is responsible for updating indexes based on the new, dirty and detached components 
 * after a successful backend commit.
 * @see ICDOChangeProcessor
 * @see ICDOCommitChangeSet
 */
public class SnomedCDOChangeProcessor implements ICDOChangeProcessor {

	private static final Set<String> MEMBER_FIELD_TO_LOAD = SnomedMappings.fieldsToLoad()
			.active()
			.memberReferencedComponentId()
			.memberRefSetId()
			.memberRefSetType()
			.memberAcceptabilityId()
			.memberUuid()
			.build();

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedCDOChangeProcessor.class);
	
	private static Function<Component, String> GET_SCT_ID_FUNCTION = new Function<Component, String>() {
		@Override public String apply(final Component component) {
			return Preconditions.checkNotNull(component, "Component argument cannot be null.").getId();
		}
	};

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
	
	private final SnomedIndexServerService index;
	private final IBranchPath branchPath;
	private final Supplier<ISnomedTaxonomyBuilder> inferredNewTaxonomyBuilderSupplier;
	private final Supplier<ISnomedTaxonomyBuilder> inferredPreviousTaxonomyBuilderSupplier;
	
	private final Supplier<ISnomedTaxonomyBuilder> statedNewTaxonomyBuilderSupplier;
	private final Supplier<ISnomedTaxonomyBuilder> statedPreviousTaxonomyBuilderSupplier;
	
	/**Supplies the different between the previous and current state of the ontology.
	 *<br>Assumes initialized taxonomy builders.*/
	private final Supplier<Pair<LongSet,LongSet>> inferredDifferenceSupplier;
	private final Supplier<Pair<LongSet,LongSet>> statedDifferenceSupplier;
	
	/**
	 * Flag indicating whether the {@link StoreThreadLocal#getSession()} can be copied or not. 
	 */
	private final boolean canCopyThreadLocal;
	
	/**Represents the change set.*/
	private ICDOCommitChangeSet commitChangeSet;

	private ExecutorService executor;
	
	/**
	 * Creates a new change processor for the SNOMED&nbsp;CT ontology. 
	 * @param indexUpdater the index updater for the SNOMED&nbsp;CT ontology.
	 * @param branchPath the branch path where the changes has to be calculated and processed.
	 * @param canCopyThreadLocal 
	 */
	public SnomedCDOChangeProcessor(final ExecutorService executor, final IIndexUpdater<SnomedIndexEntry> indexUpdater, final IBranchPath branchPath, final boolean canCopyThreadLocal) {
		
		Preconditions.checkNotNull(indexUpdater, "Index service argument cannot be null.");
		Preconditions.checkArgument(indexUpdater instanceof AbstractIndexUpdater, "Index updater must be instance of " + AbstractIndexUpdater.class + ". Was " + indexUpdater.getClass());

		this.index = (SnomedIndexServerService) indexUpdater; 
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.canCopyThreadLocal = canCopyThreadLocal;
		this.executor = executor;
		
		inferredNewTaxonomyBuilderSupplier = Suppliers.memoize(new Supplier<ISnomedTaxonomyBuilder>() {
			@Override public ISnomedTaxonomyBuilder get() {
				return new SnomedTaxonomyBuilder(branchPath, StatementCollectionMode.INFERRED_ISA_ONLY);
			}
		});
		
		inferredPreviousTaxonomyBuilderSupplier = Suppliers.memoize(new Supplier<ISnomedTaxonomyBuilder>() {
			@Override public ISnomedTaxonomyBuilder get() {
				final SnomedTaxonomyBuilder newBuilder = (SnomedTaxonomyBuilder) inferredNewTaxonomyBuilderSupplier.get();
				final SnomedTaxonomyBuilder copy = SnomedTaxonomyBuilder.newInstance(newBuilder);
				return copy;
			}
		});
		
		statedNewTaxonomyBuilderSupplier = Suppliers.memoize(new Supplier<ISnomedTaxonomyBuilder>() {
			@Override public ISnomedTaxonomyBuilder get() {
				return new SnomedTaxonomyBuilder(branchPath, StatementCollectionMode.STATED_ISA_ONLY);
			}
		});
		
		statedPreviousTaxonomyBuilderSupplier = Suppliers.memoize(new Supplier<ISnomedTaxonomyBuilder>() {
			@Override public ISnomedTaxonomyBuilder get() {
				final SnomedTaxonomyBuilder newBuilder = (SnomedTaxonomyBuilder) statedNewTaxonomyBuilderSupplier.get();
				final SnomedTaxonomyBuilder copy = SnomedTaxonomyBuilder.newInstance(newBuilder);
				return copy;
			}
		});
		
		inferredDifferenceSupplier = Suppliers.memoize(new Supplier<Pair<LongSet, LongSet>>() {
			@Override public Pair<LongSet, LongSet> get() {
				LOGGER.info("Calculating taxonomic differences...");
				final Pair<LongSet, LongSet> difference = inferredNewTaxonomyBuilderSupplier.get().difference(inferredPreviousTaxonomyBuilderSupplier.get());
				LOGGER.info("Calculating taxonomic differences successfully.");
				return difference;
			}
		});
		
		statedDifferenceSupplier = Suppliers.memoize(new Supplier<Pair<LongSet, LongSet>>() {
			@Override public Pair<LongSet, LongSet> get() {
				LOGGER.info("Calculating stated taxonomic differences...");
				final Pair<LongSet, LongSet> difference = statedNewTaxonomyBuilderSupplier.get().difference(statedPreviousTaxonomyBuilderSupplier.get());
				LOGGER.info("Calculating stated taxonomic differences successfully.");
				return difference;
			}
		});
		
	}
	
	@Override
	public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		this.commitChangeSet = checkNotNull(commitChangeSet, "CDO commit change set argument cannot be null.");
		for (final CDOObject newObject : commitChangeSet.getNewComponents()) {
			if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(newObject.eClass())) {
				newCodeSystems.add((CodeSystem) newObject);
			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(newObject.eClass())) {
				newCodeSystemVersions.add((CodeSystemVersion) newObject);
			}
		}
		
		for (final CDOObject dirtyObject : commitChangeSet.getDirtyComponents()) {
			if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(dirtyObject.eClass())) {
				checkAndSetCodeSystemLastUpdateTime(dirtyObject);
			}
		}
		
		updateDocuments();
	}

	@Override
	public void prepareCommit() throws SnowowlServiceException {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	@Override
	public void commit() throws SnowowlServiceException {
		LOGGER.info("Persisting changes...");
		index.commit(branchPath);
		LOGGER.info("Changes have been successfully persisted.");
	}
	
	@Override
	public void afterCommit() {
		//does nothing
	}
	
	@Override
	public boolean hadChangesToProcess() {
		return !commitChangeSet.isEmpty();
	}
	
	@Override
	public String getChangeDescription() {
		
		final StringBuilder sb = new StringBuilder("SNOMED CT Changes:");
		if (!newConceptLogEntries.isEmpty()) {
			if (newConceptLogEntries.size() > 100) {
				sb.append(" number of new concepts: ");
				sb.append(newConceptLogEntries.size());
			} else {
				sb.append(" new concepts added: ");
				createLogEntry(sb, newConceptLogEntries);
			}
		}
		
		if (!deletedConceptLogEntries.isEmpty()) {
			sb.append(" deleted concepts: ");
			createLogEntry(sb, deletedConceptLogEntries);
		}

		//reasoner can change lots of concepts. Do not log.
		if (!changedConceptLogEntries.isEmpty() && changedConceptLogEntries.size() <= 100) {
			sb.append(" changed concepts: ");
			createLogEntry(sb, changedConceptLogEntries);
		} else if (changedConceptLogEntries.size() > 100) {
			sb.append(" number of changed concepts: ");
			sb.append(changedConceptLogEntries.size());
		}
		
		if (!newRefsetLogEntries.isEmpty()) {
			sb.append(" new reference sets: ");
			createLogEntry(sb, newRefsetLogEntries);
		}
		
		if (!deletedRefSetLogEntries.isEmpty()) {
			sb.append(" deleted reference sets: ");
			createLogEntry(sb, deletedRefSetLogEntries);
		}
		
		if (!changedRefSetLogEntries.isEmpty()) {
			sb.append(" changed reference sets: ");
			createLogEntry(sb, changedRefSetLogEntries);
		}
		
		if (!deletedConstraintLogEntries.isEmpty()) {
			sb.append(" deleted constraint: ");
			createLogEntry(sb, deletedConstraintLogEntries);
		}
		
		return sb.toString();
	}

	@Override
	public String getUserId() {
		return commitChangeSet.getUserId();
	}
	
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	@Override
	public void rollback() throws SnowowlServiceException {
		index.rollback(branchPath);
	}

	@Override
	public String getName() {
		return "SNOMED CT Terminology";
	}

	/*updates the documents in the indexes based on the dirty, detached and new components.*/
	private void updateDocuments() {
		
		LOGGER.info("Processing and updating changes...");

		prepareTaxonomyBuilders();
		
		new Thread(new Runnable() {
			@Override public void run() {
				inferredDifferenceSupplier.get();
			}
		}, "Inferred taxonomy difference processor").start();
		
		new Thread(new Runnable() {
			@Override public void run() {
				statedDifferenceSupplier.get();
			}
		}, "Stated taxonomy difference processor").start();
		

		// TODO refactor code system update into updaters
		for (final CodeSystem newCodeSystem : newCodeSystems) {
			index.index(branchPath, new CodeSystemIndexMappingStrategy(newCodeSystem));
		}
		
		for (final CodeSystemVersion newCodeSystemVersion : newCodeSystemVersions) {
			index.index(branchPath, new CodeSystemVersionIndexMappingStrategy(newCodeSystemVersion));
		}
		
		for (final CodeSystemVersion dirtyCodeSystemVersion : dirtyCodeSystemVersions) {
			index.index(branchPath, new CodeSystemVersionIndexMappingStrategy(dirtyCodeSystemVersion));
		}

		// execute component/use case  base change processors
		final LongCollection allIndexedConceptIds = getTerminologyBrowser().getAllConceptIds(branchPath);
		final LongSet allConceptIds = LongSets.newLongSet(allIndexedConceptIds);
		for (Concept newConcept : FluentIterable.from(commitChangeSet.getNewComponents()).filter(Concept.class)) {
			allConceptIds.add(Long.parseLong(newConcept.getId()));
		}
		
		for (Entry<CDOID, EClass> entry : commitChangeSet.getDetachedComponents().entrySet()) {
			if (entry.getValue() == SnomedPackage.Literals.CONCEPT) {
				final ExtendedComponent componentIdAndLabel = getTerminologyBrowser().getExtendedComponent(branchPath, CDOIDUtil.getLong(entry.getKey()));
				allConceptIds.remove(Long.parseLong(componentIdAndLabel.getId()));
			}
		}
		
		final ComponentLabelChangeProcessor labelChangeProcessor = new ComponentLabelChangeProcessor(branchPath, index);
		final Function<CDOID, Document> documentProvider = new Function<CDOID, Document>() {
			@Override public Document apply(CDOID input) {
				return getDocumentForDetachedMember(input);
			}
		};
		final List<ChangeSetProcessor<SnomedDocumentBuilder>> changeSetProcessors = ImmutableList.<ChangeSetProcessor<SnomedDocumentBuilder>>builder()
				.add(new ConceptChangeProcessor())
				.add(new ConceptReferringMemberChangeProcessor(documentProvider))
				.add(new RelationshipChangeProcessor())
				.add(new DescriptionChangeProcessor())
				.add(new DescriptionAcceptabilityChangeProcessor(documentProvider))
				.add(new TaxonomyChangeProcessor(getAndCheckInferredNewTaxonomyBuilder(), getInferredPreviousTaxonomyBuilder(), inferredDifferenceSupplier,  ""))
				.add(new TaxonomyChangeProcessor(getAndCheckStatedNewTaxonomyBuilder(), getStatedPreviousTaxonomyBuilder(), statedDifferenceSupplier, Concepts.STATED_RELATIONSHIP))
				.add(new IconChangeProcessor(branchPath,
						new TaxonomyProvider(getAndCheckInferredNewTaxonomyBuilder(), getInferredPreviousTaxonomyBuilder(), inferredDifferenceSupplier.get()),
						new TaxonomyProvider(getAndCheckStatedNewTaxonomyBuilder(), getStatedPreviousTaxonomyBuilder(), statedDifferenceSupplier.get())))
				.add(labelChangeProcessor)
				.add(new RefSetMemberChangeProcessor())
				.add(new RefSetMapTargetUpdateChangeProcessor(branchPath, index))
				.add(new ConstraintChangeProcessor(branchPath, allConceptIds))
				.build();
		
		for (ChangeSetProcessor<SnomedDocumentBuilder> processor : changeSetProcessors) {
			LOGGER.info("Collecting {}...", processor.description());
			processor.process(commitChangeSet);
		}
		LOGGER.info("Updating indexes...");

		final List<BytesRef> deletedStorageKeys = newArrayList();
		
		for (ChangeSetProcessor<SnomedDocumentBuilder> processor : changeSetProcessors) {
			for (Long storageKey : processor.getDeletedStorageKeys()) {
				LOGGER.trace("Deleting document {}", storageKey);
				index.delete(branchPath, storageKey);
				deletedStorageKeys.add(LongIndexField._toBytesRef(storageKey));
			}
		}
		
		final LongList deletedComponentIds;
		final Collection<String> deletedMemberIds;
		if (!deletedStorageKeys.isEmpty()) {
			final Filter filter = Mappings.storageKey().createBytesRefFilter(deletedStorageKeys);
			// query component IDs
			final Query deletedComponentIdQuery = new FilteredQuery(new PrefixQuery(new Term(SnomedMappings.id().fieldName())), filter);
			final ComponentIdCollector collector = new ComponentIdCollector(deletedStorageKeys.size());
			index.search(branchPath, deletedComponentIdQuery, collector);
			deletedComponentIds = collector.getIds();
			deletedMemberIds = index.executeReadTransaction(branchPath, new IndexRead<Collection<String>>() {
				@Override
				public Collection<String> execute(IndexSearcher index) throws IOException {
					final Query deletedMemberIdQuery = new FilteredQuery(SnomedMappings.memberUuid().toExistsQuery(), filter);
					final DocIdCollector collector = DocIdCollector.create(index.getIndexReader().maxDoc());
					index.search(deletedMemberIdQuery, filter, collector);
					final DocIdsIterator it = collector.getDocIDs().iterator();
					final Collection<String> memberIds = newHashSet();
					while (it.next()) {
						final int docId = it.getDocID();
						final Document doc = index.doc(docId, SnomedMappings.fieldsToLoad().memberUuid().build());
						memberIds.add(SnomedMappings.memberUuid().getValue(doc));
					}
					return memberIds;
				}
			});
		} else {
			deletedComponentIds	= new LongArrayList();
			deletedMemberIds = Collections.emptySet();
		}
		
		final Multimap<String, DocumentUpdater<SnomedDocumentBuilder>> updates = LinkedHashMultimap.create();
		for (ChangeSetProcessor<SnomedDocumentBuilder> processor : changeSetProcessors) {
			updates.putAll(processor.getUpdates());
		}
		
		final Collection<Future<?>> promises = newHashSetWithExpectedSize(updates.keySet().size());
		final IStoreAccessor accessor = StoreThreadLocal.getAccessor();
		for (String componentId : updates.keySet()) {
			try {
				if (deletedComponentIds.contains(Long.parseLong(componentId))) {
					// skip deleted components
					continue;
				}
			} catch (NumberFormatException e) {
				// ignore, multiple ID formats are expected, so parsing a long may not work all the time
			}
			if (deletedMemberIds.contains(componentId)) {
				continue;
			}
			
			final Collection<DocumentUpdater<SnomedDocumentBuilder>> updaters = updates.get(componentId);
			final DocumentCompositeUpdater<SnomedDocumentBuilder> updater = new DocumentCompositeUpdater<>(updaters);

			final SnomedQueryBuilder query = SnomedMappings.newQuery();
			try {
				final Long componentIdLong = Long.valueOf(componentId);
				// components are indexes with their long SNOMED CT ID
				// predicates are indexed with their storageKey
				query.id(componentIdLong).storageKey(componentIdLong);
			} catch (NumberFormatException e) {
				// members are indexes with their UUID
				query.memberUuid(componentId);
			}
			final Future<?> promise = executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						StoreThreadLocal.setAccessor(accessor);
						index.upsert(branchPath, query.matchAny(), updater, new SnomedDocumentBuilder.Factory());						
					} catch (Exception e) {
						LOGGER.error("Failed to upsert a document", e);
						throw new SnowowlRuntimeException(e);
					} finally {
						StoreThreadLocal.setAccessor(null);
					}
				}
			});
			promises.add(promise);
		}
		

		// wait for all index updates
		Throwable ex = null; 
		for (Future<?> promise : promises) {
			try {
				if (ex != null) {
					promise.cancel(false);
				} else {
					promise.get();
				}
			} catch (Exception e) {
				ex = e.getCause();
			}
		}
		if (ex != null) {
			throw SnowowlRuntimeException.wrap(ex);
		}
		
		LOGGER.info("Processing and updating index changes successfully finished.");
	}

	/**
	 * Prepares the taxonomy builder. One for representing the previous state of the ontology.
	 * One for the new state.   
	 */
	private void prepareTaxonomyBuilders() {
		LOGGER.info("Retrieving taxonomic information from store.");
		final ISnomedTaxonomyBuilder inferredPreviousBuilder = getInferredPreviousTaxonomyBuilder(); //this will trigger the 'new one' instantiation.
		final ISnomedTaxonomyBuilder inferredNewBuilder = getInferredNewTaxonomyBuilder(); //this could happen on the same thread. cloning is 10-20 ms.
		
		final ISnomedTaxonomyBuilder statedPreviousBuilder = getStatedPreviousTaxonomyBuilder();
		final ISnomedTaxonomyBuilder statedNewBuilder = getStatedNewTaxonomyBuilder();
		
		LOGGER.info("Building taxonomic information.");
		final Runnable previousInferredBuilderRunnable = new SnomedTaxonomyBuilderRunnable(inferredPreviousBuilder);
		final Runnable previousStatedBuilderRunnable = new SnomedTaxonomyBuilderRunnable(statedPreviousBuilder);
		final Runnable newStatedUpdateRunnable = new SnomedTaxonomyUpdateRunnable(statedNewBuilder, Concepts.STATED_RELATIONSHIP);
		final Runnable newInferredUpdateRunnable = new SnomedTaxonomyUpdateRunnable(inferredNewBuilder, Concepts.INFERRED_RELATIONSHIP);
		
		ForkJoinUtils.runInParallel(newInferredUpdateRunnable, previousInferredBuilderRunnable, newStatedUpdateRunnable, previousStatedBuilderRunnable);
	}
	
	private class SnomedTaxonomyUpdateRunnable implements Runnable {
		
		private ISnomedTaxonomyBuilder taxonomyBuilder;
		private String characteristicTypeId;

		public SnomedTaxonomyUpdateRunnable(ISnomedTaxonomyBuilder taxonomyBuilder, final String characteristicTypeId) {
			this.taxonomyBuilder = taxonomyBuilder;
			this.characteristicTypeId = characteristicTypeId;
		}
		
		@Override 
		public void run() {
			//if not change processing is triggered without CDO update and notification.
			//e.g.: on task synchronization
			if (canCopyThreadLocal) {
				StoreThreadLocal.setAccessor(CDOServerUtils.getAccessorByUuid(getConnection().getUuid()));
			}
			try {
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
				
				final Iterable<Concept> newConcepts = FluentIterable.from(commitChangeSet.getNewComponents()).filter(Concept.class);
				final Iterable<Concept> dirtyConcepts = FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(Concept.class);
				final Iterable<CDOID> deletedConcepts = ChangeSetProcessorBase.getDetachedComponents(commitChangeSet, SnomedPackage.Literals.CONCEPT);
				final Iterable<Relationship> newRelationships = FluentIterable.from(commitChangeSet.getNewComponents()).filter(Relationship.class);
				final Iterable<Relationship> dirtyRelationships = FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(Relationship.class);
				final Iterable<CDOID> deletedRelationships = ChangeSetProcessorBase.getDetachedComponents(commitChangeSet, SnomedPackage.Literals.RELATIONSHIP);
				
				//SCT ID - relationships
				final Map<String, Relationship> _newRelationships = Maps.newHashMap(Maps.uniqueIndex(newRelationships, GET_SCT_ID_FUNCTION));
				
				//SCT ID - concepts
				final Map<String, Concept> _newConcepts = Maps.newHashMap(Maps.uniqueIndex(newConcepts, GET_SCT_ID_FUNCTION));
				
				for (final Relationship newRelationship : newRelationships) {
					taxonomyBuilder.addEdge(createEdge(newRelationship));
				}
				
				for (final Relationship dirtyRelationship : dirtyRelationships) {
					taxonomyBuilder.addEdge(createEdge(dirtyRelationship));
				}
				
				for (final CDOID relationshipCdoId : deletedRelationships) {
					final long cdoId = CDOIDUtils.asLong(relationshipCdoId);
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
					taxonomyBuilder.removeEdge(createEdge(relationship));
				}
				for (final Concept newConcept : newConcepts) {
					taxonomyBuilder.addNode(createNode(newConcept));
				}
				for (final CDOID conceptCdoId : deletedConcepts) {
					
					//consider the same as for relationship
					//we have to decide if deletion is the 'stronger' modification or not
					final long cdoId = CDOIDUtils.asLong(conceptCdoId);
					final ExtendedComponent concept = getTerminologyBrowser().getExtendedComponent(branchPath, cdoId);
					checkState(concept != null, "No concepts were found with unique storage key: " + cdoId);
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
					taxonomyBuilder.removeNode(createDeletedNode(concept));
				}
				for (final Concept dirtyConcept : dirtyConcepts) {
					
					final CDORevisionDelta revisionDelta = commitChangeSet.getRevisionDeltas().get(dirtyConcept.cdoID());
					if (revisionDelta == null) {
						continue;
					}
					final CDOFeatureDelta changeStatusDelta = revisionDelta.getFeatureDelta(SnomedPackage.Literals.COMPONENT__ACTIVE);
					if (changeStatusDelta instanceof CDOSetFeatureDelta) {
						CDOSetFeatureDelta delta = (CDOSetFeatureDelta) changeStatusDelta;
						final Boolean oldValue = (Boolean) delta.getOldValue();
						final Boolean newValue = (Boolean) delta.getValue();
						if (Boolean.TRUE == oldValue && Boolean.FALSE == newValue) {
							//nothing can be dirty and new at the same time
							//we do not need this concept. either it was deactivated now or sometime earlier.
							taxonomyBuilder.removeNode(createNode(dirtyConcept.getId(), true));
						} else if (Boolean.FALSE == oldValue && Boolean.TRUE == newValue) {
							//consider reverting inactivation
							if (!taxonomyBuilder.containsNode(dirtyConcept.getId())) {
								taxonomyBuilder.addNode(createNode(dirtyConcept));
							}
						}
					}
					
				}
				LOGGER.info("Rebuilding taxonomic information based on the changes.");
				taxonomyBuilder.build();
			} finally {
				if (canCopyThreadLocal) {
					StoreThreadLocal.release();
				}
			}
			
		}
		
		/*creates a taxonomy edge instance based on the given SNOMED CT relationship*/
		private TaxonomyBuilderEdge createEdge(final Relationship relationship) {
			return new TaxonomyBuilderEdge() {
				@Override public boolean isCurrent() {
					return relationship.isActive();
				}
				@Override public String getId() {
					return relationship.getId();
				}
				@Override public boolean isValid() {
					return Concepts.IS_A.equals(relationship.getType().getId()) && characteristicTypeId.equals(relationship.getCharacteristicType().getId());
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
		private TaxonomyBuilderEdge createEdge(final SnomedRelationshipIndexEntry relationship) {
			return new TaxonomyBuilderEdge() {
				@Override public boolean isCurrent() {
					return relationship.isActive();
				}
				@Override public String getId() {
					return relationship.getId();
				}
				@Override public boolean isValid() {
					return Concepts.IS_A.equals(relationship.getAttributeId()) && characteristicTypeId.equals(relationship.getCharacteristicTypeId());
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
		private TaxonomyBuilderNode createNode(final Concept concept) {
			return new TaxonomyBuilderNode() {
				@Override public boolean isCurrent() {
					return concept.isActive();
				}
				@Override public String getId() {
					return concept.getId();
				}
			};
		}
		
		/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
		private TaxonomyBuilderNode createNode(final String id, final boolean active) {
			return new TaxonomyBuilderNode() {
				@Override public boolean isCurrent() {
					return active;
				}
				@Override public String getId() {
					return id;
				}
			};
		}
		
		private TaxonomyBuilderNode createDeletedNode(final ExtendedComponent concept) {
			return new TaxonomyBuilderNode() {
				@Override public boolean isCurrent() {
					throw new UnsupportedOperationException("This method should not be called when removing taxonomy nodes.");
				}
				@Override public String getId() {
					return concept.getId();
				}
			};
		}
	}

	/*returns with the taxonomy builder instance representing the latest state of the current ontology*/
	private ISnomedTaxonomyBuilder getInferredNewTaxonomyBuilder() {
		return inferredNewTaxonomyBuilderSupplier.get();
	}
	
	private ISnomedTaxonomyBuilder getStatedNewTaxonomyBuilder() {
		return statedNewTaxonomyBuilderSupplier.get();
	}

	/*returns with the new taxonomy builder instance. also checks it's state.*/
	private ISnomedTaxonomyBuilder getAndCheckInferredNewTaxonomyBuilder() {
		final ISnomedTaxonomyBuilder taxonomyBuilder = getInferredNewTaxonomyBuilder();
		Preconditions.checkState(!taxonomyBuilder.isDirty(), "Builder for representing the new state of the taxonomy has dirty state.");
		return taxonomyBuilder;
	}
	
	private ISnomedTaxonomyBuilder getAndCheckStatedNewTaxonomyBuilder() {
		final ISnomedTaxonomyBuilder taxonomyBuilder = getStatedNewTaxonomyBuilder();
		Preconditions.checkState(!taxonomyBuilder.isDirty(), "Builder for representing the new state of the taxonomy has dirty state.");
		return taxonomyBuilder;
	}
	
	/*returns with the taxonomy builder representing the state of the ontology before the commit that is currently being processed*/
	private ISnomedTaxonomyBuilder getInferredPreviousTaxonomyBuilder() {
		return inferredPreviousTaxonomyBuilderSupplier.get();
	}
	
	private ISnomedTaxonomyBuilder getStatedPreviousTaxonomyBuilder() {
		return statedPreviousTaxonomyBuilderSupplier.get();
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

	/*returns with the index document of a detached reference set member identified by its unique storage key.*/
	private Document getDocumentForDetachedMember(final CDOID id) {
		
		final long storageKey = CDOIDUtils.asLong(id);
		final Query query = SnomedMappings.newQuery().storageKey(storageKey).matchAll();
		
		final TopDocs topDocs = index.search(branchPath, query, 1);
		
		Preconditions.checkNotNull(topDocs, "Cannot find detached reference set member with its unique storage key: " + id);
		Preconditions.checkState(!CompareUtils.isEmpty(topDocs.scoreDocs), "Cannot find detached reference set member with its unique storage key: " + id);
		
		final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
		final Document doc = index.document(branchPath, scoreDoc.doc, MEMBER_FIELD_TO_LOAD);
		
		return Preconditions.checkNotNull(doc, "Cannot find detached reference set member with its unique storage key: " + id);
		
	}
	
	/*returns with the terminology browser service. always represents the previous state of the SNOMED CT ontology*/
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*returns with index service for SNOMED CT ontology*/
	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}
	
	private ISnomedIdentifierService getIdentifierService() {
		return ApplicationContext.getInstance().getService(ISnomedIdentifierService.class);
	}

	private void createLogEntry(final StringBuilder sb, final Set<ComponentIdAndLabel> logEntries) {
		sb.append(Joiner.on(", ").join(FluentIterable.from(logEntries).transform(new Function<ComponentIdAndLabel, String>() {
			@Override public String apply(ComponentIdAndLabel input) {
				return String.format("[%s:%s]", input.getId(), input.getLabel());
			}
		})));
	}

	/**returns with the CDO connection for SNOMED CT*/
	private ICDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}

}