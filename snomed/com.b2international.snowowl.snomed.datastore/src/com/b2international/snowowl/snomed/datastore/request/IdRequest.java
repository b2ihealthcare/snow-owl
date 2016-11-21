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

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.TransactionalRequest;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.ReservingIdStrategy;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
final class IdRequest extends DelegatingRequest<BranchContext, BranchContext, CommitResult> {

	private static final long serialVersionUID = 1L;

	protected IdRequest(final Request<BranchContext, CommitResult> next) {
		super(next);
	}

	@Override
	public CommitResult execute(final BranchContext context) {
		
		final ISnomedIdentifierService identifierService = context.service(ISnomedIdentifierService.class);
		final SnomedIdentifiers snomedIdentifiers = new SnomedIdentifiers(identifierService);

		try {

			final Multimap<ComponentCategory, SnomedComponentCreateRequest> bulkCreateRequests = extractBulkCreateRequests(next());

			if (bulkCreateRequests != null) {

				for (final Entry<ComponentCategory, Collection<SnomedComponentCreateRequest>> entry : bulkCreateRequests.asMap().entrySet()) {

					final ComponentCategory componentCategory = entry.getKey();
					final Collection<SnomedComponentCreateRequest> requests = entry.getValue();

					final Multimap<String, SnomedComponentCreateRequest> nameSpaceToRequestMap = FluentIterable.from(requests)
							.index(new Function<SnomedComponentCreateRequest, String>() {
								@Override
								public String apply(final SnomedComponentCreateRequest input) {
									return getNamespace(input);
								}
							});

					for (final Entry<String, Collection<SnomedComponentCreateRequest>> nameSpaceEntry : nameSpaceToRequestMap.asMap().entrySet()) {

						final String namespace = nameSpaceEntry.getKey();
						final Collection<SnomedComponentCreateRequest> requestsWithNamespace = nameSpaceEntry.getValue();

						final String namespaceToUse = namespace.equals(SnomedIdentifiers.INT_NAMESPACE) ? null : namespace;
						final Collection<String> ids = snomedIdentifiers.reserve(namespaceToUse, componentCategory, requestsWithNamespace.size());
						
						final List<String> idsCopy = newArrayListWithExpectedSize(ids.size());
						idsCopy.addAll(ids);
						
						final Iterator<String> idsToUse = Iterators.consumingIterator(idsCopy.iterator());

						for (final SnomedComponentCreateRequest createRequest : requestsWithNamespace) {
							createRequest.setIdGenerationStrategy(new ConstantIdStrategy(idsToUse.next()));
						}

						if (Iterators.size(idsToUse) != 0) {
							throw new RuntimeException("More SNOMED CT ids have been requested than the amount of create requests");
						}

					}
					
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

	private String getNamespace(final SnomedComponentCreateRequest createRequest) {
		final IdGenerationStrategy idGenerationStrategy = createRequest.getIdGenerationStrategy();
		if (idGenerationStrategy instanceof ReservingIdStrategy) {
			final String namespace = ((ReservingIdStrategy) idGenerationStrategy).getNamespaceId();
			return namespace == null ? SnomedIdentifiers.INT_NAMESPACE : namespace;
		}
		return SnomedIdentifiers.INT_NAMESPACE;
	}

	private Multimap<ComponentCategory, SnomedComponentCreateRequest> extractBulkCreateRequests(final Request<?, ?> next) {

		final Request<?, ?> firstNonDelegateRequest = getFirstNonDelegatingRequest(next);

		if (firstNonDelegateRequest instanceof TransactionalRequest) {
			final TransactionalRequest transactionalRequest = (TransactionalRequest) firstNonDelegateRequest;
			final Request<?, ?> nextRequest = transactionalRequest.getNext();

			if (nextRequest instanceof BulkRequest) {
				return collectCreateRequests((BulkRequest<?>) nextRequest);
			}
		}

		return null;
	}

	private Multimap<ComponentCategory, SnomedComponentCreateRequest> collectCreateRequests(final BulkRequest<?> bulkRequest) {
		return FluentIterable.from(bulkRequest.getRequests()).filter(SnomedComponentCreateRequest.class)
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
						throw new IllegalArgumentException(String.format("Unknown create request type: %s", input.getClass().getName()));
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
