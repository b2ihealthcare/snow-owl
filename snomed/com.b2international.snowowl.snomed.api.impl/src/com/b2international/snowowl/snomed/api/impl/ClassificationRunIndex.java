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
package com.b2international.snowowl.snomed.api.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.index.mapping.QueryBuilderBase.QueryBuilder;
import com.b2international.snowowl.datastore.server.domain.StorageRef;
import com.b2international.snowowl.datastore.store.SingleDirectoryIndexImpl;
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
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.EquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.GetResultResponseChanges;
import com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry.Nature;
import com.b2international.snowowl.snomed.reasoner.classification.entry.RelationshipChangeEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class ClassificationRunIndex extends SingleDirectoryIndexImpl {

	private final ObjectMapper objectMapper;

	public ClassificationRunIndex(final File directory) {
		super(directory, true);
		objectMapper = new ObjectMapper();
	}

	public List<IClassificationRun> getAllClassificationRuns(final StorageRef storageRef, final String userId) throws IOException {
		final Query query = Mappings.newQuery()
				.field("class", ClassificationRun.class.getSimpleName())
				.field("userId", userId)
				.field("branchPath", storageRef.getBranchPath())
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

	public void insertOrUpdateClassificationRun(final IBranchPath branchPath, final ClassificationRun classificationRun) throws IOException {

		final Document updatedDocument = Mappings.doc()
				.searchOnlyField("class", ClassificationRun.class.getSimpleName())
				.searchOnlyField("id", classificationRun.getId())
				.field("userId", classificationRun.getUserId())
				.field("branchPath", branchPath.getPath())
				.storedOnly("source", objectMapper.writer().writeValueAsString(classificationRun))
				.build();

		final Query query = Mappings.newQuery()
				.field("class", ClassificationRun.class.getSimpleName())
				.field("id", classificationRun.getId())
				.matchAll();

		writer.deleteDocuments(query);
		writer.addDocument(updatedDocument);
		commit();
	}

	public void updateClassificationRunStatus(final UUID id, final ClassificationStatus newStatus) throws IOException {
		updateClassificationRunStatusAndIndexChanges(id, newStatus, null);
	}

	public void updateClassificationRunStatusAndIndexChanges(final UUID id, final ClassificationStatus newStatus, final GetResultResponseChanges changes) throws IOException {

		final Document sourceDocument = getClassificationRunDocument(id);
		if (null == sourceDocument) {
			return;
		}

		final IBranchPath branchPath = BranchPathUtils.createPath(sourceDocument.get("branchPath"));
		final ClassificationRun classificationRun = objectMapper.reader(ClassificationRun.class).readValue(sourceDocument.get("source"));

		if (newStatus.equals(classificationRun.getStatus())) {
			return;
		}

		classificationRun.setStatus(newStatus);

		if (ClassificationStatus.COMPLETED.equals(newStatus)) {
			if(null == classificationRun.getCompletionDate()) {
				classificationRun.setCompletionDate(new Date());
			}
			checkNotNull(changes, "GetResultResponseChanges are required to update a completed classification.");
			final ClassificationIssueFlags issueFlags = indexChanges(sourceDocument, changes);
			classificationRun.setRedundantStatedRelationshipsFound(issueFlags.isRedundantStatedFound());
			classificationRun.setEquivalentConceptsFound(issueFlags.isEquivalentConceptsFound());
		}

		insertOrUpdateClassificationRun(branchPath, classificationRun);
	}

	private SnomedRelationshipIndexEntry getSnomedRelationshipIndexEntry(IBranchPath branchPath, RelationshipChangeEntry relationshipChange) {
		SnomedRelationshipIndexEntry foundRelationshipIndexEntry = null;
		final Long sourceId = relationshipChange.getSource().getId();
		final List<SnomedRelationshipIndexEntry> relationshipIndexEntries = getIndexService().search(branchPath, new SnomedRelationshipIndexQueryAdapter(sourceId.toString(), SnomedRelationshipIndexQueryAdapter.SEARCH_SOURCE_ID));
		for (SnomedRelationshipIndexEntry relationshipIndexEntry : relationshipIndexEntries) {
			if (relationshipIndexEntry.getValueId().equals(relationshipChange.getDestination().getId().toString())
				&& relationshipIndexEntry.getAttributeId().equals(relationshipChange.getType().getId().toString())
				&& relationshipIndexEntry.getGroup() == relationshipChange.getGroup()) {
				foundRelationshipIndexEntry = relationshipIndexEntry;
			}
		}
		return foundRelationshipIndexEntry;
	}

	public void deleteClassificationData(final UUID id) throws IOException {
		// Removes all documents, not just the classification run document
		writer.deleteDocuments(new Term("id", id.toString()));
		commit();
	}

	private ClassificationIssueFlags indexChanges(Document sourceDocument, final GetResultResponseChanges changes) throws IOException {
		final UUID id = changes.getClassificationId();
		final IBranchPath branchPath = BranchPathUtils.createPath(sourceDocument.get("branchPath"));
		final String userId = sourceDocument.get("userId");
		final ClassificationIssueFlags classificationIssueFlags = new ClassificationIssueFlags();

		final List<AbstractEquivalenceSet> equivalenceSets = changes.getEquivalenceSets();
		classificationIssueFlags.setEquivalentConceptsFound(!equivalenceSets.isEmpty());
		for (final AbstractEquivalenceSet equivalenceSet : equivalenceSets) {

			final List<IEquivalentConcept> convertedEquivalentConcepts = newArrayList();
			for (final SnomedConceptIndexEntry equivalentEntry : equivalenceSet.getConcepts()) {
				addEquivalentConcept(convertedEquivalentConcepts, equivalentEntry);
			}

			if (equivalenceSet instanceof EquivalenceSet) {
				addEquivalentConcept(convertedEquivalentConcepts, ((EquivalenceSet) equivalenceSet).getSuggestedConcept());
			}
			
			final EquivalentConceptSet convertedEquivalenceSet = new EquivalentConceptSet();
			convertedEquivalenceSet.setUnsatisfiable(equivalenceSet.isUnsatisfiable());
			convertedEquivalenceSet.setEquivalentConcepts(convertedEquivalentConcepts);

			indexResult(id, branchPath, userId, EquivalentConceptSet.class, equivalenceSet.getConcepts().get(0).getId(), convertedEquivalenceSet);
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
				final SnomedRelationshipIndexEntry snomedRelationshipIndexEntry = getSnomedRelationshipIndexEntry(branchPath, relationshipChange);
				characteristicTypeId = snomedRelationshipIndexEntry.getCharacteristicTypeId();
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

			indexResult(id, branchPath, userId, RelationshipChange.class, convertedRelationshipChange.getSourceId(), convertedRelationshipChange);
		}

		commit();
		return classificationIssueFlags;
	}

	private void addEquivalentConcept(final List<IEquivalentConcept> convertedEquivalentConcepts, final SnomedConceptIndexEntry equivalentEntry) {
		final EquivalentConcept convertedConcept = new EquivalentConcept();
		convertedConcept.setId(equivalentEntry.getId());
		convertedConcept.setLabel(equivalentEntry.getLabel());

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
		final RelationshipChangeList result = new RelationshipChangeList();

		result.setTotal(getHitCount(query));
		result.setChanges(this.<IRelationshipChange>search(query, RelationshipChange.class, offset, limit));

		return result;
	}

	private <T> void indexResult(final UUID id, final IBranchPath branchPath, final String userId,
			final Class<T> clazz, String componentId, final T value) throws IOException {

		final Document updateDocument = new Document();
		updateDocument.add(new StringField("class", clazz.getSimpleName(), Store.NO));
		updateDocument.add(new StringField("id", id.toString(), Store.NO));
		updateDocument.add(new StringField("userId", userId, Store.NO));
		updateDocument.add(new StringField("branchPath", branchPath.getPath(), Store.NO));
		updateDocument.add(new StringField("componentId", componentId, Store.NO));
		updateDocument.add(new StoredField("source", objectMapper.writer().writeValueAsString(value)));

		writer.addDocument(updateDocument);
	}

	private Document getClassificationRunDocument(final UUID id) throws IOException {
		final Query query = Mappings.newQuery()
				.field("class", ClassificationRun.class.getSimpleName())
				.field("id", id.toString())
				.matchAll();
		return Iterables.getFirst(search(query, 1), null);
	}

	private Query createClassQuery(final String className, final String classificationId, final StorageRef storageRef, String componentId, final String userId) {
		final QueryBuilder query = Mappings.newQuery()
				.field("class", className)
				.field("id", classificationId)
				.field("userId", userId)
				.field("branchPath", storageRef.getBranchPath());
		if (componentId != null) {
			query.field("componentId", componentId);
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
		IndexSearcher searcher = null;

		try {

			searcher = manager.acquire();

			final TopDocs docs = searcher.search(query, null, offset + limit, Sort.INDEXORDER, false, false);
			final ScoreDoc[] scoreDocs = docs.scoreDocs;
			final ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();

			for (int i = offset; i < offset + limit && i < scoreDocs.length; i++) {
				final Document sourceDocument = searcher.doc(scoreDocs[i].doc, ImmutableSet.of("source"));
				final String source = sourceDocument.get("source");
				final T deserializedSource = objectMapper.reader(sourceClass).readValue(source);
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
			final TopDocs docs = searcher.search(query, null, limit, Sort.INDEXORDER, false, false);
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
			final int expectedSize = searcher.getIndexReader().maxDoc();
			final DocIdCollector collector = DocIdCollector.create(expectedSize);

			searcher.search(query, collector);

			int totalHits = 0;
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			while (itr.next()) {
				totalHits++;
			}
			
			return totalHits;

		} finally {

			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}

	private static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
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
