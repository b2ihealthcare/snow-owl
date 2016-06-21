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
package com.b2international.snowowl.snomed.refset.clone;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.Lists;
import com.google.common.primitives.SignedBytes;

/**
 * Clones the members of a given {@link SnomedRefSet reference set}.
 * 
 */
public class SnomedRefSetCloner {
	
	private final SnomedRefSetEditingContext editingContext;
	
	public SnomedRefSetCloner(SnomedRefSetEditingContext editingContext) {
		this.editingContext = editingContext;
	}

	/**
	 * Loads the members of the original reference set from the index and clones them into new {@link SnomedRefSetMember} instances.
	 * @param cloneRefSet 
	 * 
	 * @param originalRefSetId the identifier of the reference set to clone
	 * @param originalRefSetType the type of the reference set to clone
	 * @param monitor the progress monitor
	 * @return
	 */
	public List<SnomedRefSetMember> cloneRefSetMembers(SnomedRegularRefSet cloneRefSet, String originalRefSetId, SnomedRefSetType originalRefSetType, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		SubMonitor loadChildMonitor = subMonitor.newChild(1);
		loadChildMonitor.setTaskName("Loading reference set members...");
		loadChildMonitor.setWorkRemaining(1);
		SnomedClientIndexService indexService = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		SnomedRefSetMemberIndexQueryAdapter originalMembersQueryAdapter = new SnomedRefSetMemberIndexQueryAdapter((String) originalRefSetId, "");
		List<SnomedRefSetMemberIndexEntry> originalMembers = indexService.search(originalMembersQueryAdapter);
		loadChildMonitor.worked(1);

		SubMonitor cloneChildMonitor = subMonitor.newChild(1);
		cloneChildMonitor.setTaskName("Cloning reference set members...");
		cloneChildMonitor.setWorkRemaining(originalMembers.size());
		List<SnomedRefSetMember> newMembers = Lists.newArrayList();
		
		for (SnomedRefSetMemberIndexEntry originalMemberIndexEntry : originalMembers) {
			ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.<String>create(
					originalMemberIndexEntry.getReferencedComponentType(), originalMemberIndexEntry.getReferencedComponentId());
			SnomedRefSetMember newRefSetMember;
			switch (originalRefSetType) {
			case ATTRIBUTE_VALUE:
				ComponentIdentifierPair<String> specialFieldPair = ComponentIdentifierPair.<String>create(SnomedTerminologyComponentConstants.CONCEPT, originalMemberIndexEntry.getValueId());
				newRefSetMember = editingContext.createAttributeValueRefSetMember(identifierPair, specialFieldPair, originalMemberIndexEntry.getModuleId(), cloneRefSet);
				break;
			case QUERY:
				newRefSetMember = editingContext.createQueryRefSetMember(identifierPair, originalMemberIndexEntry.getQuery(), originalMemberIndexEntry.getModuleId(), cloneRefSet);
				break;
			case SIMPLE:
				newRefSetMember = editingContext.createSimpleTypeRefSetMember(identifierPair, originalMemberIndexEntry.getModuleId(), cloneRefSet);
				break;
			case COMPLEX_MAP:
				String mapTargetId = originalMemberIndexEntry.getMapTargetComponentId();
				String mapTargetComponentType = originalMemberIndexEntry.getMapTargetComponentType();
				specialFieldPair = ComponentIdentifierPair.<String>createWithUncheckedComponentId(mapTargetComponentType, mapTargetId);
				SnomedComplexMapRefSetMember newComplexMapRefSetMember = editingContext.createComplexMapRefSetMember(identifierPair, specialFieldPair, originalMemberIndexEntry.getModuleId(), (SnomedMappingRefSet) cloneRefSet);
				SnomedRefSetMemberIndexEntry complexMapRefSetMemberIndexEntry = (SnomedRefSetMemberIndexEntry) originalMemberIndexEntry;
				newComplexMapRefSetMember.setCorrelationId(complexMapRefSetMemberIndexEntry.getCorrelationId());
				newComplexMapRefSetMember.setMapAdvice(complexMapRefSetMemberIndexEntry.getMapAdvice());
				newComplexMapRefSetMember.setMapGroup(SignedBytes.checkedCast(complexMapRefSetMemberIndexEntry.getMapGroup()));
				newComplexMapRefSetMember.setMapPriority(SignedBytes.checkedCast(complexMapRefSetMemberIndexEntry.getMapPriority()));
				newComplexMapRefSetMember.setMapRule(complexMapRefSetMemberIndexEntry.getMapRule());
				newRefSetMember = newComplexMapRefSetMember;
				break;
			case SIMPLE_MAP:
				specialFieldPair = ComponentIdentifierPair.<String>create(originalMemberIndexEntry.getMapTargetComponentType(), originalMemberIndexEntry.getMapTargetComponentId());
				newRefSetMember = editingContext.createSimpleMapRefSetMember(identifierPair, specialFieldPair, originalMemberIndexEntry.getModuleId(), (SnomedMappingRefSet) cloneRefSet);
				break;
			default:
				throw new RuntimeException("Unhandled reference set type: " + originalRefSetType); 
			}
			newMembers.add(newRefSetMember);
			cloneChildMonitor.worked(1);
		}
		
		return newMembers;
	}
}