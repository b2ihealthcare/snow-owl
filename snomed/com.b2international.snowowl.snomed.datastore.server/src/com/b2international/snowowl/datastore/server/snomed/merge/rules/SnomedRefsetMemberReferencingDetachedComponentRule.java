/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.merge.rules;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.cdo.IMergeConflictRule;
import com.b2international.snowowl.datastore.server.snomed.SnomedMergeConflict;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.7
 */
public class SnomedRefsetMemberReferencingDetachedComponentRule implements IMergeConflictRule {

	@Override
	public Collection<MergeConflict> validate(CDOTransaction transaction) {
		
		final Set<String> detachedMemberIds = FluentIterable.from(ComponentUtils2.getDetachedObjects(transaction, SnomedRefSetMember.class)).transform(new Function<SnomedRefSetMember, String>() {
			@Override
			public String apply(SnomedRefSetMember input) {
				return input.getUuid();
			}
		}).toSet();
		
		final Set<String> detachedCoreComponentIds = FluentIterable.from(ComponentUtils2.getDetachedObjects(transaction, Component.class)).transform(new Function<Component, String>() {
			@Override
			public String apply(Component input) {
				return input.getId();
			}
		}).toSet();
		
		List<MergeConflict> conflicts = newArrayList();
		
		if (!detachedCoreComponentIds.isEmpty()) {
			final SnomedReferenceSetMembers membersReferencingDetachedComponents = SnomedRequests
					.prepareSearchMember()
					.filterByReferencedComponent(detachedCoreComponentIds)
					.setLimit(detachedCoreComponentIds.size())
					.build(BranchPathUtils.createPath(transaction).getPath())
					.executeSync(ApplicationContext.getInstance().getService(IEventBus.class));
			
			for (SnomedReferenceSetMember member : membersReferencingDetachedComponents) {
				if (!detachedMemberIds.contains(member.getId())) {
					conflicts.add(new SnomedMergeConflict(member.getId(), member.getReferencedComponent().getId(), String.format("Member '%s' is referencing detached component '%s'", member.getId(), member.getReferencedComponent().getId())));
				}
			}
		}
		
		return conflicts;
	}

}
