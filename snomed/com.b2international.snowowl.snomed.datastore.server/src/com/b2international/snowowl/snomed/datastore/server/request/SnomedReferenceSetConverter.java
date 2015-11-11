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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetImpl;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Function;

/**
 * @since 4.5
 */
class SnomedReferenceSetConverter implements Function<SnomedRefSetIndexEntry, SnomedReferenceSet> {

	private final List<String> expansions;
	private final BranchContext context;

	public SnomedReferenceSetConverter(BranchContext context) {
		this(context, null);
	}
	
	public SnomedReferenceSetConverter(BranchContext context, List<String> expansions) {
		this.context = context;
		this.expansions = expansions == null ? Collections.<String>emptyList() : expansions;
	}
	
	@Override
	public SnomedReferenceSet apply(SnomedRefSetIndexEntry entry) {
		final SnomedReferenceSetImpl refset = new SnomedReferenceSetImpl();
		refset.setId(entry.getId());
		refset.setEffectiveTime(EffectiveTimes.toDate(entry.getEffectiveTimeAsLong()));
		refset.setActive(entry.isActive());
		refset.setReleased(entry.isReleased());
		refset.setModuleId(entry.getModuleId());
		final short referencedComponentType = entry.getReferencedComponentType();
		refset.setReferencedComponent(getReferencedComponentType(referencedComponentType));
		refset.setType(entry.getType());
		expand(refset);
		return refset;
	}

	public SnomedReferenceSet apply(SnomedRefSet refSet, ISnomedConcept concept) {
		final SnomedReferenceSetImpl refset = new SnomedReferenceSetImpl();
		refset.setId(concept.getId());
		refset.setEffectiveTime(concept.getEffectiveTime());
		refset.setActive(concept.isActive());
		refset.setReleased(concept.isReleased());
		refset.setModuleId(concept.getModuleId());
		final short referencedComponentType = refSet.getReferencedComponentType();
		refset.setReferencedComponent(getReferencedComponentType(referencedComponentType));
		refset.setType(refSet.getType());
		expand(refset);
		return refset;
	}

	private String getReferencedComponentType(final short referencedComponentType) {
		return CoreTerminologyBroker.getInstance().getComponentInformation(referencedComponentType).getId();
	}
	
	private void expand(final SnomedReferenceSetImpl refset) {
		if (expansions.contains("members")) {
			refset.setMembers(SnomedRequests.prepareMemberSearch().all().filterByRefSet(refset.getId()).build().execute(context));
		}
	}
	
}
