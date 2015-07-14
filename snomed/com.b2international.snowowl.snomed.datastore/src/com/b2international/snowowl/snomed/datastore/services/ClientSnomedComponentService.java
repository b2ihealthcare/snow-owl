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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService.IdStorageKeyPair;
import com.b2international.snowowl.snomed.mrcm.DataType;
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
	public Map<String, Integer> getAvailableDescriptionTypeIdsWithLength() {
		return wrappedService.getAvailableDescriptionTypeIdsWithLength(getBranchPath());
	}

	@Override
	public Set<String> getAvailableDescriptionTypeIds() {
		return wrappedService.getAvailableDescriptionTypeIds(getBranchPath());
	}

	@Override
	public Set<String> getAvailablePreferredTermIds() {
		return wrappedService.getAvailablePreferredTermIds(getBranchPath());
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
	public boolean isPreferred(final Description description) {
		return wrappedService.isPreferred(getBranchPath(), description);
	}

	@Override
	public long getExtensionConceptId(final String componentId) {
		return wrappedService.getExtensionConceptId(getBranchPath(), componentId);
	}

	@Override
	public String[] getLabels(final String... componentIds) {
		return wrappedService.getLabels(getBranchPath(), componentIds);
	}

	@Override
	public String[] getIconId(final String... conceptId) {
		return wrappedService.getIconId(getBranchPath(), conceptId);
	}

	@Override
	public boolean descriptionExists(final String descriptionId) {
		return wrappedService.descriptionExists(getBranchPath(), descriptionId);
	}

	@Override
	public boolean relationshipExists(final String relationshipId) {
		return wrappedService.relationshipExists(getBranchPath(), relationshipId);
	}

	@Override
	public boolean componentExists(final String componentId) {
		return wrappedService.componentExists(getBranchPath(), componentId);
	}

	@Override
	public Collection<IdStorageKeyPair> getAllComponentIdStorageKeys(final short terminologyComponentId) {
		return wrappedService.getAllComponentIdStorageKeys(getBranchPath(), terminologyComponentId);
	}

	@Override
	public Collection<IdStorageKeyPair> getAllMemberIdStorageKeys(final int refSetTypeOrdinal) {
		return wrappedService.getAllMemberIdStorageKeys(getBranchPath(), refSetTypeOrdinal);
	}

	@Override
	public LongSet getAllReferringMembersStorageKey(final String componentId, final int typeOrdinal, final int... otherTypeOrdinal) {
		return wrappedService.getAllReferringMembersStorageKey(getBranchPath(), componentId, typeOrdinal, otherTypeOrdinal);
	}

	@Override
	public LongSet getAllDescriptionIds() {
		return wrappedService.getAllDescriptionIds(getBranchPath());
	}

	@Override
	public Collection<SnomedDescriptionFragment> getDescriptionFragmentsForConcept(final String conceptId, final String languageRefSetId) {
		return wrappedService.getDescriptionFragmentsForConcept(getBranchPath(), conceptId, languageRefSetId);
	}
	
	@Override
	public LongSet getAllRefSetIds() {
		return wrappedService.getAllRefSetIds(getBranchPath());
	}
	
	@Override
	public Map<String, String> getReferencedConceptTerms(final String refSetId, final String... descriptionTypeId) {
		return wrappedService.getReferencedConceptTerms(getBranchPath(), refSetId, descriptionTypeId);
	}
	
	@Override
	public Collection<SnomedRefSetMemberFragment> getRefSetMemberFragments(final String refSetId) {
		return wrappedService.getRefSetMemberFragments(getBranchPath(), checkNotNull(refSetId, "refSetId"));
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
	public Map<String, Date> getExistingModulesWithEffectiveTime() {
		return wrappedService.getExistingModulesWithEffectiveTime(getBranchPath());
	}
	
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}