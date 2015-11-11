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
package com.b2international.snowowl.scripting.services.api.impl;

import java.util.Collection;
import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.scripting.services.api.IRefSetService;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Reference set service implementation for SNOMED&nbsp;CT.
 *
 */
public class SnomedRefSetService implements IRefSetService {

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMembers(final String referenceSetId) {
		return getRefSetBrowser().getActiveMembers(referenceSetId);
	}

	@Override
	public Collection<SnomedRefSetIndexEntry> getAllReferenceSets() {
		return getRefSetBrowser().getAllReferenceSets();
	}

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final String conceptId) {
		return getRefSetBrowser().getActiveReferringMembers(conceptId);
	}

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final String conceptId, final String refSetId) {
		return Lists.newArrayList(Collections2.filter(getReferringMembers(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry input) {
				return input.isActive() && refSetId.equals(input.getRefSetIdentifierId());
			}
		}));
	}

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getReferringSimpleTypeMembers(final String conceptId) {
		return Lists.newArrayList(Collections2.filter(getReferringMembers(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry input) {
				return input.isActive() && SnomedRefSetType.SIMPLE.equals(input.getRefSetType());
			}
		}));
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final String conceptId) {
		return getRefSetBrowser().getMappingMembers(conceptId);
	}

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final String conceptId, final String refSetId) {
		return Lists.newArrayList(Collections2.filter(getMappingMembers(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry input) {
				return input.isActive() && refSetId.equals(input.getRefSetIdentifierId());
			}
		}));
	}

	@Override
	public Map<String, Collection<String>> getMapppings(final String refSetId) {
		return getRefSetBrowser().getMapppings(refSetId);
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getMembersForMapTarget(final String mapTarget, final String mappingRefSetId) {
		return getRefSetBrowser().getMembersForMapTarget(mapTarget, mappingRefSetId);
	}
	
	public Collection<SnomedRefSetMemberIndexEntry> getReferringSimpleTypeMembers(final String conceptId, final String refSetId) {
		return Lists.newArrayList(Collections2.filter(getReferringMembers(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry input) {
				return input.isActive() && SnomedRefSetType.SIMPLE.equals(input.getRefSetType()) && refSetId.equals(input.getRefSetIdentifierId());
			}
		}));
	}
	
	private SnomedClientRefSetBrowser getRefSetBrowser() {
		return ApplicationContext.getInstance().getService(SnomedClientRefSetBrowser.class);
	}
	
}