/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.IndexResourceRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.*;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;

/**
 * @since 4.5
 */
public final class EvaluateQueryRefSetMemberRequest extends IndexResourceRequest<BranchContext, QueryRefSetMemberEvaluation> implements AccessControl {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private final String memberId;

	EvaluateQueryRefSetMemberRequest(final String memberId) {
		this.memberId = memberId;
	}

	@Override
	public QueryRefSetMemberEvaluation execute(final BranchContext context) {
		// TODO support pre-population???
		final boolean active;
		final String query;
		final String targetReferenceSet;

		if (context instanceof TransactionContext) {
			final TransactionContext transactionContext = (TransactionContext) context;
			final SnomedRefSetMemberIndexEntry member = transactionContext.lookup(memberId, SnomedRefSetMemberIndexEntry.class);
			
			query = member.getQuery();
			targetReferenceSet = member.getReferencedComponentId();
			active = member.isActive();
		} else {
			final SnomedReferenceSetMember member = SnomedRequests.prepareGetMember(memberId)
				.build()
				.execute(context);

			query = (String) member.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
			targetReferenceSet = member.getReferencedComponentId();
			active = member.isActive();
		}

		if (!active) {
			return new QueryRefSetMemberEvaluationImpl(memberId, targetReferenceSet);
		}

		if (Strings.isNullOrEmpty(query)) {
			return new QueryRefSetMemberEvaluationImpl(memberId, targetReferenceSet);
		}

		final Set<String> expectedConcepts = newHashSet();
		final Options expandOptions = expand().getOptions("referencedComponent");
		final int pageSize = context.getPageSize();
		
		// Evaluate the query expression to find out which concepts should be in the simple type reference set
		final Stream<MemberChange> expectedConceptChanges = SnomedRequests.prepareSearchConcept()
			.filterByEcl(query)
			.setExpand(expandOptions.getOptions("expand"))
			.setLocales(locales())
			.setLimit(pageSize)
			.stream(context)
			.flatMap(batch -> {
				final Map<String, SnomedConcept> conceptsToAdd = newHashMap(Maps.uniqueIndex(batch, SnomedConcept::getId));
				final Map<String, SnomedConcept> conceptsSeen = newHashMap();
				final List<MemberChange> memberChanges = newArrayList();

				expectedConcepts.addAll(conceptsToAdd.keySet());
				
				/*
				 * Search for existing reference set members with the same referenced component
				 * (but keep in mind that multiple members can be present for each one)
				 */
				SnomedRequests.prepareSearchMember()
					.filterByRefSet(targetReferenceSet)
					.filterByReferencedComponent(conceptsToAdd.keySet())
					.sortBy("id:asc")
					.setLimit(pageSize)
					.stream(context)
					.flatMap(SnomedReferenceSetMembers::stream)
					.forEachOrdered(member -> {
						final String memberId = member.getId();
						final Boolean memberActive = member.isActive();
						final String referencedComponentId = member.getReferencedComponentId();

						if (conceptsToAdd.containsKey(referencedComponentId)) {
							final SnomedConcept visitedConcept = conceptsToAdd.remove(referencedComponentId);
							conceptsSeen.put(visitedConcept.getId(), visitedConcept);
							
							if (!memberActive) {
								// We don't need to add a new member, set the existing one to active instead
								memberChanges.add(MemberChangeImpl.changed(visitedConcept, memberId));
							} else {
								// The referenced component is in the evaluated result set, no need to add it again
								conceptsToAdd.remove(referencedComponentId);
							}
						} else {
							if (memberActive) {
								// We have seen the referenced component earlier; later occurrences should be removed
								memberChanges.add(MemberChangeImpl.removed(conceptsSeen.get(referencedComponentId), memberId));
							}
						}
					});
					
				// Any concept still present in conceptsToAdd should be added as a new member
				conceptsToAdd.values().forEach(c -> {
					memberChanges.add(MemberChangeImpl.added(c));
				});
				
				return memberChanges.stream();
			});

		/*
		 * XXX: Remaining active members that have an "unexpected" referenced component
		 * should also be registered for removal, however we won't know what these are
		 * until after the Stream above has been exhausted!
		 * 
		 * So adding a filter condition here that says "filter by all referenced 
		 * components _except_ this set" would turn up empty handed, as the set is not 
		 * populated at the time the query is put together.
		 */
		final Stream<MemberChange> unexpectedConceptChanges = SnomedRequests.prepareSearchMember()
			.filterByActive(true)
			.filterByRefSet(targetReferenceSet)
			.setExpand(expand())
			.setLocales(locales())
			.setLimit(pageSize)
			.stream(context)
			.flatMap(batch -> batch.stream())
			.filter(member -> !expectedConcepts.contains(member.getReferencedComponentId()))
			.<MemberChange>map(member -> MemberChangeImpl.removed((SnomedConcept) member.getReferencedComponent(), member.getId()));

		/* 
		 * We checked elements in batches matching the result window setting above, however if only a few members out of a single "run" are affected,
		 * we get small lists of changes which is ineffective. Re-package them into larger batches here.
		 */
		final Stream<List<MemberChange>> expectedConceptBatches = Streams.stream(Iterators.partition(expectedConceptChanges.iterator(), 10_000));
		final Stream<List<MemberChange>> unexpectedConceptBatches = Streams.stream(Iterators.partition(unexpectedConceptChanges.iterator(), 10_000));
		return new QueryRefSetMemberEvaluationImpl(memberId, targetReferenceSet, Stream.concat(expectedConceptBatches, unexpectedConceptBatches));
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
}
