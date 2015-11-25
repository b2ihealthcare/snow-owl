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
package com.b2international.snowowl.datastore.server.internal.review;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branch.BranchState;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.review.ReviewStatus;
import com.b2international.snowowl.datastore.server.ReviewConfiguration;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;
import com.b2international.snowowl.datastore.store.Store;
import com.b2international.snowowl.datastore.store.query.Query;
import com.b2international.snowowl.datastore.store.query.QueryBuilder;
import com.b2international.snowowl.datastore.version.VersionCompareService;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.2
 */
public class ReviewManagerImpl implements ReviewManager {

	private final class CreateReviewJob extends Job {

		private final String reviewId;
		private final VersionCompareConfiguration configuration;

		private CreateReviewJob(final String reviewId, final VersionCompareConfiguration configuration) {
			super(MessageFormat.format("Creating review for branch ''{0}''", configuration.getTargetPath().getPath()));
			this.reviewId = reviewId;
			this.configuration = configuration;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final CompareResult compare = getServiceForClass(VersionCompareService.class).compare(configuration, monitor);
			createConceptChanges(reviewId, compare);
			return Status.OK_STATUS;
		}

		public String getReviewId() {
			return reviewId;
		}
	}

	private final class ReviewJobChangeListener extends JobChangeAdapter {
		@Override
		public void done(IJobChangeEvent event) {
			final String id = ((CreateReviewJob) event.getJob()).getReviewId();
			if (event.getResult().isOK()) {
				updateReviewStatus(id.toString(), ReviewStatus.CURRENT);				
			} else {
				updateReviewStatus(id.toString(), ReviewStatus.FAILED);				
			}
		}
	}

	private final class StaleHandler implements IHandler<IMessage> {
		@Override
		public void handle(final IMessage message) {
			final BranchChangedEvent changeEvent = message.body(BranchChangedEvent.class);
			final String path = changeEvent.getBranch().path();

			synchronized (reviewStore) {
				final Set<ReviewImpl> affectedReviews = ImmutableSet.<ReviewImpl>builder()
						.addAll(reviewStore.search(QueryBuilder.newQuery().match("sourcePath", path).build()))
						.addAll(reviewStore.search(QueryBuilder.newQuery().match("targetPath", path).build()))
						.build();

				for (final ReviewImpl affectedReview : affectedReviews) {
					updateReviewStatus(affectedReview.id(), ReviewStatus.STALE);
				}
			}
		}
	}

	private final class CleanupTask extends TimerTask {
		@Override
		public void run() {
			final long now = System.currentTimeMillis();

			synchronized (reviewStore) {
				final Set<ReviewImpl> affectedReviews;
				try {

					affectedReviews = ImmutableSet.<ReviewImpl>builder()
							.addAll(reviewStore.search(buildQuery(ReviewStatus.CURRENT, now - keepCurrentMillis)))
							.addAll(reviewStore.search(buildQuery(ReviewStatus.PENDING, now - keepOtherMillis)))
							.addAll(reviewStore.search(buildQuery(ReviewStatus.STALE, now - keepOtherMillis)))
							.addAll(reviewStore.search(buildQuery(ReviewStatus.FAILED, now - keepOtherMillis)))
							.build();

				} catch (final Exception e) {
					LOG.error("Exception in review cleanup task when searching for outdated reviews.", e);
					return;
				}

				for (final ReviewImpl affectedReview : affectedReviews) {
					try {
						deleteReview(affectedReview);
					} catch (final Exception e) {
						LOG.error("Exception in review cleanup task when deleting review {}.", affectedReview.id(), e);
					}
				}
			}
		}

		private Query buildQuery(ReviewStatus status, long beforeTimestamp) {
			return QueryBuilder.newQuery()
					.match("status", status.toString())
					.lessThan("lastUpdated", ISO8601Utils.format(new Date(beforeTimestamp)))
					.build();
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ReviewManagerImpl.class);

	private final String repositoryId;
	private final Store<ReviewImpl> reviewStore;
	private final Store<ConceptChangesImpl> conceptChangesStore;
	private final IJobChangeListener jobChangeListener = new ReviewJobChangeListener();
	private final StaleHandler staleHandler = new StaleHandler();
	private final TimerTask cleanupTask = new CleanupTask();

	private static final long REFRESH_INTERVAL = TimeUnit.MINUTES.toMillis(1L);

	private final long keepOtherMillis;
	private final long keepCurrentMillis;

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("Review cleanup", true);
	}

	public ReviewManagerImpl(final InternalRepository repository, final Store<ReviewImpl> reviewStore, final Store<ConceptChangesImpl> conceptChangesStore) {
		this(repository, reviewStore, conceptChangesStore, new ReviewConfiguration());
	}

	public ReviewManagerImpl(final InternalRepository repository, 
			final Store<ReviewImpl> reviewStore, final Store<ConceptChangesImpl> conceptChangesStore, 
			final ReviewConfiguration config) {

		this.repositoryId = repository.id();
		this.keepCurrentMillis = TimeUnit.MINUTES.toMillis(config.getKeepCurrentMins());
		this.keepOtherMillis = TimeUnit.MINUTES.toMillis(config.getKeepOtherMins());

		this.reviewStore = reviewStore;
		reviewStore.configureSearchable("status");
		reviewStore.configureSearchable("sourcePath");
		reviewStore.configureSearchable("targetPath");
		reviewStore.configureSearchable("lastUpdated");

		this.conceptChangesStore = conceptChangesStore;

		// Check every minute if there's something to remove
		Holder.CLEANUP_TIMER.schedule(cleanupTask, REFRESH_INTERVAL, REFRESH_INTERVAL);
	}
	
	public IHandler<IMessage> getStaleHandler() {
		return staleHandler;
	}

	@Override
	public Review createReview(final Branch source, final Branch target) {
		if (source.path().equals(target.path())) {
			throw new BadRequestException("Cannot create a review with the same source and target '%s'.", source.path());
		}

		// Comparison ends with the head commit of the source branch, but we'll have to figure out where to retrieve the starting commit from.
		final VersionCompareConfiguration.Builder configurationBuilder = VersionCompareConfiguration.builder(repositoryId, false).target(source.branchPath(), false);
		
		if (source.parent().equals(target)) { 

			/* 
			 * target is the parent of source 
			 * source is the child of target
			 * review is for changes on child (source) that will be made visible on parent (target) by merging
			 * 
			 * Comparison starts from the base of the child (source) branch.
			 */
			configurationBuilder.source(BranchPathUtils.convertIntoBasePath(source.branchPath()), false);

		} else if (target.parent().equals(source)) {

			/* 
			 * source is the parent of target
			 * target is the child of source
			 * review is for changes on parent (source) that will be made visible on child (target) by rebasing
			 */
			if (target.state(source) == BranchState.STALE) {
				// Start from the parent (source) base _as seen from the child (target) branch_, if parent (source) itself has been rebased in the meantime 
				configurationBuilder.source(BranchPathUtils.convertIntoBasePath(source.branchPath(), target.branchPath()), false);
			} else {
				// Start from the child (target) base
				configurationBuilder.source(BranchPathUtils.convertIntoBasePath(target.branchPath()), false);
			}

		} else {
			throw new BadRequestException("Cannot create review for source '%s' and target '%s', because there is no relation between them.", source.path(), target.path());
		}
		
		final VersionCompareConfiguration configuration = configurationBuilder.build();
		final String reviewId = UUID.randomUUID().toString();
		final CreateReviewJob compareJob = new CreateReviewJob(reviewId, configuration);

		final ReviewImpl review = ReviewImpl.builder(reviewId.toString(), source, target).build();
		review.setReviewManager(this);

		synchronized (reviewStore) {
			reviewStore.put(reviewId.toString(), review);
		}

		compareJob.addJobChangeListener(jobChangeListener);
		compareJob.schedule();
		return review;
	}

	void updateReviewStatus(final String id, final ReviewStatus newReviewStatus) {
		synchronized (reviewStore) {
			try {
				final ReviewImpl oldReviewImpl = (ReviewImpl) getReview(id);
				if (!ReviewStatus.STALE.equals(oldReviewImpl.status())) {
					final ReviewImpl newReviewImpl = ReviewImpl.builder(oldReviewImpl)
							.status(newReviewStatus)
							.refreshLastUpdated()
							.build();

					reviewStore.put(id, newReviewImpl);
				}
			} catch (final NotFoundException ignored) {
				// No need to update if a review has been removed in the meantime 
			}
		}
	}

	void createConceptChanges(final String id, final CompareResult compare) {
		final Set<String> newConcepts = newHashSet();
		final Set<String> changedConcepts = newHashSet();
		final Set<String> deletedConcepts = newHashSet();

		for (final NodeDiff diff : compare) {
			switch (diff.getChange()) {
				case ADDED:
					newConcepts.add(diff.getId());
					break;
				case DELETED:
					deletedConcepts.add(diff.getId());
					break;
				case UNCHANGED:
					// Nothing to do
					break;
				case UPDATED:
					changedConcepts.add(diff.getId());
					break;
				default:
					throw new IllegalStateException(MessageFormat.format("Unexpected change kind ''{0}''.", diff.getChange()));
			}
		}

		final ConceptChangesImpl convertedChanges = new ConceptChangesImpl(id, newConcepts, changedConcepts, deletedConcepts);

		synchronized (reviewStore) {
			if (reviewStore.containsKey(id)) {
				synchronized (conceptChangesStore) {
					conceptChangesStore.put(id, convertedChanges);
				}
			}
		}
	}

	@Override
	public Review getReview(final String id) {
		final ReviewImpl review;
		synchronized (reviewStore) {
			review = reviewStore.get(id);
		}

		if (review == null) {
			throw new NotFoundException(Review.class.getSimpleName(), id);
		} else {
			review.setReviewManager(this);
			return review;
		}
	}

	@Override
	public ConceptChanges getConceptChanges(final String id) {
		final ConceptChangesImpl conceptChanges;
		synchronized (conceptChangesStore) {
			conceptChanges = conceptChangesStore.get(id);
		}

		if (conceptChanges == null) {
			throw new NotFoundException("Concept changes", id);
		} else {
			return conceptChanges;
		}
	}

	Review deleteReview(final Review review) {
		synchronized (reviewStore) {
			synchronized (conceptChangesStore) {
				reviewStore.remove(review.id());
				conceptChangesStore.remove(review.id());
			}
		}

		return review;
	}
}
