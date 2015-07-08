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
import static com.b2international.snowowl.datastore.BranchPathUtils.convertIntoBasePath;
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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.review.ConceptChanges;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.datastore.server.review.ReviewManager;
import com.b2international.snowowl.datastore.server.review.ReviewStatus;
import com.b2international.snowowl.datastore.store.Store;
import com.b2international.snowowl.datastore.store.query.Query;
import com.b2international.snowowl.datastore.store.query.QueryBuilder;
import com.b2international.snowowl.datastore.version.VersionCompareService;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
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

	private final class SetStaleHandler implements CDOCommitInfoHandler {
		@Override
		@SuppressWarnings("restriction")
		public void handleCommitInfo(final CDOCommitInfo commitInfo) {
			if (commitInfo instanceof org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo) {
				return;
			}

			final String path = commitInfo.getBranch().getPathName();

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

			long now = System.currentTimeMillis();
			
			synchronized (reviewStore) {
				final Set<ReviewImpl> affectedReviews = ImmutableSet.<ReviewImpl>builder()
						.addAll(reviewStore.search(buildQuery(ReviewStatus.CURRENT, now - keepCurrentMillis)))
						.addAll(reviewStore.search(buildQuery(ReviewStatus.PENDING, now - keepStaleMillis)))
						.addAll(reviewStore.search(buildQuery(ReviewStatus.STALE, now - keepStaleMillis)))
						.addAll(reviewStore.search(buildQuery(ReviewStatus.FAILED, now - keepStaleMillis)))
						.build();

				for (final ReviewImpl affectedReview : affectedReviews) {
					deleteReview(affectedReview);
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

	private final String repositoryId;
	private final Store<ReviewImpl> reviewStore;
	private final Store<ConceptChangesImpl> conceptChangesStore;
	private final IJobChangeListener jobChangeListener = new ReviewJobChangeListener();
	private final SetStaleHandler commitInfoHandler = new SetStaleHandler();
	private final TimerTask cleanupTask = new CleanupTask();

	// Check every minute if there's something to remove
	private static final long CHECK_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1L);

	private final long keepStaleMillis;
	private final long keepCurrentMillis;

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("Review cleanup", true);
	}

	public ReviewManagerImpl(final ICDORepository repository, final Store<ReviewImpl> reviewStore, final Store<ConceptChangesImpl> conceptChangesStore) {
		this(repository, reviewStore, conceptChangesStore, 5 * CHECK_INTERVAL_MILLIS, 15 * CHECK_INTERVAL_MILLIS);
	}

	public ReviewManagerImpl(final ICDORepository repository, 
			final Store<ReviewImpl> reviewStore, final Store<ConceptChangesImpl> conceptChangesStore, 
			final long keepStaleMillis, final long keepCurrentMillis) {

		this.repositoryId = repository.getUuid();
		this.keepStaleMillis = keepStaleMillis;
		this.keepCurrentMillis = keepCurrentMillis;

		this.reviewStore = reviewStore;
		reviewStore.configureSearchable("status");
		reviewStore.configureSearchable("sourcePath");
		reviewStore.configureSearchable("targetPath");
		reviewStore.configureSearchable("lastUpdated");

		this.conceptChangesStore = conceptChangesStore;

		repository.getRepository().addCommitInfoHandler(commitInfoHandler);
		Holder.CLEANUP_TIMER.schedule(cleanupTask, CHECK_INTERVAL_MILLIS, CHECK_INTERVAL_MILLIS);
	}

	@Override
	public Review createReview(final String userId, final Branch source, final Branch target) {
		final IBranchPath headPath = true ? source.branchPath() : target.branchPath();
		final IBranchPath basePath = convertIntoBasePath(headPath);
		final VersionCompareConfiguration configuration = new VersionCompareConfiguration(repositoryId, basePath, headPath, false, true, false);

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
			final ReviewImpl oldReviewImpl = (ReviewImpl) getReview(id);
			if (!ReviewStatus.STALE.equals(oldReviewImpl.status())) {
				final ReviewImpl newReviewImpl = ReviewImpl.builder(oldReviewImpl).status(newReviewStatus).build();
				reviewStore.put(id, newReviewImpl);
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

		return ReviewImpl.builder((ReviewImpl) review).refreshLastUpdated().build();
	}
}
