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
package com.b2international.snowowl.snomed.core.refset;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.Lists;

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
		final SnomedReferenceSetMembers originalMembers = SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(originalRefSetId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, editingContext.getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		loadChildMonitor.worked(1);

		SubMonitor cloneChildMonitor = subMonitor.newChild(1);
		cloneChildMonitor.setTaskName("Cloning reference set members...");
		cloneChildMonitor.setWorkRemaining(originalMembers.getTotal());
		List<SnomedRefSetMember> newMembers = Lists.newArrayList();
		
		for (SnomedReferenceSetMember originalMember : originalMembers) {
			final String referencedComponentId = originalMember.getReferencedComponent().getId();
			SnomedRefSetMember newRefSetMember;
			switch (originalRefSetType) {
			case ATTRIBUTE_VALUE:
				final String valueId = (String) originalMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID);
				newRefSetMember = editingContext.createAttributeValueRefSetMember(referencedComponentId, valueId, originalMember.getModuleId(), cloneRefSet);
				break;
			case QUERY:
				final String query = (String) originalMember.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
				newRefSetMember = editingContext.createQueryRefSetMember(referencedComponentId, query, originalMember.getModuleId(), cloneRefSet);
				break;
			case SIMPLE:
				newRefSetMember = editingContext.createSimpleTypeRefSetMember(referencedComponentId, originalMember.getModuleId(), cloneRefSet);
				break;
			case COMPLEX_MAP:
				String mapTargetId = (String) originalMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET);
				SnomedComplexMapRefSetMember newComplexMapRefSetMember = editingContext.createComplexMapRefSetMember(referencedComponentId, mapTargetId, originalMember.getModuleId(), (SnomedMappingRefSet) cloneRefSet);
				newComplexMapRefSetMember.setCorrelationId((String) originalMember.getProperties().get(Fields.CORRELATION_ID));
				newComplexMapRefSetMember.setMapAdvice((String) originalMember.getProperties().get(Fields.MAP_ADVICE));
				newComplexMapRefSetMember.setMapGroup((Integer) originalMember.getProperties().get(Fields.MAP_GROUP));
				newComplexMapRefSetMember.setMapPriority((Integer) originalMember.getProperties().get(Fields.MAP_PRIORITY));
				newComplexMapRefSetMember.setMapRule((String) originalMember.getProperties().get(Fields.MAP_RULE));
				newRefSetMember = newComplexMapRefSetMember;
				break;
			case SIMPLE_MAP:
				mapTargetId = (String) originalMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET);
				newRefSetMember = editingContext.createSimpleMapRefSetMember(referencedComponentId, mapTargetId, originalMember.getModuleId(), (SnomedMappingRefSet) cloneRefSet);
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