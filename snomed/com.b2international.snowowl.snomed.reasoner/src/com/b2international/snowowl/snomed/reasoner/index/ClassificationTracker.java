/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.index;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.BulkDelete;
import com.b2international.index.BulkUpdate;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.core.taxonomy.InternalSctIdMultimap;
import com.b2international.snowowl.snomed.core.taxonomy.InternalSctIdSet;
import com.b2international.snowowl.snomed.core.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.diff.concretedomain.ConcreteDomainWriter;
import com.b2international.snowowl.snomed.reasoner.diff.relationship.RelationshipWriter;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.normalform.NormalFormGenerator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 7.0
 */
public final class ClassificationTracker implements IDisposableService {

	private static final Logger LOG = LoggerFactory.getLogger("classification");

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("Classification task cleanup", true);
	}

	private final class CleanUpTask extends TimerTask {

		@Override
		public void run() {
			try {
				index.write(writer -> {

					final Query<String> query = Query.select(String.class)
							.from(ClassificationTaskDocument.class)
							.fields(ClassificationTaskDocument.Fields.ID)
							.where(Expressions.builder()
									.filter(ClassificationTaskDocument.Expressions.deleted(true))
									.build())
							.limit(Integer.MAX_VALUE)
							.build();

					deleteClassifications(writer, query);
					writer.commit();
					return null;
				});

			} catch (final IllegalStateException e) {
				cancel();
			}
		}
	}

	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final Index index;
	private final CleanUpTask cleanUp;

	public ClassificationTracker(final Index index, final int maximumReasonerRuns, final long cleanUpInterval) {
		this.index = index;

		this.index.write(writer -> {
			// Set classification statuses where a process was interrupted by a shutdown to FAILED
			updateTasksByStatus(writer, ImmutableSet.of(ClassificationStatus.RUNNING, ClassificationStatus.SCHEDULED), 
					ClassificationTaskDocument.Scripts.FAILED,
					ImmutableMap.of("completionDate", System.currentTimeMillis()));

			updateTasksByStatus(writer, ImmutableSet.of(ClassificationStatus.SAVING_IN_PROGRESS), 
					ClassificationTaskDocument.Scripts.SAVE_FAILED,
					ImmutableMap.of());

			// Trim the list 
			final Query<ClassificationTaskDocument> firstNQuery = Query.select(ClassificationTaskDocument.class)
					.where(Expressions.builder()
							.filter(ClassificationTaskDocument.Expressions.deleted(false))
							.build())
					.sortBy(SortBy.builder()
							.sortByField(ClassificationTaskDocument.Fields.CREATION_DATE, Order.DESC)
							.sortByField(ClassificationTaskDocument.Fields.ID, Order.ASC)
							.build())
					.limit(maximumReasonerRuns)
					.build();

			final Hits<ClassificationTaskDocument> firstNTasks = writer.searcher()
					.search(firstNQuery);

			if (firstNTasks.getTotal() > maximumReasonerRuns) {
				final ClassificationTaskDocument firstToRemove = Iterables.getLast(firstNTasks);
				final long endDate = firstToRemove.getCreationDate().getTime();

				final Query<String> deleteQuery = Query.select(String.class)
						.from(ClassificationTaskDocument.class)
						.where(ClassificationTaskDocument.Expressions.created(0L, endDate))
						.limit(Integer.MAX_VALUE)
						.build();

				deleteClassifications(writer, deleteQuery);
			}

			writer.commit();
			return null;
		});

		this.cleanUp = new CleanUpTask();
		Holder.CLEANUP_TIMER.schedule(cleanUp, cleanUpInterval, cleanUpInterval);
	}

	private void updateTasksByStatus(final Writer writer, 
			final Set<ClassificationStatus> statuses,
			final String script,
			final Map<String, Object> scriptArgs) {

		writer.bulkUpdate(new BulkUpdate<>(
				ClassificationTaskDocument.class, 
				ClassificationTaskDocument.Expressions.statuses(statuses), 
				ClassificationTaskDocument.Fields.ID, 
				script,
				scriptArgs));
	}

	private void deleteClassifications(final Writer writer, final Query<String> query) throws IOException {
		final Hits<String> deletedIds = writer.searcher()
				.search(query);

		if (deletedIds.getTotal() > 0) {
			LOG.trace("Purging classification tasks {}", deletedIds);

			writer.bulkDelete(new BulkDelete<>(ClassificationTaskDocument.class, ClassificationTaskDocument.Expressions.ids(deletedIds)));
			writer.bulkDelete(new BulkDelete<>(EquivalentConceptSetDocument.class, EquivalentConceptSetDocument.Expressions.classificationId(deletedIds)));
			writer.bulkDelete(new BulkDelete<>(ConcreteDomainChangeDocument.class, ConcreteDomainChangeDocument.Expressions.classificationId(deletedIds)));
			writer.bulkDelete(new BulkDelete<>(RelationshipChangeDocument.class, RelationshipChangeDocument.Expressions.classificationId(deletedIds)));
		}
	}

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			this.cleanUp.cancel();
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}

	public void classificationScheduled(final String classificationId, final String reasonerId, final String userId, final String branch) {
		index.write(writer -> {
			final ClassificationTaskDocument classificationRun = ClassificationTaskDocument.builder()
					.id(classificationId)
					.reasonerId(reasonerId)
					.userId(userId)
					.branch(branch)
					.creationDate(Dates.todayGmt())
					.status(ClassificationStatus.SCHEDULED)
					.build();

			writer.put(classificationId, classificationRun);
			writer.commit();
			return null;
		});
	}

	public void classificationRunning(final String classificationId, final long timestamp) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(
					ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.RUNNING, 
					ImmutableMap.of("timestamp", timestamp)));
			writer.commit();
			return null;
		});
	}

	public void classificationCompleted(final String classificationId, final ReasonerTaxonomy inferredTaxonomy) {
		index.write(writer -> {

			indexUnsatisfiableConcepts(writer, classificationId, inferredTaxonomy.getUnsatisfiableConcepts());
			indexEquivalentConcepts(writer, classificationId, inferredTaxonomy.getEquivalentConcepts());

			final NormalFormGenerator normalFormGenerator = new NormalFormGenerator(inferredTaxonomy);
			final RelationshipWriter relationshipWriter = new RelationshipWriter(classificationId, writer);
			final ConcreteDomainWriter concreteDomainWriter = new ConcreteDomainWriter(classificationId, writer);

			normalFormGenerator.computeChanges(null, relationshipWriter, concreteDomainWriter);

			final boolean hasEquivalentConcepts = !inferredTaxonomy.getUnsatisfiableConcepts().isEmpty()
					|| !inferredTaxonomy.getEquivalentConcepts().isEmpty();
			final boolean hasInferredChanges = relationshipWriter.hasInferredChanges()
					|| concreteDomainWriter.hasInferredChanges();
			final boolean hasRedundantStatedChanges = relationshipWriter.hasRedundantStatedChanges();
			
			writer.bulkUpdate(new BulkUpdate<>(ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.COMPLETED, 
					ImmutableMap.of("completionDate", System.currentTimeMillis(),
							"hasEquivalentConcepts", hasEquivalentConcepts,
							"hasInferredChanges", hasInferredChanges,
							"hasRedundantStatedChanges", hasRedundantStatedChanges)));

			writer.commit();
			return null;
		});
	}
	
	public void classificationSaving(final String classificationId) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(
					ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.SAVING_IN_PROGRESS, 
					ImmutableMap.of()));
			writer.commit();
			return null;
		});
	}

	public void classificationSaved(final String classificationId, final long commitTimestamp) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(
					ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.SAVED, 
					ImmutableMap.of("saveDate", commitTimestamp)));
			writer.commit();
			return null;
		});
	}

	public void classificationSaveFailed(final String classificationId) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(
					ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.SAVE_FAILED, 
					ImmutableMap.of()));
			writer.commit();
			return null;
		});
	}

	private void indexUnsatisfiableConcepts(final Writer writer, 
			final String classificationId, 
			final InternalSctIdSet unsatisfiableConcepts) {

		if (!unsatisfiableConcepts.isEmpty()) {
			final EquivalentConceptSetDocument equivalentDoc = EquivalentConceptSetDocument.builder()
					.classificationId(classificationId)
					.conceptIds(unsatisfiableConcepts.toLongList())
					.unsatisfiable(true)
					.build();
	
			writer.put(UUID.randomUUID().toString(), equivalentDoc);
		}
	}

	private void indexEquivalentConcepts(final Writer writer, 
			final String classificationId, 
			final InternalSctIdMultimap equivalentConcepts) {

		for (final LongIterator itr = equivalentConcepts.keySet().iterator(); itr.hasNext(); /*empty*/) {
			final long representativeConcept = itr.next();
			final LongSet equivalents = equivalentConcepts.get(representativeConcept);
			final LongList orderedConcepts = PrimitiveLists.newLongArrayListWithExpectedSize(equivalents.size() + 1);

			orderedConcepts.add(representativeConcept);
			orderedConcepts.addAll(equivalents);

			final EquivalentConceptSetDocument equivalentDoc = EquivalentConceptSetDocument.builder()
					.classificationId(classificationId)
					.conceptIds(orderedConcepts)
					.unsatisfiable(false)
					.build();

			writer.put(UUID.randomUUID().toString(), equivalentDoc);			
		}
	}

	public void classificationFailed(final String classificationId) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.FAILED, 
					ImmutableMap.of("completionDate", System.currentTimeMillis())));
			writer.commit();
			return null;
		});
	}

	public void classificationDeleted(final String classificationId) {
		index.write(writer -> {
			// Check if the classification exists first
			getClassificationChecked(writer.searcher(), classificationId);

			// Set a soft deletion flag only -- the request should be ideally quick to complete
			writer.bulkUpdate(new BulkUpdate<>(
					ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.Scripts.DELETED));

			writer.commit();
			return null;
		});
	}

	private ClassificationTaskDocument getClassificationChecked(final Searcher searcher, final String classificationId) {
		try {

			final ClassificationTaskDocument document = searcher.get(ClassificationTaskDocument.class, classificationId);
			if (document == null || document.isDeleted()) {
				throw new NotFoundException("Classification task", classificationId);
			}
			return document;

		} catch (final IOException e) {
			throw new FormattedRuntimeException("Failed to retrieve classification document for ID '%s'.", classificationId, e);
		}
	}
}
