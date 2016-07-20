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
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.scripting.services.api.IConcreteDomainService;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

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
		throw new UnsupportedOperationException();
//		final Set<String> ids = newHashSet(conceptId);
//		final Collection<SnomedRelationshipIndexEntry> sourceRelationships = statementBrowser.getActiveOutboundStatementsById(conceptId);
//		final String[] activeSourceIds = Iterables.toArray(Iterables.transform(sourceRelationships, new Function<SnomedRelationshipIndexEntry, String>() {
//			@Override public String apply(final SnomedRelationshipIndexEntry relationship) {
//				return relationship.getId();
//			}
//		}), String.class);
//		ids.addAll(Arrays.asList(activeSourceIds));
//		return getConcreteDomains(ids);
	}

	private Collection<SnomedRefSetMemberIndexEntry> getConcreteDomains(final Set<String> ids) {
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByReferencedComponent(ids)
				.filterByRefSetType(Collections.singleton(SnomedRefSetType.CONCRETE_DATA_TYPE))
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedReferenceSetMembers, Collection<SnomedRefSetMemberIndexEntry>>() {
					@Override
					public Collection<SnomedRefSetMemberIndexEntry> apply(SnomedReferenceSetMembers input) {
						return SnomedRefSetMemberIndexEntry.from(input);
					}
				})
				.getSync();
	}
	
	@Override
	public boolean isVaccine(final String conceptId) {
		return 0 < Collections2.filter(getAllDataTypesForConcept(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry dataType) {
				return IS_VACCINE.equals(dataType.getAttributeName()) && Boolean.parseBoolean(String.valueOf(dataType.getValue()));
			}
		}).size();
	}

	@Override
	public boolean isVitamin(final String conceptId) {
		return 0 < Collections2.filter(getAllDataTypesForConcept(conceptId), new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry dataType) {
				return IS_VITAMIN.equals(dataType.getAttributeName()) && Boolean.parseBoolean(String.valueOf(dataType.getValue()));
			}
		}).size();
	}
	
	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getDataTypesForConcept(final String conceptId) {
		return getConcreteDomains(Collections.singleton(conceptId));
	}

	@Override
	public Collection<SnomedRefSetMemberIndexEntry> getDataTypesForRelationship(final String relationshipId) {
		return getConcreteDomains(Collections.singleton(relationshipId));
	}

	@Override
	public String getDataTypeLabel(final String label) {
		return DataTypeUtils.getDefaultDataTypeLabel(label);
	}
	
	@Override
	public Map<String, Boolean> getAllManufacturedConcreteDomains() {
		throw new UnsupportedOperationException();
//		final Multimap<String, Boolean> concreteDomainsForName = // 
//				getServiceForClass(IClientSnomedComponentService.class).<Boolean>getAllConcreteDomainsForName(IS_MANUFACTURED);
//		//we blindly ignore multiple values for the same concrete domains per components
//		final Map<String, Boolean> results = newHashMapWithExpectedSize(concreteDomainsForName.size());
//		for (final String componentId : concreteDomainsForName.keys()) {
//			final Collection<Boolean> values = concreteDomainsForName.get(componentId);
//			if (!isEmpty(values)) {
//				results.put(componentId, get(values, 0));
//			}
//		}
//		
//		return results;
	}

}