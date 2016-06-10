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
package com.b2international.snowowl.snomed.datastore.services;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Multimap;

/**
 * Client side SNOMED CT component service implementation.
 */
@Client
public class ClientSnomedComponentService extends ActiveBranchPathAwareService implements IClientSnomedComponentService {

	private final ISnomedComponentService wrappedService;

	public ClientSnomedComponentService(final ISnomedComponentService wrappedService) {
		this.wrappedService = wrappedService;
	}

	@Override
	public void warmCache() {
		wrappedService.warmCache(getBranchPath());
	}

	@Override
	public Set<String> getAvailableDataTypeLabels(final DataType dataType) {
		return wrappedService.getAvailableDataTypeLabels(getBranchPath(), dataType);
	}

	@Override
	public Set<String> getSynonymAndDescendantIds() {
		return wrappedService.getSynonymAndDescendantIds(getBranchPath());
	}

	@Override
	public LongSet getAllReferringMembersStorageKey(final String componentId, final EnumSet<SnomedRefSetType> types) {
		return wrappedService.getAllReferringMembersStorageKey(getBranchPath(), componentId, types);
	}

	@Override
	@Deprecated
	public Map<String, String> getReferencedConceptTerms(final String refSetId, final String... descriptionTypeId) {
		return wrappedService.getReferencedConceptTerms(getBranchPath(), refSetId, descriptionTypeId);
	}
	
	@Override
	public <V> Multimap<String, V> getAllConcreteDomainsForName(final String concreteDomainName) {
		return wrappedService.getAllConcreteDomainsForName(getBranchPath(), concreteDomainName);
	}
	
	@Override
	public Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules() {
		return wrappedService.getExistingModules(getBranchPath());
	}
	
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}