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

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map.Entry;
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

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.compare.RevisionCompare;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.index.RevisionDocument.Views.IdOnly;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.review.ReviewStatus;
import com.b2international.snowowl.datastore.server.ReviewConfiguration;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.2
 */
public class ReviewManagerImpl implements ReviewManager {

	private static final String LAST_UPDATED_FIELD = "lastUpdated";
	private static final String TARGET_PATH_FIELD = "targetPath";
	private static final String SOURCE_PATH_FIELD = "sourcePath";
	private static final String STATUS_FIELD = "status";

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

	private final class StaleHandler implements IHandler<IMessage> {
		@Override
		public void handle(final IMessage message) {
			final BranchChangedEvent changeEvent = message.body(BranchChangedEvent.class);
			final String path = changeEvent.getBranch().path();

			store.write(new IndexWrite<Void>() {
				@Override
				public Void execute(Writer index) throws IOException {
					final Iterable<Review> affectedReviews = index.searcher().search(
							Query.select(Review.class)
							.where(Expressions.builder()
									.must(Expressions.exactMatch(SOURCE_PATH_FIELD, path))
									.must(Expressions.exactMatch(TARGET_PATH_FIELD, path))
									.build()
							).build());
					
					for (final Review affectedReview : affectedReviews) {
						Review newReview = updateStatus((ReviewImpl) affectedReview, ReviewStatus.STALE);
						if (newReview != null) {
							index.put(newReview.id(), newReview);
						}
					}
					index.commit();
					return null;
				}
			});
		}
	}

	private final class CleanupTask extends TimerTask {
		@Override
		public void run() {
			final long now = System.currentTimeMillis();
			try {
				store.write(new IndexWrite<Void>() {
					@Override
					public Void execute(Writer index) throws IOException {
						final Iterable<Review> affectedReviews = index.searcher().search(Query.select(Review.class)
								.where(Expressions.builder()
										.should(buildQuery(ReviewStatus.FAILED, now - keepOtherMillis))
										.should(buildQuery(ReviewStatus.STALE, now - keepOtherMillis))
										.should(buildQuery(ReviewStatus.PENDING, now - keepOtherMillis))
										.should(buildQuery(ReviewStatus.CURRENT, now - keepCurrentMillis))
										.build())
								.limit(Integer.MAX_VALUE)
								.build());
						
						final Set<String> ids = newHashSet();
						for (Review r : affectedReviews) {
							ids.add(r.id());
						}
						
						index.removeAll(ImmutableMap.of(
								Review.class, ids,
								ConceptChanges.class, ids
								));
						
						index.commit();
						return null;
					}
				});
			} catch (final Exception e) {
				LOG.error("Exception in review cleanup task when searching for outdated reviews.", e);
				return;
			}
		}

		private Expression buildQuery(ReviewStatus status, long beforeTimestamp) {
			return Expressions.builder()
					.must(Expressions.exactMatch(STATUS_FIELD, status.toString()))
					.must(Expressions.matchRange(LAST_UPDATED_FIELD, null, ISO8601Utils.format(new Date(beforeTimestamp))))
					.build();
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ReviewManagerImpl.class);

	private final Index store;
	private final RevisionIndex revisionIndex;
	private final IJobChangeListener jobChangeListener = new ReviewJobChangeListener();
	private final StaleHandler staleHandler = new StaleHandler();
	private final TimerTask cleanupTask = new CleanupTask();

	private static final long REFRESH_INTERVAL = TimeUnit.MINUTES.toMillis(1L);

	private final long keepOtherMillis;
	private final long keepCurrentMillis;

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("Review cleanup", true);
	}

	public ReviewManagerImpl(final InternalRepository repository) {
		this(repository, new ReviewConfiguration());
	}

	public ReviewManagerImpl(final InternalRepository repository, final ReviewConfiguration config) {
		this.store = repository.getIndex();
		this.revisionIndex = repository.getRevisionIndex();

		this.keepCurrentMillis = TimeUnit.MINUTES.toMillis(config.getKeepCurrentMins());
		this.keepOtherMillis = TimeUnit.MINUTES.toMillis(config.getKeepOtherMins());
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

		final String branchToCompare;
		final String branchAsBase;
		if (source.parent().equals(target)) {
			/* 
			 * target is the parent of source 
			 * source is the child of target
			 * review is for changes on child (source) that will be made visible on parent (target) by merging
			 * 
			 * Comparison starts from the base of the child (source) branch.
			 */
			branchAsBase = null;
			branchToCompare = source.path();
		} else if (target.parent().equals(source)) {
			/* 
			 * source is the parent of target
			 * target is the child of source
			 * review is for changes on parent (source) that will be made visible on child (target) by rebasing
			 */
			if (target.state(source) == Branch.BranchState.STALE) {
				throw new UnsupportedOperationException("Not implemented this compare yet");
			} else {
				branchAsBase = target.path();
				branchToCompare = source.path();
			}
		} else {
			throw new BadRequestException("Cannot create review for source '%s' and target '%s', because there is no relation between them.", source.path(), target.path());
		}
		
//		// Comparison ends with the head commit of the source branch, but we'll have to figure out where to retrieve the starting commit from.
//		final VersionCompareConfiguration.Builder configurationBuilder = VersionCompareConfiguration.builder(repositoryId, false).target(source.branchPath(), false);
//		
//		if (source.parent().equals(target)) { 
//
//			configurationBuilder.source(BranchPathUtils.convertIntoBasePath(source.branchPath()), false);
//
//		} else if (target.parent().equals(source)) {
//
//			if (target.state(source) == BranchState.STALE) {
//				// Start from the parent (source) base _as seen from the child (target) branch_, if parent (source) itself has been rebased in the meantime 
//				configurationBuilder.source(BranchPathUtils.convertIntoBasePath(source.branchPath(), target.branchPath()), false);
//			} else {
//				// Start from the child (target) base
//				configurationBuilder.source(BranchPathUtils.convertIntoBasePath(target.branchPath()), false);
//			}
//
//		} else {
//			throw new BadRequestException("Cannot create review for source '%s' and target '%s', because there is no relation between them.", source.path(), target.path());
//		}
//		
//		final VersionCompareConfiguration configuration = configurationBuilder.build();
		final String reviewId = UUID.randomUUID().toString();
		final CreateReviewJob compareJob = new CreateReviewJob(reviewId, revisionIndex, branchAsBase, branchToCompare);

		final ReviewImpl review = ReviewImpl.builder(reviewId.toString(), source, target).build();
		review.setReviewManager(this);

		putReview(review);

		compareJob.addJobChangeListener(jobChangeListener);
		compareJob.schedule();
		return review;
	}

	void updateReviewStatus(final String id, final ReviewStatus newReviewStatus) {
		try {
			final ReviewImpl oldReview = (ReviewImpl) getReview(id);
			final Review newReview = updateStatus(oldReview, newReviewStatus);
			if (newReview != null) {
				putReview(newReview);
			}
		} catch (final NotFoundException ignored) {
			// No need to update if a review has been removed in the meantime 
		}
	}

	private Review updateStatus(ReviewImpl current, ReviewStatus newStatus) {
		if (!ReviewStatus.STALE.equals(current.status())) {
			return ReviewImpl.builder(current)
					.status(newStatus)
					.refreshLastUpdated()
					.build();
		} else {
			// XXX null means no change here
			return null;
		}
	}

	private void putReview(final Review newReview) {
		store.write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.put(newReview.id(), newReview);
				index.commit();
				return null;
			}
		});
	}

	void createConceptChanges(final String id, final String branch, final RevisionCompare compare) {
		// TODO concept changes is mutable
		// TODO non concept changes should be converted to concept changes for API compatibility
		// TODO add deleted IDs???
		final ConceptChangesImpl convertedChanges = revisionIndex.read(branch, new RevisionIndexRead<ConceptChangesImpl>() {
			@Override
			public ConceptChangesImpl execute(RevisionSearcher searcher) throws IOException {
				final Set<String> newConcepts = newHashSet();
				final Set<String> changedConcepts = newHashSet();
				final Set<String> deletedConcepts = newHashSet();
				
				for (final Entry<Class<? extends Revision>, LongSet> newComponents : compare.getNewComponents().entrySet()) {
					final Hits<IdOnly> ids = searcher.search(Query.selectPartial(RevisionDocument.Views.IdOnly.class, newComponents.getKey())
							.where(Expressions.matchAnyLong(Revision.STORAGE_KEY, LongSets.toSet(newComponents.getValue())))
							.build());
					FluentIterable.from(ids).transform(ComponentUtils.<String>getIdFunction()).copyInto(newConcepts);
				}
				
				for (final Entry<Class<? extends Revision>, LongSet> changedComponents : compare.getChangedComponents().entrySet()) {
					final Hits<IdOnly> ids = searcher.search(Query.selectPartial(RevisionDocument.Views.IdOnly.class, changedComponents.getKey())
							.where(Expressions.matchAnyLong(Revision.STORAGE_KEY, LongSets.toSet(changedComponents.getValue())))
							.build());
					FluentIterable.from(ids).transform(ComponentUtils.<String>getIdFunction()).copyInto(changedConcepts);
				}
				
				return new ConceptChangesImpl(id, newConcepts, changedConcepts, deletedConcepts);
			}
		});
		
		
		try {
			getReview(id);
			store.write(new IndexWrite<Void>() {
				@Override
				public Void execute(Writer index) throws IOException {
					index.put(id, convertedChanges);
					index.commit();
					return null;
				}
			});
		} catch (NotFoundException ignored) {
		}
	}

	@Override
	public Review getReview(final String id) {
		final Review review= store.read(new IndexRead<Review>() {
			@Override
			public Review execute(Searcher index) throws IOException {
				return index.get(Review.class, id);
			}
		});

		if (review == null) {
			throw new NotFoundException(Review.class.getSimpleName(), id);
		} else {
			((ReviewImpl) review).setReviewManager(this);
			return review;
		}
	}

	@Override
	public ConceptChanges getConceptChanges(final String id) {
		final ConceptChanges conceptChanges = store.read(new IndexRead<ConceptChanges>() {
			@Override
			public ConceptChanges execute(Searcher index) throws IOException {
				return index.get(ConceptChanges.class, id);
			}
		});

		if (conceptChanges == null) {
			throw new NotFoundException("Concept changes", id);
		} else {
			return conceptChanges;
		}
	}

	Review deleteReview(final Review review) {
		return store.write(new IndexWrite<Review>() {
			@Override
			public Review execute(Writer index) throws IOException {
				index.removeAll(ImmutableMap.of(
						Review.class, Collections.singleton(review.id()),
						ConceptChanges.class, Collections.singleton(review.id())));
				index.commit();
				return review;
			}
		});
	}

}
