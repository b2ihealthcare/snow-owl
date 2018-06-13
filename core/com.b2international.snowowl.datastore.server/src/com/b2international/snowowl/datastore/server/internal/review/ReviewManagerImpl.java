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
package com.b2international.snowowl.datastore.server.internal.review;

import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionCompare;
import com.b2international.index.revision.RevisionCompareDetail;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.internal.InternalRepository;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.review.ReviewStatus;
import com.b2international.snowowl.datastore.server.ReviewConfiguration;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

import io.reactivex.disposables.Disposable;

/**
 * @since 4.2
 */
public class ReviewManagerImpl implements ReviewManager {

	private final class CreateReviewJob extends Job {

		private final String reviewId;
		private final RevisionIndex index;
		private final String branchToCompare;
		private final String branchAsBase;

		private CreateReviewJob(final String reviewId, final RevisionIndex index, final String branchAsBase, final String branchToCompare) {
			super(MessageFormat.format("Creating review for branch ''{0}''", branchToCompare));
			this.reviewId = reviewId;
			this.index = index;
			this.branchAsBase = branchAsBase;
			this.branchToCompare = branchToCompare;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final RevisionCompare compare;
			if (branchAsBase != null) {
				compare = index.compare(branchAsBase, branchToCompare); 
			} else {
				compare = index.compare(branchToCompare);
			}
			createConceptChanges(reviewId, branchToCompare, compare);
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

	private final class CleanupTask extends TimerTask {
		@Override
		public void run() {
			final long now = System.currentTimeMillis();
			try {
				store.get().write(index -> {
					final Hits<Review> affectedReviews = index.searcher().search(Query.select(Review.class)
							.where(Expressions.builder()
									.should(buildQuery(ReviewStatus.FAILED, now - keepOtherMillis))
									.should(buildQuery(ReviewStatus.STALE, now - keepOtherMillis))
									.should(buildQuery(ReviewStatus.PENDING, now - keepOtherMillis))
									.should(buildQuery(ReviewStatus.CURRENT, now - keepCurrentMillis))
									.build())
							.limit(Integer.MAX_VALUE)
							.build());
					
					
					if (affectedReviews.getTotal() > 0) {
						final Set<String> ids = newHashSet();
						for (Review r : affectedReviews) {
							ids.add(r.id());
						}
					
						index.removeAll(ImmutableMap.of(
								Review.class, ids,
								ConceptChanges.class, ids
								));
						
						index.commit();
					}
					return null;
				});
			} catch (final Exception e) {
				LOG.error("Exception in review cleanup task when searching for outdated reviews.", e);
				return;
			}
		}

		private Expression buildQuery(ReviewStatus status, long beforeTimestamp) {
			return Expressions.builder()
					.filter(Expressions.exactMatch(Review.Fields.STATUS, status.toString()))
					.filter(Expressions.matchRange(Review.Fields.LAST_UPDATED, null, ISO8601Utils.format(new Date(beforeTimestamp))))
					.build();
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ReviewManagerImpl.class);

	private final Provider<Index> store;
	private final Provider<RevisionIndex> revisionIndex;
	private final IJobChangeListener jobChangeListener = new ReviewJobChangeListener();
	private final TimerTask cleanupTask = new CleanupTask();
	private final Disposable notificationSubscription;

	private static final long CLEANUP_INTERVAL = TimeUnit.MINUTES.toMillis(1L);

	private final long keepOtherMillis;
	private final long keepCurrentMillis;
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("Review cleanup", true);
	}

	public ReviewManagerImpl(final InternalRepository repository) {
		this(repository, new ReviewConfiguration());
	}

	public ReviewManagerImpl(final InternalRepository repository, final ReviewConfiguration config) {
		this.store = repository.provider(Index.class);
		this.revisionIndex = repository.provider(RevisionIndex.class);

		this.keepCurrentMillis = TimeUnit.MINUTES.toMillis(config.getKeepCurrentMins());
		this.keepOtherMillis = TimeUnit.MINUTES.toMillis(config.getKeepOtherMins());
		// Check every minute if there's something to remove
		Holder.CLEANUP_TIMER.schedule(cleanupTask, CLEANUP_INTERVAL, CLEANUP_INTERVAL);
		notificationSubscription = repository.notifications()
			.ofType(BranchChangedEvent.class)
			.subscribe(this::onBranchChange);
	}
	
	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			cleanupTask.cancel();
			notificationSubscription.dispose();
		}
	}
	
	/*Handles repository specific branch change events*/
	private void onBranchChange(BranchChangedEvent event) {
		final String path = event.getBranch();
		store.get().write(index -> {
			final Hits<Review> affectedReviews = index.searcher().search(
					Query.select(Review.class)
					.where(Expressions.builder()
							.should(Expressions.nestedMatch("source", Expressions.exactMatch("path", path)))
							.should(Expressions.nestedMatch("target", Expressions.exactMatch("path", path)))
							.build()
							)
					.limit(Integer.MAX_VALUE)
					.build());
			
			for (final Review affectedReview : affectedReviews) {
				Review newReview = updateStatus(affectedReview, ReviewStatus.STALE);
				if (newReview != null) {
					index.put(newReview.id(), newReview);
				}
			}
			index.commit();
			return null;
		});
	}
	
	@Override
	public Review createReview(final Branch source, final Branch target) {
		if (source.path().equals(target.path())) {
			throw new BadRequestException("Cannot create a review with the same source and target '%s'.", source.path());
		} else if (!source.parentPath().equals(target.path()) && !target.parentPath().equals(source.path())){
			throw new BadRequestException("Cannot create review for source '%s' and target '%s', because there is no relation between them.", source.path(), target.path());
		}

		// the compared branch is always the source branch
		final String branchToCompare = source.path();
		// if target is parent of source, then this is a merge review otherwise this is a rebase review
		final String branchAsBase = source.parentPath().equals(target.path()) ? null : target.path();
		
		final String reviewId = UUID.randomUUID().toString();
		final CreateReviewJob compareJob = new CreateReviewJob(reviewId, revisionIndex.get(), branchAsBase, branchToCompare);

		final Review review = Review.builder(reviewId.toString(), source, target).build();

		putReview(review);

		compareJob.addJobChangeListener(jobChangeListener);
		compareJob.schedule();
		return review;
	}

	void updateReviewStatus(final String id, final ReviewStatus newReviewStatus) {
		try {
			final Review oldReview = (Review) getReview(id);
			final Review newReview = updateStatus(oldReview, newReviewStatus);
			if (newReview != null) {
				putReview(newReview);
			}
		} catch (final NotFoundException ignored) {
			// No need to update if a review has been removed in the meantime 
		}
	}

	private Review updateStatus(Review current, ReviewStatus newStatus) {
		if (!ReviewStatus.STALE.equals(current.status())) {
			return Review.builder(current)
					.status(newStatus)
					.refreshLastUpdated()
					.build();
		} else {
			// XXX null means no change here
			return null;
		}
	}

	private void putReview(final Review newReview) {
		store.get().write(index -> {
			index.put(newReview.id(), newReview);
			index.commit();
			return null;
		});
	}

	void createConceptChanges(final String id, final String branch, final RevisionCompare compare) {
		final Set<String> newConcepts = newHashSet();
		final Set<String> changedConcepts = newHashSet();
		final Set<String> deletedConcepts = newHashSet();
		
		// register new ROOT objects first as new
		for (RevisionCompareDetail detail : compare.getDetails()) {
			if (detail.isAdd() && detail.isComponentChange() && detail.getObject().isRoot()) {
				newConcepts.add(detail.getComponent().id());
			}
		}
		
		// register new sub components as ROOT changes if the ROOT is not new
		for (RevisionCompareDetail detail : compare.getDetails()) {
			if (detail.isAdd() && detail.isComponentChange() && !detail.getObject().isRoot() && !newConcepts.contains(detail.getObject().id())) {
				changedConcepts.add(detail.getObject().id());
			}
		}
		
		// register all changes
		for (RevisionCompareDetail detail : compare.getDetails()) {
			if (detail.isChange()) {
				changedConcepts.add(detail.getObject().isRoot() ? detail.getComponent().id() : detail.getObject().id());
			}
		}

		// register removed ROOT objects first as deleted
		for (RevisionCompareDetail detail : compare.getDetails()) {
			if (detail.isRemove() && detail.isComponentChange() && detail.getObject().isRoot()) {
				deletedConcepts.add(detail.getComponent().id());
			}
		}
		
		// register deleted sub components as ROOT changes if the ROOT is not deleted
		for (RevisionCompareDetail detail : compare.getDetails()) {
			if (detail.isRemove() && detail.isComponentChange() && !detail.getObject().isRoot() && !deletedConcepts.contains(detail.getObject().id())) {
				changedConcepts.add(detail.getObject().id());
			}
		}
		
		final ConceptChanges convertedChanges = new ConceptChanges(id, newConcepts, changedConcepts, deletedConcepts);

		try {
			getReview(id);
			store.get().write(index -> {
				index.put(id, convertedChanges);
				index.commit();
				return null;
			});
		} catch (NotFoundException ignored) {
		}
	}

	@Override
	public Review getReview(final String id) {
		final Review review= store.get().read(index -> index.get(Review.class, id));

		if (review == null) {
			throw new NotFoundException(Review.class.getSimpleName(), id);
		} else {
			return review;
		}
	}

	@Override
	public ConceptChanges getConceptChanges(final String id) {
		final ConceptChanges conceptChanges = store.get().read(index -> index.get(ConceptChanges.class, id));

		if (conceptChanges == null) {
			throw new NotFoundException("Concept changes", id);
		} else {
			return conceptChanges;
		}
	}

	@Override
	public void delete(final String reviewId) {
		store.get().write(index -> {
			index.removeAll(ImmutableMap.of(
					Review.class, Collections.singleton(reviewId),
					ConceptChanges.class, Collections.singleton(reviewId)));
			index.commit();
			return null;
		});
	}

}
