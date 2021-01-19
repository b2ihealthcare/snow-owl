/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.metrics.Metrics;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.request.TransactionalRequest;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.action.IdActionRecorder;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.NamespaceIdStrategy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public final class IdRequest<C extends BranchContext, R> extends DelegatingRequest<C, C, R> {

	/** 
	 * The maximum number of identifier service reservation calls (after which a namespace is known to be completely full). 
	 */
	public static final int ID_GENERATION_ATTEMPTS = 9999_9999;

	private static final long serialVersionUID = 1L;
	
	private static final Map<ComponentCategory, Class<? extends SnomedDocument>> CATEGORY_TO_DOCUMENT_CLASS_MAP;
	
	static {
		CATEGORY_TO_DOCUMENT_CLASS_MAP = ImmutableMap.<ComponentCategory, Class<? extends SnomedDocument>>builder()
				.put(ComponentCategory.CONCEPT, SnomedConceptDocument.class)
				.put(ComponentCategory.RELATIONSHIP, SnomedRelationshipIndexEntry.class)
				.put(ComponentCategory.DESCRIPTION, SnomedDescriptionIndexEntry.class)
				.put(ComponentCategory.SET_MEMBER, SnomedRefSetMemberIndexEntry.class)
				.build();
	}

	private static Class<? extends SnomedDocument> getDocumentClass(final ComponentCategory category) {
		return CATEGORY_TO_DOCUMENT_CLASS_MAP.get(category);
	}

	public IdRequest(final Request<C, R> next) {
		super(next);
	}

	@Override
	public R execute(final C context) {

		final IdActionRecorder recorder = new IdActionRecorder(context);

		try {

			final Multimap<ComponentCategory, SnomedComponentCreateRequest> componentCreateRequests = getComponentCreateRequests(next());

			if (!componentCreateRequests.isEmpty()) {
				final Timer idGenerationTimer = context.service(Metrics.class).timer("idGeneration");

				try {
					idGenerationTimer.start();

					for (final ComponentCategory category : componentCreateRequests.keySet()) {
						final Class<? extends SnomedDocument> documentClass = getDocumentClass(category);
						final Collection<SnomedComponentCreateRequest> categoryRequests = componentCreateRequests.get(category);
						
						final Set<String> coreComponentIds = FluentIterable.from(categoryRequests)
								.filter(SnomedCoreComponentCreateRequest.class)
								.filter(request -> request.getIdGenerationStrategy() instanceof ConstantIdStrategy)
								.transform(request -> ((ConstantIdStrategy) request.getIdGenerationStrategy()).getId())
								.toSet();
						
						final Set<String> refsetMemberIds = FluentIterable.from(categoryRequests)
								.filter(SnomedRefSetMemberCreateRequest.class)
								.transform(request -> request.getId())
								.toSet();
						
						final Set<String> userRequestedIds = Sets.newHashSet(Iterables.concat(coreComponentIds, refsetMemberIds));
						
						final Set<String> existingIds = getExistingIds(context, userRequestedIds, documentClass);
						if (!existingIds.isEmpty()) {
							// TODO: Report all existing identifiers
							throw new AlreadyExistsException(category.getDisplayName(), Iterables.getFirst(existingIds, null));
						} else {
							if (!coreComponentIds.isEmpty()) {
								recorder.register(coreComponentIds);
							}
						}
						
						final Multimap<String, BaseSnomedComponentCreateRequest> requestsByNamespace = FluentIterable.from(categoryRequests)
								.filter(BaseSnomedComponentCreateRequest.class)
								.filter(request -> request.getIdGenerationStrategy() instanceof NamespaceIdStrategy)
								.index(request -> getNamespaceKey(request));

						for (final String namespace : requestsByNamespace.keySet()) {
							final String convertedNamespace = namespace.equals(SnomedIdentifiers.INT_NAMESPACE) ? null : namespace;
							final Collection<BaseSnomedComponentCreateRequest> namespaceRequests = requestsByNamespace.get(namespace);
							final int count = namespaceRequests.size();
							
							final Set<String> uniqueIds = getUniqueIds(context, recorder, category, documentClass, count, convertedNamespace);

							final Iterator<String> idsToUse = Iterators.consumingIterator(uniqueIds.iterator());
							for (final BaseSnomedComponentCreateRequest createRequest : namespaceRequests) {
								createRequest.setIdGenerationStrategy(new ConstantIdStrategy(idsToUse.next()));
							}

							checkState(!idsToUse.hasNext(), "More SNOMED CT ids have been requested than used.");
						}
					}

				} finally {
					idGenerationTimer.stop();
				}
			}

			final R commitInfo = next(context);
			recorder.commit();
			return commitInfo;

		} catch (final Exception e) {
			
			recorder.rollback();
			throw e;
		}
	}

	private static Multimap<ComponentCategory, SnomedComponentCreateRequest> getComponentCreateRequests(final Request<?, ?> request) {
		final ImmutableMultimap.Builder<ComponentCategory, SnomedComponentCreateRequest> resultBuilder = ImmutableMultimap.builder();
		collectComponentCreateRequests(request, resultBuilder);
		return resultBuilder.build();
	}
	
	private static void collectComponentCreateRequests(Request<?, ?> request, ImmutableMultimap.Builder<ComponentCategory, SnomedComponentCreateRequest> resultBuilder) {
		if (request instanceof DelegatingRequest) {
			collectComponentCreateRequests(((DelegatingRequest<?, ?, ?>) request).next(), resultBuilder);
		} else if (request instanceof TransactionalRequest) {
			collectComponentCreateRequests(((TransactionalRequest) request).getNext(), resultBuilder);
		} else if (request instanceof BaseSnomedComponentCreateRequest) {
			final BaseSnomedComponentCreateRequest createRequest = (BaseSnomedComponentCreateRequest) request;
			for (SnomedCoreComponentCreateRequest nestedRequest : createRequest.getNestedRequests()) {
				ComponentCategory category = getComponentCategory(nestedRequest);
				resultBuilder.put(category, (BaseSnomedComponentCreateRequest) nestedRequest);
				// XXX: we could recurse here, but only concept creation requests have actual nested requests at the moment
			}
		} else if (request instanceof SnomedRefSetMemberCreateRequest) {
			final SnomedRefSetMemberCreateRequest createRequest = (SnomedRefSetMemberCreateRequest) request;
			final ComponentCategory category = getComponentCategory(createRequest);
			
			resultBuilder.put(category, createRequest);
		} else if (request instanceof BulkRequest) {
			final BulkRequest<?> bulkRequest = (BulkRequest<?>) request;
			for (Request<?, ?> bulkRequestItem : bulkRequest.getRequests()) {
				collectComponentCreateRequests(bulkRequestItem, resultBuilder);
			}
		}
	}

	private static ComponentCategory getComponentCategory(SnomedComponentRequest<?> request) {
		if (request instanceof SnomedConceptCreateRequest) {
			return ComponentCategory.CONCEPT;
		} else if (request instanceof SnomedDescriptionCreateRequest) {
			return ComponentCategory.DESCRIPTION;
		} else if (request instanceof SnomedRelationshipCreateRequest) {
			return ComponentCategory.RELATIONSHIP;
		} else if (request instanceof SnomedRefSetMemberCreateRequest) {
			return ComponentCategory.SET_MEMBER;
		} else {
			throw new NotImplementedException("Unknown create request type: %s", request.getClass().getName());
		}
	}

	private Set<String> getUniqueIds(final BranchContext context, final IdActionRecorder recorder, 
			final ComponentCategory category, 
			final Class<? extends SnomedDocument> documentClass, 
			final int quantity, 
			final String namespace) {
		
		final Set<String> uniqueIds = newHashSet(); 
				
		for (int i = 0; i < ID_GENERATION_ATTEMPTS && uniqueIds.size() < quantity; i++) {

			final Set<String> candidateIds = recorder.reserve(namespace, category, quantity - uniqueIds.size());
			uniqueIds.addAll(candidateIds);
			
			final Set<String> existingIds = getExistingIds(context, candidateIds, documentClass);
			uniqueIds.removeAll(existingIds);
		}

		if (uniqueIds.size() == quantity) {
			return uniqueIds;
		} else {
			throw new BadRequestException("There are insufficient number of component ids available for category: %s", category.getDisplayName());
		}
	}

	private Set<String> getExistingIds(final BranchContext context, final Set<String> ids, final Class<? extends SnomedDocument> documentClass) {
		
		try {
			
			final Query<String> getComponentsById = Query.select(String.class).from(documentClass)
					.fields(RevisionDocument.Fields.ID)
					.where(RevisionDocument.Expressions.ids(ids))
					.limit(ids.size())
					.build();
			
			return Sets.newHashSet(context.service(RevisionSearcher.class).search(getComponentsById));
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	/**
	 * @return a namespace key intended to be used as a key in a collection;
	 *         <code>null</code> values are converted to {@link SnomedIdentifiers#INT_NAMESPACE}.
	 */
	private String getNamespaceKey(final SnomedCoreComponentCreateRequest createRequest) {
		final IdGenerationStrategy idGenerationStrategy = createRequest.getIdGenerationStrategy();
		final String namespace = idGenerationStrategy.getNamespace();
		return namespace == null ? SnomedIdentifiers.INT_NAMESPACE : namespace;
	}
}
