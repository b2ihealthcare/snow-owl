/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.index.compat.SingleDirectoryIndexImpl;
import com.b2international.index.lucene.Fields;
import com.b2international.index.lucene.QueryBuilderBase.QueryBuilder;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.domain.StorageRef;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.classification.ChangeNature;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConcept;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChange;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;
import com.b2international.snowowl.snomed.api.exception.ClassificationRunNotFoundException;
import com.b2international.snowowl.snomed.api.impl.domain.classification.ClassificationRun;
import com.b2international.snowowl.snomed.api.impl.domain.classification.EquivalentConcept;
import com.b2international.snowowl.snomed.api.impl.domain.classification.EquivalentConceptSet;
import com.b2international.snowowl.snomed.api.impl.domain.classification.RelationshipChange;
import com.b2international.snowowl.snomed.api.impl.domain.classification.RelationshipChangeList;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.EquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponseChanges;
import com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry.Nature;
import com.b2international.snowowl.snomed.reasoner.classification.entry.RelationshipChangeEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

public class ClassificationRunIndex extends SingleDirectoryIndexImpl {

	private static final String FIELD_ID = "id";
	private static final String FIELD_CLASS = "class";
	private static final String FIELD_BRANCH_PATH = "branchPath";
	private static final String FIELD_USER_ID = "userId";
	private static final String FIELD_CREATION_DATE = "creationDate";
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_SOURCE = "source";
	private static final String FIELD_COMPONENT_ID = "componentId";
	
	private final ObjectMapper objectMapper;

	public ClassificationRunIndex(final File directory) {
		super(directory);
		objectMapper = new ObjectMapper();
	}

	public void trimIndex(int maximumResultsToKeep) throws IOException {
		final Query query = Fields.newQuery()
				.field(FIELD_CLASS, ClassificationRun.class.getSimpleName())
				.matchAll();
		
		// Sort by decreasing document order
		final Sort sort = new Sort(new SortField(null, Type.DOC, true));
		
		final ClassificationRun lastRunToKeep = Iterables.getFirst(search(query, ClassificationRun.class, sort, maximumResultsToKeep - 1, 1), null);
		if (lastRunToKeep == null) {
			return;
		}
		
		final Date lastCreationDate = lastRunToKeep.getCreationDate();
		final Query trimmingQuery = LongPoint.newRangeQuery(FIELD_CREATION_DATE, Long.MIN_VALUE, lastCreationDate.getTime());
		writer.deleteDocuments(trimmingQuery);
		commit();
	}
	
	public void invalidateClassificationRuns() throws IOException {
		
		final Query statusQuery = Fields.newQuery()
				.field(FIELD_STATUS, ClassificationStatus.COMPLETED.name())
				.field(FIELD_STATUS, ClassificationStatus.RUNNING.name())
				.field(FIELD_STATUS, ClassificationStatus.SAVING_IN_PROGRESS.name())
				.field(FIELD_STATUS, ClassificationStatus.SCHEDULED.name())
				.matchAny();
		
		final Query query = Fields.newQuery()
				.field(FIELD_CLASS, ClassificationRun.class.getSimpleName())
				.and(statusQuery)
				.matchAll();
		
		IndexSearcher searcher = null;

		try {

			searcher = manager.acquire();

			final TotalHitCountCollector collector = new TotalHitCountCollector();
			searcher.search(query, collector);
			final int totalHits = collector.getTotalHits();
			
			final int docsToRetrieve = Ints.min(searcher.getIndexReader().maxDoc(), totalHits);
			if (docsToRetrieve < 1) {
				return;
			}
			
			final TopDocs docs = searcher.search(query, docsToRetrieve, Sort.INDEXORDER, false, false);
			final ScoreDoc[] scoreDocs = docs.scoreDocs;

			final ObjectReader reader = objectMapper.reader(ClassificationRun.class);
			for (int i = 0; i < scoreDocs.length; i++) {
				final Document sourceDocument = searcher.doc(scoreDocs[i].doc, ImmutableSet.of(FIELD_BRANCH_PATH, FIELD_SOURCE));
				
				final String branchPath = sourceDocument.get(FIELD_BRANCH_PATH);
				final String source = sourceDocument.get(FIELD_SOURCE);
				final ClassificationRun run = reader.readValue(source);
				
				run.setStatus(ClassificationStatus.STALE);
				
				upsertClassificationRunNoCommit(BranchPathUtils.createPath(branchPath), run);
			}

			commit();
			
		} finally {
			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}

	public List<IClassificationRun> getAllClassificationRuns(final StorageRef storageRef, final String userId) throws IOException {
		final Query query = Fields.newQuery()
				.field(FIELD_CLASS, ClassificationRun.class.getSimpleName())
				.field(FIELD_USER_ID, userId)
				.field(FIELD_BRANCH_PATH, storageRef.getBranchPath())
				.matchAll();
		
		return this.<IClassificationRun>search(query, ClassificationRun.class);
	}

	public IClassificationRun getClassificationRun(final StorageRef storageRef, final String classificationId, final String userId) throws IOException {
		final Query query = createClassQuery(ClassificationRun.class.getSimpleName(), classificationId, storageRef, null, userId);

		try {
			return Iterables.getOnlyElement(search(query, ClassificationRun.class, 1));
		} catch (final NoSuchElementException e) {
			throw new ClassificationRunNotFoundException(classificationId);
		}
	}

	public void upsertClassificationRun(final IBranchPath branchPath, final ClassificationRun classificationRun) throws IOException {
		upsertClassificationRunNoCommit(branchPath, classificationRun);
		commit();
	}

	private void upsertClassificationRunNoCommit(final IBranchPath branchPath, final ClassificationRun classificationRun) throws IOException {
		final Document updatedDocument = new Document();
		
		Fields.searchOnlyStringField(FIELD_CLASS).addTo(updatedDocument, ClassificationRun.class.getSimpleName());
		Fields.searchOnlyStringField(FIELD_ID).addTo(updatedDocument, classificationRun.getId());
		Fields.searchOnlyStringField(FIELD_STATUS).addTo(updatedDocument, classificationRun.getStatus().name());
		Fields.longField(FIELD_CREATION_DATE).addTo(updatedDocument, classificationRun.getCreationDate().getTime());
		Fields.stringField(FIELD_USER_ID).addTo(updatedDocument, classificationRun.getUserId());
		Fields.stringField(FIELD_BRANCH_PATH).addTo(updatedDocument, branchPath.getPath());
		Fields.storedOnlyStringField(FIELD_SOURCE).addTo(updatedDocument, objectMapper.writer().writeValueAsString(classificationRun));
		
		final Query query = Fields.newQuery()
				.field(FIELD_CLASS, ClassificationRun.class.getSimpleName())
				.field(FIELD_ID, classificationRun.getId())
				.matchAll();

		writer.deleteDocuments(query);
		writer.addDocument(updatedDocument);
	}

	public void updateClassificationRunStatus(final String id, final ClassificationStatus newStatus) throws IOException {
		updateClassificationRunStatus(id, newStatus, null);
	}

	public void updateClassificationRunStatus(final String id, final ClassificationStatus newStatus, final GetResultResponseChanges changes) throws IOException {

		final Document sourceDocument = getClassificationRunDocument(id);
		if (null == sourceDocument) {
			return;
		}

		final IBranchPath branchPath = BranchPathUtils.createPath(sourceDocument.get(FIELD_BRANCH_PATH));
		final ClassificationRun classificationRun = objectMapper.reader(ClassificationRun.class).readValue(sourceDocument.get(FIELD_SOURCE));

		if (newStatus.equals(classificationRun.getStatus())) {
			return;
		}

		classificationRun.setStatus(newStatus);

		if (ClassificationStatus.COMPLETED.equals(newStatus)) {
			checkNotNull(changes, "GetResultResponseChanges are required to update a completed classification.");

			if (null == classificationRun.getCompletionDate()) {
				classificationRun.setCompletionDate(new Date());
			}
			
			final ClassificationIssueFlags issueFlags = indexChanges(sourceDocument, id, changes);
			classificationRun.setInferredRelationshipChangesFound(!changes.getRelationshipEntries().isEmpty());
			classificationRun.setRedundantStatedRelationshipsFound(issueFlags.isRedundantStatedFound());
			classificationRun.setEquivalentConceptsFound(issueFlags.isEquivalentConceptsFound());
		} else if (ClassificationStatus.SAVED.equals(newStatus)) {
			classificationRun.setSaveDate(new Date());
		}

		upsertClassificationRun(branchPath, classificationRun);
	}

	private SnomedRelationship getRelationship(IBranchPath branchPath, RelationshipChangeEntry relationshipChange) {
		return Iterables.getOnlyElement(SnomedRequests.prepareSearchRelationship()
				.setLimit(1)
				.filterBySource(relationshipChange.getSource().getId().toString())
				.filterByDestination(relationshipChange.getDestination().getId().toString())
				.filterByType(relationshipChange.getType().getId().toString())
				.filterByGroup(relationshipChange.getGroup())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync(), null);
	}

	public void deleteClassificationData(final String classificationId) throws IOException {
		// Removes all documents, not just the classification run document
		writer.deleteDocuments(new Term(FIELD_ID, classificationId));
		commit();
	}

	private ClassificationIssueFlags indexChanges(Document sourceDocument, String id, final GetResultResponseChanges changes) throws IOException {
		final IBranchPath branchPath = BranchPathUtils.createPath(sourceDocument.get(FIELD_BRANCH_PATH));
		final String userId = sourceDocument.get(FIELD_USER_ID);
		final long creationDate = sourceDocument.getField(FIELD_CREATION_DATE).numericValue().longValue();
		final ClassificationIssueFlags classificationIssueFlags = new ClassificationIssueFlags();

		final List<AbstractEquivalenceSet> equivalenceSets = changes.getEquivalenceSets();
		classificationIssueFlags.setEquivalentConceptsFound(!equivalenceSets.isEmpty());
		for (final AbstractEquivalenceSet equivalenceSet : equivalenceSets) {

			final List<IEquivalentConcept> convertedEquivalentConcepts = newArrayList();
			for (final String equivalentId : equivalenceSet.getConceptIds()) {
				addEquivalentConcept(convertedEquivalentConcepts, equivalentId);
			}

			if (equivalenceSet instanceof EquivalenceSet) {
				addEquivalentConcept(convertedEquivalentConcepts, ((EquivalenceSet) equivalenceSet).getSuggestedConceptId());
			}
			
			final EquivalentConceptSet convertedEquivalenceSet = new EquivalentConceptSet();
			convertedEquivalenceSet.setUnsatisfiable(equivalenceSet.isUnsatisfiable());
			convertedEquivalenceSet.setEquivalentConcepts(convertedEquivalentConcepts);

			indexResult(id, branchPath, userId, creationDate, EquivalentConceptSet.class, equivalenceSet.getConceptIds().get(0), convertedEquivalenceSet);
		}

		for (final RelationshipChangeEntry relationshipChange : changes.getRelationshipEntries()) {

			final RelationshipChange convertedRelationshipChange = new RelationshipChange();
			final ChangeNature changeNature = Nature.INFERRED.equals(relationshipChange.getNature()) ? ChangeNature.INFERRED : ChangeNature.REDUNDANT;
			convertedRelationshipChange.setChangeNature(changeNature);
			convertedRelationshipChange.setDestinationId(Long.toString(relationshipChange.getDestination().getId()));
			convertedRelationshipChange.setDestinationNegated(relationshipChange.isDestinationNegated());

			final String characteristicTypeId;
			if (changeNature == ChangeNature.INFERRED) {
				characteristicTypeId = Concepts.INFERRED_RELATIONSHIP;
			} else {
				final SnomedRelationship existingRelationship = getRelationship(branchPath, relationshipChange);
				characteristicTypeId = existingRelationship.getCharacteristicType().getConceptId();
				if (changeNature == ChangeNature.REDUNDANT && characteristicTypeId.equals(Concepts.STATED_RELATIONSHIP)) {
					classificationIssueFlags.setRedundantStatedFound(true);
				}
			}
			convertedRelationshipChange.setCharacteristicTypeId(characteristicTypeId);
			convertedRelationshipChange.setGroup(relationshipChange.getGroup());

			final String modifierId = Long.toString(relationshipChange.getModifier().getId());
			convertedRelationshipChange.setModifier(Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(modifierId) ? RelationshipModifier.UNIVERSAL : RelationshipModifier.EXISTENTIAL);
			convertedRelationshipChange.setSourceId(Long.toString(relationshipChange.getSource().getId()));
			convertedRelationshipChange.setTypeId(Long.toString(relationshipChange.getType().getId()));
			convertedRelationshipChange.setUnionGroup(relationshipChange.getUnionGroup());

			indexResult(id, branchPath, userId, creationDate, RelationshipChange.class, convertedRelationshipChange.getSourceId(), convertedRelationshipChange);
		}

		commit();
		return classificationIssueFlags;
	}

	private void addEquivalentConcept(final List<IEquivalentConcept> convertedEquivalentConcepts, final String equivalentId) {
		final EquivalentConcept convertedConcept = new EquivalentConcept();
		convertedConcept.setId(equivalentId);
		convertedEquivalentConcepts.add(convertedConcept);
	}

	/**
	 * @param storageRef
	 * @param classificationId
	 * @param userId
	 * @return
	 */
	public List<IEquivalentConceptSet> getEquivalentConceptSets(final StorageRef storageRef, final String classificationId, final String userId) throws IOException {

		final Query query = createClassQuery(EquivalentConceptSet.class.getSimpleName(), classificationId, storageRef, null, userId);
		return this.<IEquivalentConceptSet>search(query, EquivalentConceptSet.class);
	}

	/**
	 * @param storageRef
	 * @param classificationId
	 * @param sourceConceptId used to restrict results, can be null
	 * @param userId
	 * @param limit
	 * @param offset
	 * @return
	 */
	public IRelationshipChangeList getRelationshipChanges(final StorageRef storageRef, final String classificationId, final String sourceConceptId, final String userId, final int offset, final int limit) throws IOException {

		final Query query = createClassQuery(RelationshipChange.class.getSimpleName(), classificationId, storageRef, sourceConceptId, userId);
		final int total = getHitCount(query);
		final List<IRelationshipChange> changes = this.search(query, RelationshipChange.class, offset, limit);
		return new RelationshipChangeList(changes, offset, limit, total);
	}

	private <T> void indexResult(final String id, final IBranchPath branchPath, final String userId, final long creationDate,
			final Class<T> clazz, String componentId, final T value) throws IOException {
		final Document doc = new Document();
		
		Fields.searchOnlyStringField(FIELD_CLASS).addTo(doc, clazz.getSimpleName());
		Fields.searchOnlyStringField(FIELD_ID).addTo(doc, id.toString());
		Fields.searchOnlyStringField(FIELD_USER_ID).addTo(doc, userId);
		Fields.searchOnlyLongField(FIELD_CREATION_DATE).addTo(doc, creationDate);
		Fields.searchOnlyStringField(FIELD_BRANCH_PATH).addTo(doc, branchPath.getPath());
		Fields.searchOnlyStringField(FIELD_COMPONENT_ID).addTo(doc, componentId);
		Fields.storedOnlyStringField(FIELD_SOURCE).addTo(doc, objectMapper.writer().writeValueAsString(value));
				
		writer.addDocument(doc);
	}

	private Document getClassificationRunDocument(final String id) throws IOException {
		final Query query = Fields.newQuery()
				.field(FIELD_CLASS, ClassificationRun.class.getSimpleName())
				.field(FIELD_ID, id.toString())
				.matchAll();
		return Iterables.getFirst(search(query, 1), null);
	}

	private Query createClassQuery(final String className, final String classificationId, final StorageRef storageRef, String componentId, final String userId) {
		final QueryBuilder query = Fields.newQuery()
				.field(FIELD_CLASS, className)
				.field(FIELD_ID, classificationId)
				.field(FIELD_USER_ID, userId)
				.field(FIELD_BRANCH_PATH, storageRef.getBranchPath());
		if (componentId != null) {
			query.field(FIELD_COMPONENT_ID, componentId);
		}
		return query.matchAll();
	}

	private <T> List<T> search(final Query query, final Class<? extends T> sourceClass) throws IOException {
		return search(query, sourceClass, Integer.MAX_VALUE);
	}

	private <T> List<T> search(final Query query, final Class<? extends T> sourceClass, final int limit) throws IOException {
		return search(query, sourceClass, 0, limit);
	}

	private <T> List<T> search(final Query query, final Class<? extends T> sourceClass, final int offset, final int limit) throws IOException {
		return search(query, sourceClass, Sort.INDEXORDER, offset, limit);
	}

	private <T> List<T> search(final Query query, final Class<? extends T> sourceClass, Sort sort, final int offset, final int limit) throws IOException {
		IndexSearcher searcher = null;

		try {

			searcher = manager.acquire();

			final TotalHitCountCollector collector = new TotalHitCountCollector();
			searcher.search(query, collector);
			final int totalHits = collector.getTotalHits();
			
			final int saturatedSum = Ints.saturatedCast((long) offset + limit);
			final int docsToRetrieve = Ints.min(saturatedSum, searcher.getIndexReader().maxDoc(), totalHits);
			final ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();
			
			if (docsToRetrieve < 1) {
				return resultBuilder.build();
			}
			
			final TopDocs docs = searcher.search(query, docsToRetrieve, sort, false, false);
			final ScoreDoc[] scoreDocs = docs.scoreDocs;

			final ObjectReader reader = objectMapper.reader(sourceClass);
			for (int i = offset; i < docsToRetrieve && i < scoreDocs.length; i++) {
				final Document sourceDocument = searcher.doc(scoreDocs[i].doc, ImmutableSet.of(FIELD_SOURCE));
				final String source = sourceDocument.get(FIELD_SOURCE);
				final T deserializedSource = reader.readValue(source);
				resultBuilder.add(deserializedSource);
			}

			return resultBuilder.build();

		} finally {

			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}

	private List<Document> search(final Query query, final int limit) throws IOException {
		IndexSearcher searcher = null;

		try {

			searcher = manager.acquire();
			final TopDocs docs = searcher.search(query, limit, Sort.INDEXORDER, false, false);
			final ImmutableList.Builder<Document> resultBuilder = ImmutableList.builder();

			for (final ScoreDoc scoreDoc : docs.scoreDocs) {
				resultBuilder.add(searcher.doc(scoreDoc.doc));
			}

			return resultBuilder.build();

		} finally {

			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}

	private int getHitCount(final Query query) throws IOException {
		IndexSearcher searcher = null;

		try {

			searcher = manager.acquire();
			final TotalHitCountCollector collector = new TotalHitCountCollector();
			searcher.search(query, collector);
			return collector.getTotalHits();

		} finally {

			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}

	private class ClassificationIssueFlags {
		private boolean redundantStatedFound;
		private boolean equivalentConceptsFound;

		public boolean isRedundantStatedFound() {
			return redundantStatedFound;
		}

		public void setRedundantStatedFound(boolean redundantStatedFound) {
			this.redundantStatedFound = redundantStatedFound;
		}

		public boolean isEquivalentConceptsFound() {
			return equivalentConceptsFound;
		}

		public void setEquivalentConceptsFound(boolean equivalentConceptsFound) {
			this.equivalentConceptsFound = equivalentConceptsFound;
		}
	}
}
