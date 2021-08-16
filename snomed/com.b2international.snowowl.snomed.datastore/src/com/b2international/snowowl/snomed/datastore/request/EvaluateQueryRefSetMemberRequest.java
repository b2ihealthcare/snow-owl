/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.IndexResourceRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.*;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
public final class EvaluateQueryRefSetMemberRequest extends IndexResourceRequest<BranchContext, QueryRefSetMemberEvaluation> implements AccessControl {

	@NotEmpty
	private String memberId;
	
	EvaluateQueryRefSetMemberRequest(String memberId) {
		this.memberId = memberId;
	}
	
	@Override
	public QueryRefSetMemberEvaluation execute(BranchContext context) {
		// TODO support pre-population???
		final boolean active;
		final String query;
		final String targetReferenceSet;
		
		if (context instanceof TransactionContext) {
			SnomedRefSetMemberIndexEntry member = ((TransactionContext) context).lookup(memberId, SnomedRefSetMemberIndexEntry.class);
			query = member.getQuery();
			targetReferenceSet = member.getReferencedComponentId();
			active = member.isActive();
		} else {
			final SnomedReferenceSetMember member = SnomedRequests
					.prepareGetMember(memberId)
					.build()
					.execute(context);
			
			query = (String) member.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
			targetReferenceSet = member.getReferencedComponent().getId();
			active = member.isActive();
		}
		
		if (!active) {
			return new QueryRefSetMemberEvaluationImpl(memberId, targetReferenceSet, Collections.emptyList());
		}
		
		if (Strings.isNullOrEmpty(query)) {
			return new QueryRefSetMemberEvaluationImpl(memberId, targetReferenceSet, Collections.emptyList());
		}

		// GET matching members of a query
		final SnomedConcepts matchingConcepts = SnomedRequests.prepareSearchConcept()
				.filterByEcl(query)
				.all()
				.build()
				.execute(context);
		
		final Map<String, SnomedConcept> conceptsToAdd = newHashMap();
		final Collection<SnomedReferenceSetMember> membersToRemove = newHashSet();
		final Collection<SnomedReferenceSetMember> conceptsToActivate = newHashSet();

		// add all matching first
		for (SnomedConcept matchedConcept : matchingConcepts.getItems()) {
			conceptsToAdd.put(matchedConcept.getId(), matchedConcept);
		}
		
		// then re-evaluate all current members of the target simple type reference set
		final Collection<SnomedReferenceSetMember> curretMembersOfTarget = SnomedRequests.prepareSearchMember()
					.all()
					.filterByRefSet(targetReferenceSet)
					.build()
					.execute(context)
					.getItems();
		
		for (SnomedReferenceSetMember currentMember : curretMembersOfTarget) {
			final String referencedComponentId = currentMember.getReferencedComponent().getId();
			if (conceptsToAdd.containsKey(referencedComponentId)) {
				if (!currentMember.isActive()) {
					conceptsToAdd.remove(referencedComponentId);
					conceptsToActivate.add(currentMember);
				} else {
					conceptsToAdd.remove(referencedComponentId);
				}
			} else {
				if (currentMember.isActive()) {
					membersToRemove.add(currentMember);
				}
			}
		}
		
		// fetch all referenced components
		final Set<String> referencedConceptIds = newHashSet();
		referencedConceptIds.addAll(conceptsToAdd.keySet());
		referencedConceptIds.addAll(FluentIterable.from(membersToRemove).transform(SnomedReferenceSetMember::getReferencedComponent).transform(IComponent::getId).toSet());
		
		referencedConceptIds.addAll(FluentIterable.from(conceptsToActivate).transform(SnomedReferenceSetMember::getReferencedComponent).transform(IComponent::getId).toSet());
		
		final Map<String, SnomedConcept> concepts;
		if (expand().containsKey("referencedComponent")) {
			final Options expandOptions = expand().getOptions("referencedComponent");
			concepts = Maps.uniqueIndex(SnomedRequests.prepareSearchConcept()
					.filterByIds(referencedConceptIds)
					.setLimit(referencedConceptIds.size())
					.setExpand(expandOptions.getOptions("expand"))
					.setLocales(locales())
					.build()
					.execute(context), IComponent::getId);
		} else {
			// initialize with empty SnomedConcept resources
			concepts = newHashMap();
			for (String referencedConceptId : referencedConceptIds) {
				concepts.put(referencedConceptId, new SnomedConcept(referencedConceptId));
			}
		}
		
		final Collection<MemberChange> changes = newArrayList();
		
		for (String id : conceptsToAdd.keySet()) {
			changes.add(MemberChangeImpl.added(concepts.get(id)));
		}

		for (SnomedReferenceSetMember memberToRemove : membersToRemove) {
			changes.add(MemberChangeImpl.removed(concepts.get(memberToRemove.getReferencedComponent().getId()), memberToRemove.getId()));
		}

		for (SnomedReferenceSetMember conceptToActivate : conceptsToActivate) {
			changes.add(MemberChangeImpl.changed(concepts.get(conceptToActivate.getReferencedComponent().getId()), conceptToActivate.getId()));
		}
		return new QueryRefSetMemberEvaluationImpl(memberId, targetReferenceSet, changes);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

}
