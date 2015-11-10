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
package com.b2international.snowowl.snomed.datastore.server.refset;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorClientService;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class QueryRefSetUpdater {

	public List<QueryTypeRefSetMemberDifference> reevaluateQueryRefSet(SnomedRegularRefSet queryRefSet) {
		if (!SnomedRefSetType.QUERY.equals(queryRefSet.getType())) {
			throw new BadRequestException("Cannot reevaluate reference set '%s'", queryRefSet.getIdentifierId());
		}
		final List<QueryTypeRefSetMemberDifference> differences = newArrayList();
		for (SnomedQueryRefSetMember member : Iterables.filter(queryRefSet.getMembers(), SnomedQueryRefSetMember.class)) {
			differences.add(reevaluateMember(member));
		}
		return differences;

	}

	public QueryTypeRefSetMemberDifference reevaluateMember(SnomedQueryRefSetMember member) {
		final String query = member.getQuery();
		final String targetReferenceSet = member.getReferencedComponentId();

		// TODO convert this to request call if required
		final IEscgQueryEvaluatorClientService queryEvaluatorService = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorClientService.class);
		final Collection<SnomedConceptIndexEntry> matchingQueryConcepts = queryEvaluatorService.evaluate(query);

		final Map<String, SnomedConceptIndexEntry> conceptsToAdd = newHashMap();
		final Collection<SnomedRefSetMemberIndexEntry> membersToRemove = newHashSet();
		final Map<String, String> conceptsToActivate = Maps.newHashMap();

		// all members of the target simple type reference set
		// TODO convert this to Request call
		final SnomedClientIndexService indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		final SnomedRefSetMemberIndexQueryAdapter adapter = new SnomedRefSetMemberIndexQueryAdapter(targetReferenceSet, null, false);
		final List<SnomedRefSetMemberIndexEntry> currentTargetReferenceSetMembers = indexSearcher.search(adapter);

		// add all matching
		for (SnomedConceptIndexEntry matchedConcept : matchingQueryConcepts) {
			if (matchedConcept.isActive()) {
				conceptsToAdd.put(matchedConcept.getId(), matchedConcept);
			}
		}
		
		for (SnomedRefSetMemberIndexEntry currentMember : currentTargetReferenceSetMembers) {
			final String referencedComponentId = currentMember.getReferencedComponentId();
			if (conceptsToAdd.containsKey(referencedComponentId)) {
				if (!currentMember.isActive()) {
					conceptsToActivate.put(referencedComponentId, currentMember.getLabel());
				} else {
					conceptsToAdd.remove(referencedComponentId);
				}
			} else {
				membersToRemove.add(currentMember);
			}
		}
		
		QueryTypeRefSetMemberDifference diff = new QueryTypeRefSetMemberDifference(conceptsToAdd, membersToRemove, conceptsToActivate);
		diff.setTargetReferenceSetId(targetReferenceSet);
		return diff;
	}

	public static class QueryTypeRefSetMemberDifference {

		public enum Action {
			ADD, REMOVE, ACTIVATE
		}

		public static class Diff {
			private Action action;
			private boolean selected = true;
			private String referencedComponentId;
			private String label;
			private String memberId;

			public Diff(Action action, String referencedComponentId, String label) {
				this.action = action;
				this.referencedComponentId = referencedComponentId;
				this.label = label;
			}

			public String getMemberId() {
				return memberId;
			}
			
			public void setMemberId(String memberId) {
				this.memberId = memberId;
			}
			
			public Action getAction() {
				return action;
			}

			public String getReferencedComponentId() {
				return referencedComponentId;
			}

			public String getLabel() {
				return label;
			}

			public boolean isSelected() {
				return selected;
			}

			public void setSelected(boolean selected) {
				this.selected = selected;
			}

			@Override
			public String toString() {
				return action.toString() + " [" + label + "(" + referencedComponentId + ")] (selected: " + selected + ")";
			}

			@Override
			public int hashCode() {
				return Objects.hash(action, referencedComponentId);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Diff other = (Diff) obj;
				return Objects.equals(action, other.action) && Objects.equals(referencedComponentId, other.referencedComponentId);
			}

		}

		public static class DiffComparator implements Comparator<Diff> {

			@Override
			public int compare(Diff diff1, Diff diff2) {
				int diff = diff1.getAction().compareTo(diff2.getAction());

				if (diff == 0) {
					diff = diff1.getLabel().compareTo(diff2.getLabel());
				}

				if (diff == 0) {
					diff = diff1.getReferencedComponentId().compareTo(diff2.getReferencedComponentId());
				}
				return diff;
			}
		}

		public Set<Diff> diffs = Sets.newTreeSet(new DiffComparator());
		private String targetReferenceSetId;

		public QueryTypeRefSetMemberDifference(Map<String, SnomedConceptIndexEntry> conceptsToAdd, Collection<SnomedRefSetMemberIndexEntry> membersToRemove, Map<String, String> conceptsToActivate) {
			super();
			for (String id : conceptsToAdd.keySet()) {
				diffs.add(new Diff(Action.ADD, id, conceptsToAdd.get(id).getLabel()));
			}

			for (SnomedRefSetMemberIndexEntry member : membersToRemove) {
				final Diff diff = new Diff(Action.REMOVE, member.getReferencedComponentId(), member.getLabel());
				diff.setMemberId(member.getId());
				diffs.add(diff);
			}

			for (String id : conceptsToActivate.keySet()) {
				diffs.add(new Diff(Action.ACTIVATE, id, conceptsToActivate.get(id)));
			}
		}

		public Set<Diff> getDiffs() {
			return diffs;
		}

		public boolean isEmpty() {
			return diffs.isEmpty();
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Diff diff : diffs) {
				sb.append(diff);
			}
			sb.append("]");

			return sb.toString();
		}
		
		void setTargetReferenceSetId(String targetReferenceSetId) {
			this.targetReferenceSetId = targetReferenceSetId;
		}

		public String getTargetReferenceSetId() {
			return this.targetReferenceSetId;
		}

	}

}