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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChangeImpl;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluationImpl;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
public final class EvaluateQueryRefSetMemberRequest extends ResourceRequest<BranchContext, QueryRefSetMemberEvaluation> {

	@NotEmpty
	private String memberId;
	
	EvaluateQueryRefSetMemberRequest(String memberId) {
		this.memberId = memberId;
	}
	
	@Override
	public QueryRefSetMemberEvaluation execute(BranchContext context) {
		// TODO support pre-population???
		final String query;
		final String targetReferenceSet;
		if (context instanceof TransactionContext) {
			SnomedQueryRefSetMember member = ((TransactionContext) context).lookup(memberId, SnomedQueryRefSetMember.class);
			query = member.getQuery();
			targetReferenceSet = member.getReferencedComponentId();
		} else {
			final SnomedReferenceSetMember member = SnomedRequests
					.prepareGetMember(memberId)
					.build()
					.execute(context);
			query = (String) member.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
			targetReferenceSet = member.getReferencedComponent().getId();
		}
		

		// GET matching members of a query
		final SnomedConcepts matchingConcepts = SnomedRequests.prepareSearchConcept().filterByEcl(query).all().build().execute(context);
		
		final Map<String, SnomedConcept> conceptsToAdd = newHashMap();
		final Collection<SnomedReferenceSetMember> membersToRemove = newHashSet();
		final Map<String, String> conceptsToActivate = Maps.newHashMap();

		// add all matching first
		for (SnomedConcept matchedConcept : matchingConcepts.getItems()) {
			if (matchedConcept.isActive()) {
				conceptsToAdd.put(matchedConcept.getId(), matchedConcept);
			}
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
					// TODO fix reactivation label???
					conceptsToActivate.put(referencedComponentId, referencedComponentId);
				} else {
					conceptsToAdd.remove(referencedComponentId);
				}
			} else {
				membersToRemove.add(currentMember);
			}
		}
		
		final Collection<MemberChange> changes = newArrayList();
		
		// fetch all referenced components
		final Set<String> referencedConceptIds = newHashSet();
		referencedConceptIds.addAll(conceptsToAdd.keySet());
		referencedConceptIds.addAll(FluentIterable.from(membersToRemove).transform(new Function<SnomedReferenceSetMember, SnomedCoreComponent>() {
			@Override
			public SnomedCoreComponent apply(SnomedReferenceSetMember input) {
				return input.getReferencedComponent();
			}
		}).transform(IComponent.ID_FUNCTION).toSet());
		
		final Map<String, SnomedConcept> concepts;
		if (expand().containsKey("referencedComponent")) {
			final Options expandOptions = expand().getOptions("referencedComponent");
			concepts = Maps.uniqueIndex(SnomedRequests.prepareSearchConcept()
					.filterByIds(referencedConceptIds)
					.setLimit(referencedConceptIds.size())
					.setExpand(expandOptions.getOptions("expand"))
					.setLocales(locales())
					.build()
					.execute(context), IComponent.ID_FUNCTION);
		} else {
			// initialize with empty SnomedConcept resources
			concepts = newHashMap();
			for (String referencedConceptId : referencedConceptIds) {
				concepts.put(referencedConceptId, new SnomedConcept(referencedConceptId));
			}
		}
		
		for (String id : conceptsToAdd.keySet()) {
			changes.add(MemberChangeImpl.added(concepts.get(id)));
		}

		for (SnomedReferenceSetMember memberToRemove : membersToRemove) {
			changes.add(MemberChangeImpl.removed(concepts.get(memberToRemove.getReferencedComponent().getId()), memberToRemove.getId()));
		}

		// TODO reactivation???
//		for (String id : conceptsToActivate.keySet()) {
//			changes.add(new Diff(MemberChangeKind.ACTIVATE, id, conceptsToActivate.get(id)));
//		}
		return new QueryRefSetMemberEvaluationImpl(targetReferenceSet, changes);
	}

}
