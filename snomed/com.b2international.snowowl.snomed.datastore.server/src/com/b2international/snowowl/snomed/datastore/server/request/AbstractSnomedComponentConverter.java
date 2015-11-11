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

import java.util.Collection;
import java.util.Date;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponent;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractSnomedComponentConverter<F extends SnomedIndexEntry, T extends ISnomedComponent> implements Function<F, T> {

	private final AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService;

	public AbstractSnomedComponentConverter(AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService) {
		this.refSetMembershipLookupService = refSetMembershipLookupService;
	}

	protected final Date toEffectiveTime(final long effectiveTimeAsLong) {
		return EffectiveTimes.toDate(effectiveTimeAsLong);
	}

	protected final AbstractSnomedRefSetMembershipLookupService getRefSetMembershipLookupService() {
		return refSetMembershipLookupService;
	}
	
	protected final Multimap<AssociationType, String> toAssociationTargets(final String type, final String id) {
		final ImmutableMultimap.Builder<AssociationType, String> resultBuilder = ImmutableMultimap.builder();
	
		for (final AssociationType associationType : AssociationType.values()) {
			// TODO: it might be quicker to collect the refset IDs first and retrieve all members with a single call
			final Collection<SnomedRefSetMemberIndexEntry> members = getRefSetMembershipLookupService().getMembers(
					type,
					ImmutableList.of(associationType.getConceptId()),
					id);
	
			for (final SnomedRefSetMemberIndexEntry member : members) {
				// FIXME: inactive inactivation indicators are shown in the desktop form UI
				if (member.isActive()) {
					resultBuilder.put(associationType, member.getTargetComponentId());
				}
			}
		}
	
		return resultBuilder.build();
	}

}