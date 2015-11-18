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

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Collection;
import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.scripting.services.api.IConcreteDomainService;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.IClientSnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Concrete domain service implementation.
 *
 */
public class ConcreteDomainService implements IConcreteDomainService {

	private static final String IS_MANUFACTURED = "isManufactured";
	private static final String IS_VACCINE = "isVaccine";
	private static final String IS_VITAMIN = "isVitamin";

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getAllDataTypesForConcept(final String conceptId) {
		
		final Collection<SnomedRefSetMemberIndexEntry> $ = Lists.newArrayList();
		$.addAll(new SnomedRefSetMembershipLookupService().getConceptDataTypes(conceptId));
		final Collection<SnomedRelationshipIndexEntry> sourceRelationships = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class).getActiveOutboundStatementsById(conceptId);
		final String[] activeSourceIds = Iterables.toArray(Iterables.transform(sourceRelationships, new Function<SnomedRelationshipIndexEntry, String>() {
			@Override public String apply(final SnomedRelationshipIndexEntry relationship) {
				return relationship.getId();
			}
		}), String.class);
		$.addAll(new SnomedRefSetMembershipLookupService().getRelationshipDataTypes(activeSourceIds));
		return $;
	}
	
	@Override
	public boolean isVaccine(final String conceptId) {
		return 0 < Collections2.filter(getAllDataTypesForConcept(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry dataType) {
				return IS_VACCINE.equals(dataType.getLabel()) && Boolean.parseBoolean(String.valueOf(dataType.getValue()));
			}
		}).size();
	}

	@Override
	public boolean isVitamin(final String conceptId) {
		return 0 < Collections2.filter(getAllDataTypesForConcept(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry dataType) {
				return IS_VITAMIN.equals(dataType.getLabel()) && Boolean.parseBoolean(String.valueOf(dataType.getValue()));
			}
		}).size();
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getDataTypesForConcept(final String conceptId) {
		return Lists.newArrayList(new SnomedRefSetMembershipLookupService().getConceptDataTypes(conceptId));
	}

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getDataTypesForRelationship(final String relationshipId) {
		return Lists.newArrayList(new SnomedRefSetMembershipLookupService().getRelationshipDataTypes(relationshipId));
	}

	@Override
	public String getDataTypeLabel(final String label) {
		return DataTypeUtils.getDefaultDataTypeLabel(label);
	}
	
	@Override
	public Map<String, Boolean> getAllManufacturedConcreteDomains() {
		final Multimap<String, Boolean> concreteDomainsForName = // 
				getServiceForClass(IClientSnomedComponentService.class).<Boolean>getAllConcreteDomainsForName(IS_MANUFACTURED);
		//we blindly ignore multiple values for the same concrete domains per components
		final Map<String, Boolean> results = newHashMapWithExpectedSize(concreteDomainsForName.size());
		for (final String componentId : concreteDomainsForName.keys()) {
			final Collection<Boolean> values = concreteDomainsForName.get(componentId);
			if (!isEmpty(values)) {
				results.put(componentId, get(values, 0));
			}
		}
		
		return results;
	}

}