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
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.remotejobs.*;
import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.remotejobs.AbstractRemoteJob;
import com.b2international.snowowl.datastore.server.review.ConceptChanges;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.datastore.server.review.ReviewManager;
import com.b2international.snowowl.datastore.server.review.ReviewStatus;
import com.b2international.snowowl.datastore.store.Store;
import com.b2international.snowowl.datastore.store.query.QueryBuilder;
import com.b2international.snowowl.datastore.version.VersionCompareService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
public class ReviewManagerImpl implements ReviewManager {

	private final class CreateReviewRemoteJob extends AbstractRemoteJob {

		private final VersionCompareConfiguration configuration;

		private CreateReviewRemoteJob(final VersionCompareConfiguration configuration) {
			super(MessageFormat.format("Creating review for branch ''{0}''", configuration.getTargetPath().getPath()));
			this.configuration = configuration;
		}

		@Override
		protected IStatus runWithListenableMonitor(final IProgressMonitor monitor) {
			final CompareResult compare = getServiceForClass(VersionCompareService.class).compare(configuration, monitor);
			final UUID reviewJobId = RemoteJobUtils.getRemoteJobId(this);
			createConceptChanges(reviewJobId.toString(), compare);
			return Status.OK_STATUS;
		}
	}

	private final class RemoteJobChangeHandler implements IHandler<IMessage> {

		@Override
		public void handle(final IMessage message) {
			new RemoteJobEventSwitch() {

				@Override
				protected void caseChanged(final RemoteJobChangedEvent event) {

					if (RemoteJobEntry.PROP_STATE.equals(event.getPropertyName())) {
						final RemoteJobState newState = (RemoteJobState) event.getNewValue();
						final UUID id = event.getId();

						switch (newState) {
							case CANCEL_REQUESTED:
								// Nothing to do
								break;
							case FAILED:
								updateReviewStatus(id.toString(), ReviewStatus.FAILED);
								break;
							case FINISHED: 
								updateReviewStatus(id.toString(), ReviewStatus.CURRENT);
								break;
							case RUNNING:
								// Nothing to do
								break;
							case SCHEDULED:
								// Nothing to do
								break;
							default:
								throw new IllegalStateException(MessageFormat.format("Unexpected remote job state ''{0}''.", newState));
						}
					}
				}

				@Override
				protected void caseRemoved(final RemoteJobRemovedEvent event) {

					try {
						getReview(event.getId().toString()).delete();
					} catch (final NotFoundException e) {
						return;
					}					
				}

			}.doSwitch(message.body(AbstractRemoteJobEvent.class));
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
			final Set<ReviewImpl> affectedReviews = ImmutableSet.<ReviewImpl>builder()
					.addAll(reviewStore.search(QueryBuilder.newQuery().match("sourcePath", path).build()))
					.addAll(reviewStore.search(QueryBuilder.newQuery().match("targetCdoBranchId", path).build()))
					.build();

			for (final ReviewImpl affectedReview : affectedReviews) {
				updateReviewStatus(affectedReview.id(), ReviewStatus.STALE);
			}
		}
	}

	private final Store<ReviewImpl> reviewStore;
	private final Store<ConceptChangesImpl> conceptChangesStore;
	private final IHandler<IMessage> remoteJobChangeHandler = new RemoteJobChangeHandler();
	private final SetStaleHandler commitInfoHandler = new SetStaleHandler();

	public ReviewManagerImpl(final ICDORepository repository, final Store<ReviewImpl> reviewStore, final Store<ConceptChangesImpl> conceptChangesStore) {
		this.reviewStore = reviewStore;
		this.conceptChangesStore = conceptChangesStore;
		reviewStore.configureSearchable("sourcePath");
		reviewStore.configureSearchable("targetPath");

		getServiceForClass(IEventBus.class).registerHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, remoteJobChangeHandler);
		repository.getRepository().addCommitInfoHandler(commitInfoHandler);
	}

	@Override
	public Review createReview(final String userId, final Branch source, final Branch target) {
		final UUID reviewId = UUID.randomUUID();

		final IBranchPath headPath = true ? source.branchPath() : target.branchPath();
		final IBranchPath basePath = convertIntoBasePath(headPath);
		final VersionCompareConfiguration configuration = new VersionCompareConfiguration("snomedStore", basePath, headPath, false, true, false);

		final CreateReviewRemoteJob compareJob = new CreateReviewRemoteJob(configuration);
		RemoteJobUtils.configureProperties(compareJob, userId, null, reviewId);

		final ReviewImpl review = ReviewImpl.builder(reviewId.toString(), source, target).build();
		review.setReviewManager(this);
		reviewStore.put(reviewId.toString(), review);

		return review;
	}

	void updateReviewStatus(final String id, final ReviewStatus newReviewStatus) {
		// FIXME: check if replace succeeded while spinning, similar to how classes work?
		final ReviewImpl oldReviewImpl = (ReviewImpl) getReview(id);
		if (!ReviewStatus.STALE.equals(oldReviewImpl.status())) {
			final ReviewImpl newReviewImpl = ReviewImpl.builder(oldReviewImpl).status(newReviewStatus).build();
			reviewStore.replace(id, oldReviewImpl, newReviewImpl);
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
		conceptChangesStore.put(id, convertedChanges);
	}

	@Override
	public Review getReview(final String id) {
		final ReviewImpl review = reviewStore.get(id);

		if (review == null) {
			throw new NotFoundException(Review.class.getSimpleName(), id);
		} else {
			review.setReviewManager(this);
			return review;
		}
	}

	@Override
	public ConceptChanges getConceptChanges(final String id) {
		final ConceptChangesImpl conceptChanges = conceptChangesStore.get(id);

		if (conceptChanges == null) {
			throw new NotFoundException(ConceptChanges.class.getSimpleName(), id);
		} else {
			return conceptChanges;
		}
	}

	Review deleteReview(final Review review) {
		reviewStore.remove(review.id());
		conceptChangesStore.remove(review.id());
		return ReviewImpl.builder((ReviewImpl) review).deleted().build();
	}
}
