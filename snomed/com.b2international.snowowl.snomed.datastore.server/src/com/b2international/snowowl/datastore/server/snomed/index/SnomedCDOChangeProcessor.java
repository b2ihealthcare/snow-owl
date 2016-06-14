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
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.StringUtils;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionIndexWrite;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.ChangeSetProcessor;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.index.change.ConceptChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.ConstraintChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.DescriptionChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.RefSetMemberChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.RelationshipChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ParentageUpdater;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Change processor implementation for SNOMED&nbsp;CT ontology. This class is responsible for updating indexes based on the new, dirty and detached components 
 * after a successful backend commit.
 * @see ICDOChangeProcessor
 * @see ICDOCommitChangeSet
 */
public class SnomedCDOChangeProcessor implements ICDOChangeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedCDOChangeProcessor.class);
	
	private final Set<CodeSystem> newCodeSystems = newHashSet();
	private final Set<CodeSystemVersion> newCodeSystemVersions = newHashSet();
	private final Set<CodeSystemVersion> dirtyCodeSystemVersions = newHashSet();
	
	private final IBranchPath branchPath;
	private final SnomedStatementBrowser statementBrowser;
	private final ISnomedIdentifierService identifierService;
	private final RevisionIndex index;
	
	private Taxonomy inferredTaxonomy;
	private Taxonomy statedTaxonomy;
	
	/**Represents the change set.*/
	private ICDOCommitChangeSet commitChangeSet;

	private Map<Long, Revision> mappings;
	private Multimap<Class<? extends Revision>, Long> deletions;


	public SnomedCDOChangeProcessor(final IBranchPath branchPath, final RevisionIndex index, 
			final SnomedStatementBrowser statementBrowser, final ISnomedIdentifierService identifierService) {
		this.index = index;
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.statementBrowser = statementBrowser;
		this.identifierService = identifierService;
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
		
		getIndex().read(branchPath.getPath(), new RevisionIndexRead<Void>() {
			@Override
			public Void execute(RevisionSearcher index) throws IOException {
				updateDocuments(index);
				return null;
			}
		});
	}

	@Override
	public void prepareCommit() throws SnowowlServiceException {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	@Override
	public void commit() throws SnowowlServiceException {
		checkState(commitChangeSet.getTimestamp() > 0, "Commit timestamp should be greater than zero");
		getIndex().write(branchPath.getPath(), commitChangeSet.getTimestamp(), new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter writer) throws IOException {
				LOGGER.info("Persisting changes...");
				for (Class<? extends Revision> type : ImmutableMultimap.copyOf(deletions).keySet()) {
					writer.remove(type, Sets.newHashSet(deletions.get(type)));
				}
				
				for (Entry<Long, Revision> doc : mappings.entrySet()) {
					if (!deletions.containsValue(doc.getKey())) {
						writer.put(doc.getKey(), doc.getValue());
					}
				}
				writer.commit();
				LOGGER.info("Changes have been successfully persisted.");
				return null;
			}
		});
	}

	private RevisionIndex getIndex() {
		return index;
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
		return "TODO";
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
		// XXX nothing to do, just don't commit the writer
	}

	@Override
	public String getName() {
		return "SNOMED CT Terminology";
	}

	/*updates the documents in the indexes based on the dirty, detached and new components.*/
	private void updateDocuments(RevisionSearcher searcher) throws IOException {
		LOGGER.info("Processing and updating changes...");
		
//		final LongSet conceptIds = LongSets.
		// TODO refactor code system update into updaters
//		for (final CodeSystem newCodeSystem : newCodeSystems) {
//			new CodeSystemIndexMappingStrategy(newCodeSystem).createDocument();
//		}
//		
//		for (final CodeSystemVersion newCodeSystemVersion : newCodeSystemVersions) {
//			index.index(branchPath, new CodeSystemVersionIndexMappingStrategy(newCodeSystemVersion));
//		}
//		
//		for (final CodeSystemVersion dirtyCodeSystemVersion : dirtyCodeSystemVersions) {
//			index.index(branchPath, new CodeSystemVersionIndexMappingStrategy(dirtyCodeSystemVersion));
//		}

		final Query<SnomedConceptDocument> allConceptsQuery = Query.builder(SnomedConceptDocument.class)
				.selectAll()
				.where(Expressions.matchAll())
				.limit(Integer.MAX_VALUE)
				.build();
		final Set<Long> deletedConceptStorageKeys = ImmutableSet.copyOf(CDOIDUtils.createCdoIdToLong(commitChangeSet.getDetachedComponents(SnomedPackage.Literals.CONCEPT)));
		final Hits<SnomedConceptDocument> existingConcepts = searcher.search(allConceptsQuery);
		final LongSet allConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(existingConcepts.getTotal() - deletedConceptStorageKeys.size());
		// add all current ids, except the deleted ones
		for (SnomedConceptDocument existingConcept : existingConcepts) {
			if (!deletedConceptStorageKeys.contains(existingConcept.getStorageKey())) {
				allConceptIds.add(Long.parseLong(existingConcept.getId()));
			}
		}
		for (Concept newConcept : FluentIterable.from(commitChangeSet.getNewComponents()).filter(Concept.class)) {
			allConceptIds.add(Long.parseLong(newConcept.getId()));
		}
		
		prepareTaxonomyBuilders(searcher, allConceptIds);
		
		final Collection<ConstraintDomain> allConstraintDomains = Collections.emptySet();
		
		final Collection<ChangeSetProcessor> changeSetProcessors = newHashSet();
		final ParentageUpdater inferred = new ParentageUpdater(inferredTaxonomy.getNewTaxonomy(), false);
		final ParentageUpdater stated = new ParentageUpdater(statedTaxonomy.getNewTaxonomy(), true);
		final IconIdUpdater iconId = new IconIdUpdater(inferredTaxonomy.getNewTaxonomy(), statedTaxonomy.getNewTaxonomy(), SnomedIconProvider.getInstance().getAvailableIconIds());
		changeSetProcessors.add(new ConceptChangeProcessor(branchPath, allConceptIds, allConstraintDomains , iconId, inferred, stated, statedTaxonomy, inferredTaxonomy));
		changeSetProcessors.add(new DescriptionChangeProcessor());
		changeSetProcessors.add(new RelationshipChangeProcessor());
		changeSetProcessors.add(new RefSetMemberChangeProcessor());
		changeSetProcessors.add(new ConstraintChangeProcessor());
		
		for (ChangeSetProcessor processor : changeSetProcessors) {
			LOGGER.info("Collecting {}...", processor.description());
			processor.process(commitChangeSet, searcher);
			// register additions, deletions from the sub processor
			mappings.putAll(processor.getMappings());
			deletions.putAll(processor.getDeletions());
		}

		LOGGER.info("Updating indexes...");

		final Collection<String> releasableComponentIds = getReleasableComponentIds();
		if (!releasableComponentIds.isEmpty()) {
			identifierService.release(releasableComponentIds);
		}
		
		LOGGER.info("Processing and updating index changes successfully finished.");
	}

	private Collection<String> getReleasableComponentIds() {
		final Collection<String> releasableComponentIds = newHashSet();
		for (Class<? extends Revision> type : deletions.keySet()) {
			if (isCoreComponent(type)) {
				releasableComponentIds.addAll(getReleasableComponentIds((Class<? extends RevisionDocument>) type, deletions.get(type)));
			}
		}
		return releasableComponentIds;
	}

	private boolean isCoreComponent(Class<? extends Revision> type) {
		return SnomedConceptDocument.class == type || SnomedDescriptionIndexEntry.class == type || SnomedRelationshipIndexEntry.class == type;
	}

	private Collection<String> getReleasableComponentIds(final Class<? extends RevisionDocument> type, final Iterable<Long> storageKeys) {
		return ImmutableSet.copyOf(ComponentUtils.<String>getIds(getReleasableComponents(type, storageKeys)));
	}

	private <T extends RevisionDocument> Iterable<T> getReleasableComponents(final Class<T> type, final Iterable<Long> storageKeys) {
		IBranchPath currentBranchPath = getBranchPath();
		final Set<Long> releasableStorageKeys = newHashSet(storageKeys);

		while (!StringUtils.isEmpty(currentBranchPath.getParentPath())) {
			currentBranchPath = currentBranchPath.getParent();
			final Iterable<T> hits = index.read(currentBranchPath.getPath(), new RevisionIndexRead<Iterable<T>>() {
				@Override
				public Iterable<T> execute(RevisionSearcher index) throws IOException {
					return index.get(type, releasableStorageKeys);
				}
			});
			for (T hit : hits) {
				// the ID of this component cannot be released because it is being used on an ancestor branch
				releasableStorageKeys.remove(hit.getStorageKey());
			}
		}
		
		// the remaining storageKeys can be removed, since they are not in use on any ancestor branch
		return getRevisions(type, releasableStorageKeys);
	}

	/**
	 * @deprecated - would be great to use a single revision searcher
	 */
	private <T extends RevisionDocument> Iterable<T> getRevisions(final Class<T> type, final Iterable<Long> releasableStorageKeys) {
		return index.read(branchPath.getPath(), new RevisionIndexRead<Iterable<T>>() {
			@Override
			public Iterable<T> execute(RevisionSearcher index) throws IOException {
				return index.get(type, releasableStorageKeys);
			}
		});
	}

	/**
	 * Prepares the taxonomy builder. One for representing the previous state of the ontology.
	 * One for the new state.   
	 */
	private void prepareTaxonomyBuilders(final RevisionSearcher searcher, final LongSet conceptIds) {
		LOGGER.info("Retrieving taxonomic information from store.");
		final IStoreAccessor accessor = StoreThreadLocal.getAccessor();
		
		final Runnable inferredRunnable = CDOServerUtils.withAccessor(new Runnable() {
			@Override
			public void run() {
				inferredTaxonomy = Taxonomies.inferred(searcher, branchPath, commitChangeSet, conceptIds, statementBrowser);
			}
		}, accessor);
		
		final Runnable statedRunnable = CDOServerUtils.withAccessor(new Runnable() {
			@Override
			public void run() {
				statedTaxonomy = Taxonomies.stated(searcher, branchPath, commitChangeSet, conceptIds, statementBrowser);
			}
		}, accessor);
		
		ForkJoinUtils.runInParallel(inferredRunnable, statedRunnable);
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

}
