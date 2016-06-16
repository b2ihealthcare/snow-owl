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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.ConstantScoreQuery;
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
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.api.ComponentIdAndLabel;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.ChangeSetProcessor;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexUpdater;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.DocumentCompositeUpdater;
import com.b2international.snowowl.datastore.index.DocumentUpdater;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.LongIndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.index.IndexBranchService;
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
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder.Factory;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemIndexMappingStrategy;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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
	
	private final IBranchPath branchPath;
	private final SnomedIndexServerService index;
	private final SnomedTerminologyBrowser terminologyBrowser;
	private final SnomedStatementBrowser statementBrowser;
	private final ISnomedIdentifierService identifierService;
	
	private Taxonomy inferredTaxonomy;
	private Taxonomy statedTaxonomy;
	
	/**Represents the change set.*/
	private ICDOCommitChangeSet commitChangeSet;

	private final LongCollection conceptIds;

	/**
	 * Creates a new change processor for the SNOMED&nbsp;CT ontology. 
	 * @param indexUpdater the index updater for the SNOMED&nbsp;CT ontology.
	 * @param branchPath the branch path where the changes has to be calculated and processed.
	 */
	public SnomedCDOChangeProcessor(final IBranchPath branchPath, 
			final SnomedTerminologyBrowser terminologyBrowser, 
			final SnomedStatementBrowser statementBrowser,
			final ISnomedIdentifierService identifierService,
			final IIndexUpdater<SnomedIndexEntry> indexUpdater) {
		Preconditions.checkNotNull(indexUpdater, "Index service argument cannot be null.");
		Preconditions.checkArgument(indexUpdater instanceof AbstractIndexUpdater, "Index updater must be instance of " + AbstractIndexUpdater.class + ". Was " + indexUpdater.getClass());
		this.index = (SnomedIndexServerService) indexUpdater;
		this.terminologyBrowser = terminologyBrowser;
		this.statementBrowser = statementBrowser;
		this.identifierService = identifierService;
		
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.conceptIds = terminologyBrowser.getAllConceptIds(branchPath);
		
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
		final LongSet allConceptIds = PrimitiveSets.newLongOpenHashSet(conceptIds);
		for (Concept newConcept : FluentIterable.from(commitChangeSet.getNewComponents()).filter(Concept.class)) {
			allConceptIds.add(Long.parseLong(newConcept.getId()));
		}
		
		for (Entry<CDOID, EClass> entry : commitChangeSet.getDetachedComponents().entrySet()) {
			if (entry.getValue() == SnomedPackage.Literals.CONCEPT) {
				final ExtendedComponent componentIdAndLabel = terminologyBrowser.getExtendedComponent(branchPath, CDOIDUtil.getLong(entry.getKey()));
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
				.add(new TaxonomyChangeProcessor(inferredTaxonomy,  ""))
				.add(new TaxonomyChangeProcessor(statedTaxonomy, Concepts.STATED_RELATIONSHIP))
				.add(new IconChangeProcessor(branchPath, inferredTaxonomy, statedTaxonomy))
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
		final Collection<String> releasableComponentIds = newHashSet();
		
		for (ChangeSetProcessor<SnomedDocumentBuilder> processor : changeSetProcessors) {
			for (Long storageKey : processor.getDeletedStorageKeys()) {
				LOGGER.trace("Deleting document {}", storageKey);
				index.delete(branchPath, storageKey);
				deletedStorageKeys.add(LongIndexField._toBytesRef(storageKey));
				releasableComponentIds.addAll(getReleasableComponentIds(processor, storageKey));
			}
		}
		
		if (!releasableComponentIds.isEmpty()) {
			identifierService.release(releasableComponentIds);
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
			deletedComponentIds	= PrimitiveLists.newLongArrayList();
			deletedMemberIds = Collections.emptySet();
		}
		
		final Multimap<String, DocumentUpdater<SnomedDocumentBuilder>> updates = LinkedHashMultimap.create();
		for (ChangeSetProcessor<SnomedDocumentBuilder> processor : changeSetProcessors) {
			updates.putAll(processor.getUpdates());
		}
		
		final Set<String> componentIdsToUpdate = FluentIterable.from(updates.keySet()).filter(new Predicate<String>() {
			@Override
			public boolean apply(String componentId) {
				if (updates.get(componentId).isEmpty()) {
					return false;
				}
				try {
					// skip deleted and non-updated components
					return !deletedComponentIds.contains(Long.parseLong(componentId));
				} catch (NumberFormatException e) {
					// multiple ID formats are expected, so parsing a long may not work all the time
					return !deletedMemberIds.contains(componentId);
				}
			}
		}).toSet();

		indexUpdates(componentIdsToUpdate, updates);
		
		LOGGER.info("Processing and updating index changes successfully finished.");
	}

	private void indexUpdates(final Set<String> componentIdsToUpdate, final Multimap<String, DocumentUpdater<SnomedDocumentBuilder>> updates) {
		if (componentIdsToUpdate.isEmpty()) {
			return;
		}
		LOGGER.info("(Re)indexing {} documents", componentIdsToUpdate.size());
		// fetch all necessary documents at once to update them 
		final Collection<Document> documentsToIndex = index.executeReadTransaction(branchPath, new IndexRead<Collection<Document>>() {
			@Override
			public Collection<Document> execute(IndexSearcher index) throws IOException {
				final Query query = getDocsToUpdateQuery(componentIdsToUpdate);
				
				final TopDocs result = index.search(query, componentIdsToUpdate.size() * 2);
				final Map<String, Document> docsToIndex = newHashMap();
				final Set<String> newComponentIds = newHashSet(componentIdsToUpdate);
				final Factory docBuilderFactory = new SnomedDocumentBuilder.Factory();
				// update documents
				for (int i = 0; i < result.scoreDocs.length; i++) {
					final Document doc = index.doc(result.scoreDocs[i].doc);
					
					final String docComponentId;
					if (doc.getField(SnomedMappings.id().fieldName()) != null) {
						docComponentId = SnomedMappings.id().getValueAsString(doc);
					} else if (doc.getField(SnomedMappings.memberUuid().fieldName()) != null) {
						docComponentId = SnomedMappings.memberUuid().getValueAsString(doc);
					} else if (doc.getField(Mappings.storageKey().fieldName()) != null) {
						docComponentId = Mappings.storageKey().getValueAsString(doc);
					} else {
						throw new UnsupportedOperationException("Missing known (either SNOMED ID/Member ID/MRCM storageKey) field on document: " + doc);
					}
					// this is not a new ID, execute update
					newComponentIds.remove(docComponentId);
					final Collection<DocumentUpdater<SnomedDocumentBuilder>> updaters = updates.get(docComponentId);
					final DocumentCompositeUpdater<SnomedDocumentBuilder> updater = new DocumentCompositeUpdater<>(updaters);
					final SnomedDocumentBuilder builder = docBuilderFactory.createBuilder(doc);
					updater.update(builder);
					checkState(!docsToIndex.containsKey(docComponentId), "Multiple documents found for ID '%s'", docComponentId);
					docsToIndex.put(docComponentId, builder.build());
				}
				
				// process remaining IDs, they are new documents
				for (String newComponentId : newComponentIds) {
					final Collection<DocumentUpdater<SnomedDocumentBuilder>> updaters = updates.get(newComponentId);
					final DocumentCompositeUpdater<SnomedDocumentBuilder> updater = new DocumentCompositeUpdater<>(updaters);
					final SnomedDocumentBuilder builder = docBuilderFactory.createBuilder();
					updater.update(builder);
					docsToIndex.put(newComponentId, builder.build());
				}
				
				return docsToIndex.values();
			}
			
		});
		
		final IndexBranchService branchIndex = index.getBranchService(branchPath);
		for (Document docToIndex : documentsToIndex) {
			try {
				branchIndex.updateDocument(docToIndex);
			} catch (IOException e) {
				throw new IndexException(e);
			}
		}
	}
	
	private Query getDocsToUpdateQuery(final Set<String> componentIdsToUpdate) {
		final Multimap<IndexField<String>, String> stringValuesByField = ArrayListMultimap.create();
		final Multimap<IndexField<Long>, Long> longValuesByField = ArrayListMultimap.create();
		for (final String componentId : componentIdsToUpdate) {
			try {
				final Long componentIdLong = Long.valueOf(componentId);
				// components are indexes with their long SNOMED CT ID
				// predicates are indexed with their storageKey
				longValuesByField.put(SnomedMappings.id(), componentIdLong);
				longValuesByField.put(Mappings.storageKey(), componentIdLong);
			} catch (NumberFormatException e) {
				// members are indexes with their UUID
				stringValuesByField.put(SnomedMappings.memberUuid(), componentId);
			}
		}
		final BooleanFilter filter = new BooleanFilter();
		for (IndexField<Long> field : longValuesByField.keySet()) {
			filter.add(field.createTermsFilter(longValuesByField.get(field)), Occur.SHOULD);
		}
		for (IndexField<String> field : stringValuesByField.keySet()) {
			filter.add(field.createTermsFilter(stringValuesByField.get(field)), Occur.SHOULD);
		}
		return new ConstantScoreQuery(filter);
	}

	private Collection<String> getReleasableComponentIds(final ChangeSetProcessor<SnomedDocumentBuilder> processor, final Long storageKey) {
		final Collection<String> releasableComponentIds = newHashSet();

		if (releaseSupported(processor)) {
			final String componentId = getComponentId(storageKey);
			if (releasable(componentId)) {
				releasableComponentIds.add(componentId);
			}
		}

		return releasableComponentIds;
	}

	private boolean releaseSupported(final ChangeSetProcessor<SnomedDocumentBuilder> processor) {
		return processor instanceof ConceptChangeProcessor || processor instanceof DescriptionChangeProcessor || processor instanceof RelationshipChangeProcessor;
	}

	private boolean releasable(final String id) {
		IBranchPath currentBranchPath = getBranchPath();

		while (!StringUtils.isEmpty(currentBranchPath.getParentPath())) {
			currentBranchPath = currentBranchPath.getParent();

			final int hitCount = index.getTotalHitCount(currentBranchPath, SnomedMappings.newQuery().id(id).matchAll());
			if (hitCount > 0) {
				return false;
			}
		}

		return true;
	}

	private String getComponentId(final Long storageKey) {
		return index.executeReadTransaction(branchPath, new IndexRead<String>() {
			@Override
			public String execute(IndexSearcher index) throws IOException {
				TopDocs topDocs = index.search(SnomedMappings.newQuery().storageKey(storageKey).matchAll(), 1);
				Document doc = index.doc(topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().id().build());
				return SnomedMappings.id().getValueAsString(doc);
			}
		});
	}

	/**
	 * Prepares the taxonomy builder. One for representing the previous state of the ontology.
	 * One for the new state.   
	 */
	private void prepareTaxonomyBuilders() {
		LOGGER.info("Retrieving taxonomic information from store.");
		final IStoreAccessor accessor = StoreThreadLocal.getAccessor();
		
		final Runnable inferredRunnable = CDOServerUtils.withAccessor(new Runnable() {
			@Override
			public void run() {
				inferredTaxonomy = Taxonomies.inferred(branchPath, commitChangeSet, conceptIds, statementBrowser);
			}
		}, accessor);
		
		final Runnable statedRunnable = CDOServerUtils.withAccessor(new Runnable() {
			@Override
			public void run() {
				statedTaxonomy = Taxonomies.stated(branchPath, commitChangeSet, conceptIds, statementBrowser);
			}
		}, accessor);
		
		ForkJoinUtils.runInParallel(inferredRunnable, statedRunnable);
	}
	
	@SuppressWarnings("restriction")
	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component) {
		final CodeSystemVersion version = (CodeSystemVersion) component;
		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID()).getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta).setValue(new Date(commitChangeSet.getTimestamp()));
			((InternalCDORevision) component.cdoRevision()).set(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate(), CDOStore.NO_INDEX, new Date(commitChangeSet.getTimestamp()));
			dirtyCodeSystemVersions.add(version);
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
	
	private void createLogEntry(final StringBuilder sb, final Set<ComponentIdAndLabel> logEntries) {
		for (final ComponentIdAndLabel logEntry : logEntries) {
			sb.append("[");
			sb.append(logEntry.getId());
			sb.append(":");
			sb.append(logEntry.getLabel());
			sb.append("], ");
		}
	}
}
