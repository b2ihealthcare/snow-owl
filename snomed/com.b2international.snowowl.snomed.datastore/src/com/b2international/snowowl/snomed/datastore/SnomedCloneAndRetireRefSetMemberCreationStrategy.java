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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.copy;
import static com.b2international.snowowl.datastore.cdo.IMutableAdapter.EMPTY_IMPL;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.emf.ecore.util.EcoreUtil.remove;

import java.util.List;
import java.util.UUID;

import org.eclipse.emf.ecore.util.InternalEList;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;

/**
 * Reference set member creation strategy that takes a reference set, creates a copy,
 * removes all map target related information from the copied one and adds it to the reference set.
 * Also removes or retires the original reference set.
 *
 */
public class SnomedCloneAndRetireRefSetMemberCreationStrategy implements SnomedRefSetMemberCreationStrategy {

	private SnomedEditingContext context;
	private SnomedRefSet refSet;
	private SnomedRefSetMember originalMember;
	private SnomedRefSetMember copy;

	public SnomedCloneAndRetireRefSetMemberCreationStrategy(final SnomedRefSetEditingContext context, 
			final SnomedRefSetMember originalMember) throws SnowowlServiceException {
		
		this.originalMember = checkNotNull(originalMember, "originalMember");
		this.context = context.snomedEditingContext;
		refSet = this.originalMember.getRefSet();
		copy = initializeCopyMember();
	}

	@Override
	public SnomedRefSetMember getRefSetMember() {
		return copy;
	}

	@Override
	public SnomedRefSet getRefSet() {
		return refSet;
	}

	@Override
	public SnomedEditingContext getEditingContext() {
		return context;
	}

	@Override
	public void doCreate() {

		deleteOrRetireOriginalMember();
		
		final List<SnomedRefSetMember> members = ((SnomedRegularRefSet) refSet).getMembers();
		((InternalEList<SnomedRefSetMember>) members).addUnique(copy);
		
		// Send signal for refresh
		refSet.eNotify(EMPTY_IMPL);
		
	}

	private void deleteOrRetireOriginalMember() {
		if (originalMember.isReleased()) {
			originalMember.setActive(false);
		} else {
			remove(originalMember);
		}
	}
	
	private SnomedRefSetMember initializeCopyMember() throws SnowowlServiceException {
		final SnomedRefSetMember copy = copy(this.originalMember, this.context, false, false);
		if (copy instanceof SnomedSimpleMapRefSetMember) {
			((SnomedSimpleMapRefSetMember) copy).setMapTargetComponentId(null);
			((SnomedSimpleMapRefSetMember) copy).setMapTargetComponentDescription(null);
		}
		copy.setReleased(false);
		copy.setActive(true);
		copy.unsetEffectiveTime();
		copy.setUuid(UUID.randomUUID().toString());
		return copy;
	}

}