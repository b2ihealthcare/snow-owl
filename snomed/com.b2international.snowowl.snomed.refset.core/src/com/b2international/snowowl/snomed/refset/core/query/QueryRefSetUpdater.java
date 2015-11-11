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
package com.b2international.snowowl.snomed.refset.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorClientService;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class QueryRefSetUpdater {
	

	public List<QueryTypeRefSetMemberDifference> reevaluateQueryRefSet(SnomedRegularRefSet queryRefSet) {
		
		if(!queryRefSet.getType().equals(SnomedRefSetType.QUERY)){
			return Collections.emptyList();
		}
		
		List<QueryTypeRefSetMemberDifference> differences = new ArrayList<QueryTypeRefSetMemberDifference>();
		
		for(SnomedRefSetMember member : queryRefSet.getMembers()){
			
			SnomedQueryRefSetMember queryMember = (SnomedQueryRefSetMember) member;
			
			String query = queryMember.getQuery();

			IEscgQueryEvaluatorClientService queryEvaluatorService = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorClientService.class);
			final Collection<SnomedConceptIndexEntry> queryConceptMinis = queryEvaluatorService.evaluate(query);
			
			Map<String, String> conceptsToAdd = Maps.newHashMap();
			Map<String, String> conceptsToRemove = Maps.newHashMap();
			Map<String, String> conceptsToActivate = Maps.newHashMap();

			final SnomedClientIndexService indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
			final SnomedRefSetMemberIndexQueryAdapter adapter = new SnomedRefSetMemberIndexQueryAdapter(queryMember.getReferencedComponentId(), null, false);
			final List<SnomedRefSetMemberIndexEntry> results = indexSearcher.search(adapter);
			
			for(SnomedConceptIndexEntry queryResultConcept : queryConceptMinis){
				if (queryResultConcept.isActive()) {				
					conceptsToAdd.put(queryResultConcept.getId(), queryResultConcept.getLabel());
				}
			}
			
			for(SnomedRefSetMemberIndexEntry simpleMember : results){

				if(conceptsToAdd.containsKey(simpleMember.getReferencedComponentId())) {
					if(!simpleMember.isActive()){
						conceptsToActivate.put(simpleMember.getReferencedComponentId(), simpleMember.getLabel());
					}

					conceptsToAdd.remove(simpleMember.getReferencedComponentId());
				}
				else{
					conceptsToRemove.put(simpleMember.getReferencedComponentId(), simpleMember.getLabel());
				}
			}
			
			final ILookupService<String, SnomedRegularRefSet, CDOView> lookupService = CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.REFSET);
			
			QueryTypeRefSetMemberDifference memberDifference = new QueryTypeRefSetMemberDifference(
					lookupService.getComponent(queryMember.getReferencedComponentId(), queryRefSet.cdoView()), 
					conceptsToAdd, conceptsToRemove, conceptsToActivate);
			
			differences.add(memberDifference);
			
		}
		
		return differences;
		
	}

	public static class QueryTypeRefSetMemberDifference {
		
		public enum Action {
			ADD, REMOVE, ACTIVATE
		}
		
		public static class Diff {
			private Action action;
			private boolean selected = true;
			private String id;
			private String label;
			
			public Diff(Action action, String id, String label) {
				this.action = action;
				this.id = id;
				this.label = label;
			}
			
			public Action getAction() {
				return action;
			}
			
			public String getId() {
				return id;
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
				return action.toString()+" ["+label+"("+id+")] (selected: "+selected+")";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((action == null) ? 0 : action.hashCode());
				result = prime * result
						+ ((id == null) ? 0 : id.hashCode());
				return result;
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
				if (action != other.action)
					return false;
				if (id == null) {
					if (other.id != null)
						return false;
				} else if (!id.equals(other.id))
					return false;
				return true;
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
					diff = diff1.getId().compareTo(diff2.getId());
				}
				
				return diff;
			}
		}
		
		public SnomedRegularRefSet memberRefset;
		public Set<Diff> diffs = Sets.newTreeSet(new DiffComparator());
		
		public QueryTypeRefSetMemberDifference(SnomedRegularRefSet memberRefset,
				Map<String, String> conceptsToAdd, Map<String, String> conceptsToRemove, Map<String, String> conceptsToActivate) {
			super();
			this.memberRefset = memberRefset;
			
			for(String id : conceptsToAdd.keySet()){
				diffs.add(new Diff(Action.ADD, id, conceptsToAdd.get(id)));
			}
			
			for(String id : conceptsToRemove.keySet()){
				diffs.add(new Diff(Action.REMOVE, id, conceptsToRemove.get(id)));
			}
			
			for(String id : conceptsToActivate.keySet()){
				diffs.add(new Diff(Action.ACTIVATE, id, conceptsToActivate.get(id)));
			}
		}

		public SnomedRegularRefSet getMemberRefset() {
			return memberRefset;
		}

		public void setMemberRefset(SnomedRegularRefSet memberRefset) {
			this.memberRefset = memberRefset;
		}
		
		public Set<Diff> getDiffs() {
			return diffs;
		}
		
		public boolean isEmpty() {
			return diffs.isEmpty();
		}
		
		@Override
		public String toString() {
			
			StringBuffer sb = new StringBuffer();
			sb.append(CoreTerminologyBroker.getInstance().adapt(memberRefset).getLabel());
			sb.append(" (");
			for(Diff diff : diffs){
				sb.append(diff);
			}
			sb.append(")");
			
			return sb.toString();
		}
		
		
	}

}