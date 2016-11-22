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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.metrics.Metrics;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.TransactionalRequest;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.ReservingIdStrategy;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
final class IdRequest extends DelegatingRequest<BranchContext, BranchContext, CommitResult> {

	private static final long serialVersionUID = 1L;

	private static final int ID_GENERATION_ATTEMPTS = 9999_9999;
	private static final ImmutableMap<ComponentCategory, Class<? extends SnomedComponentDocument>> CATEGORY_TO_DOC_MAP = ImmutableMap
			.<ComponentCategory, Class<? extends SnomedComponentDocument>> builder()
			.put(ComponentCategory.CONCEPT, SnomedConceptDocument.class)
			.put(ComponentCategory.RELATIONSHIP, SnomedRelationshipIndexEntry.class)
			.put(ComponentCategory.DESCRIPTION, SnomedDescriptionIndexEntry.class).build();

	protected IdRequest(final Request<BranchContext, CommitResult> next) {
		super(next);
	}

	@Override
	public CommitResult execute(final BranchContext context) {

		final ISnomedIdentifierService identifierService = context.service(ISnomedIdentifierService.class);
		final SnomedIdentifiers snomedIdentifiers = new SnomedIdentifiers(identifierService);

		try {

			final Multimap<ComponentCategory, BaseSnomedComponentCreateRequest> bulkCreateRequests = extractBulkCreateRequests(next());

			if (!bulkCreateRequests.isEmpty()) {

				final Timer idGenerationTimer = context.service(Metrics.class).timer("bulkIdGeneration");

				try {

					idGenerationTimer.start();

					for (final Entry<ComponentCategory, Collection<BaseSnomedComponentCreateRequest>> entry : bulkCreateRequests.asMap().entrySet()) {

						final ComponentCategory componentCategory = entry.getKey();
						final Collection<BaseSnomedComponentCreateRequest> requests = entry.getValue();

						final Multimap<String, BaseSnomedComponentCreateRequest> nameSpaceToRequestMap = FluentIterable.from(requests)
								.index(new Function<BaseSnomedComponentCreateRequest, String>() {
									@Override
									public String apply(final BaseSnomedComponentCreateRequest input) {
										return getNamespace(input);
									}
								});

						for (final Entry<String, Collection<BaseSnomedComponentCreateRequest>> nameSpaceEntry : nameSpaceToRequestMap.asMap()
								.entrySet()) {

							final String namespace = nameSpaceEntry.getKey();
							final Collection<BaseSnomedComponentCreateRequest> requestsWithNamespace = nameSpaceEntry.getValue();

							final String namespaceToUse = namespace.equals(SnomedIdentifiers.INT_NAMESPACE) ? null : namespace;

							final Collection<String> ids = newArrayList(
									snomedIdentifiers.reserve(namespaceToUse, componentCategory, requestsWithNamespace.size()));

							final Collection<String> uniqueIds = getUniqueIds(ids, context, snomedIdentifiers, componentCategory,
									requestsWithNamespace.size(), namespaceToUse);

							final Iterator<String> idsToUse = Iterators.consumingIterator(uniqueIds.iterator());

							for (final BaseSnomedComponentCreateRequest createRequest : requestsWithNamespace) {
								createRequest.setIdGenerationStrategy(new ConstantIdStrategy(idsToUse.next()));
							}

							checkState(Iterators.size(idsToUse) == 0, "More SNOMED CT ids have been requested than the amount of create requests");

						}

					}

				} finally {
					idGenerationTimer.stop();
				}

			}

			final CommitResult commitInfo = next(DelegatingBranchContext.basedOn(context).bind(SnomedIdentifiers.class, snomedIdentifiers).build());

			snomedIdentifiers.commit();

			return commitInfo;

		} catch (final Exception e) {
			// TODO check exception type and decide what to do (e.g. rollback ID
			// request or not)
			snomedIdentifiers.rollback();

			throw e;
		}
	}

	private Collection<String> getUniqueIds(final Collection<String> ids, final BranchContext context, final SnomedIdentifiers snomedIdentifiers,
			final ComponentCategory componentCategory, final int quantity, final String namespaceToUse) {

		for (int i = 0; i < ID_GENERATION_ATTEMPTS; i++) {

			final Collection<String> existingIds = getExistingIds(context, ids, CATEGORY_TO_DOC_MAP.get(componentCategory));

			if (!existingIds.isEmpty()) {

				ids.removeAll(existingIds);

				final Collection<String> newIds = snomedIdentifiers.reserve(namespaceToUse, componentCategory, existingIds.size());

				ids.addAll(newIds);

			} else {

				return ids;

			}
		}

		throw new BadRequestException("There are insufficient number of component ids available for category: %s",
				componentCategory.getDisplayName());
	}

	private Collection<String> getExistingIds(final BranchContext context, final Collection<String> ids,
			final Class<? extends SnomedComponentDocument> clazz) {
		return context.service(RevisionIndex.class).read(context.branch().path(), new RevisionIndexRead<Collection<String>>() {
			@Override
			public Collection<String> execute(final RevisionSearcher index) throws IOException {
				final Hits<? extends SnomedComponentDocument> hits = index.searcher()
						.search(Query.select(clazz)
								.where(RevisionDocument.Expressions.ids(ids))
								.limit(ids.size())
								.build());
				return FluentIterable.from(hits).transform(ComponentUtils.getIdFunction()).toSet();
			}
		});
	}

	private String getNamespace(final SnomedComponentCreateRequest createRequest) {
		final IdGenerationStrategy idGenerationStrategy = createRequest.getIdGenerationStrategy();
		if (idGenerationStrategy instanceof ReservingIdStrategy) {
			final String namespace = ((ReservingIdStrategy) idGenerationStrategy).getNamespaceId();
			return namespace == null ? SnomedIdentifiers.INT_NAMESPACE : namespace;
		}
		return SnomedIdentifiers.INT_NAMESPACE;
	}

	private Multimap<ComponentCategory, BaseSnomedComponentCreateRequest> extractBulkCreateRequests(final Request<?, ?> next) {

		final Request<?, ?> firstNonDelegateRequest = getFirstNonDelegatingRequest(next);

		if (firstNonDelegateRequest instanceof TransactionalRequest) {
			final TransactionalRequest transactionalRequest = (TransactionalRequest) firstNonDelegateRequest;
			final Request<?, ?> nextRequest = transactionalRequest.getNext();

			if (nextRequest instanceof BulkRequest) {
				return collectCreateRequests((BulkRequest<?>) nextRequest);
			}
		}

		return ImmutableMultimap.<ComponentCategory, BaseSnomedComponentCreateRequest> of();
	}

	private Multimap<ComponentCategory, BaseSnomedComponentCreateRequest> collectCreateRequests(final BulkRequest<?> bulkRequest) {
		return FluentIterable.from(bulkRequest.getRequests()).filter(BaseSnomedComponentCreateRequest.class)
				.index(new Function<Request<?, ?>, ComponentCategory>() {
					@Override
					public ComponentCategory apply(final Request<?, ?> input) {
						if (input instanceof SnomedConceptCreateRequest) {
							return ComponentCategory.CONCEPT;
						} else if (input instanceof SnomedDescriptionCreateRequest) {
							return ComponentCategory.DESCRIPTION;
						} else if (input instanceof SnomedRelationshipCreateRequest) {
							return ComponentCategory.RELATIONSHIP;
						}
						throw new NotImplementedException("Unknown create request type: %s", input.getClass().getName());
					}
				});
	}

	private Request<?, ?> getFirstNonDelegatingRequest(final Request<?, ?> next) {
		if (next instanceof DelegatingRequest) {
			return getFirstNonDelegatingRequest(((DelegatingRequest<?, ?, ?>) next).next());
		}
		return next;
	}
}
