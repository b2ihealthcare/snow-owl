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
package com.b2international.snowowl.datastore.server.snomed;

import java.util.Collection;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;

/**
 * POJO used for collecting SNOMED CT module dependencies.
 */
public class ModuleCollectorConfiguration {

	private CDOView view;
	private IBranchPath branchPath;
	private Collection<SnomedModuleDependencyRefSetMember> members = Sets.newHashSet();
	private Multimap<Long, Long> moduleMapping = HashMultimap.create();
	private LongKeyLongMap conceptModuleMapping;
	private LongCollection unpublishedStorageKeys;
	private Collection<SnomedModuleDependencyRefSetMemberFragment> existingModules = Sets.newHashSet();
	private SnomedRefSet moduleDependencyRefSet;
	
	public CDOView getView() {
		return view;
	}
	
	public void setView(CDOView view) {
		this.view = view;
	}
	
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
	public void setBranchPath(IBranchPath branchPath) {
		this.branchPath = branchPath;
	}
	
	public Collection<SnomedModuleDependencyRefSetMember> getMembers() {
		return members;
	}
	
	public void setMembers(Collection<SnomedModuleDependencyRefSetMember> members) {
		this.members = members;
	}
	
	public Multimap<Long, Long> getModuleMapping() {
		return moduleMapping;
	}
	
	public void setModuleMapping(Multimap<Long, Long> moduleMapping) {
		this.moduleMapping = moduleMapping;
	}
	
	public LongKeyLongMap getConceptModuleMapping() {
		return conceptModuleMapping;
	}
	
	public void setConceptModuleMapping(LongKeyLongMap conceptModuleMapping) {
		this.conceptModuleMapping = conceptModuleMapping;
	}
	
	public LongCollection getUnpublishedStorageKeys() {
		return unpublishedStorageKeys;
	}
	
	public void setUnpublishedStorageKeys(LongCollection unpublishedStorageKeys) {
		this.unpublishedStorageKeys = unpublishedStorageKeys;
	}
	
	public Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules() {
		return existingModules;
	}
	
	public void setExistingModules(Collection<SnomedModuleDependencyRefSetMemberFragment> existingModules) {
		this.existingModules = existingModules;
	}
	
	public SnomedRefSet getModuleDependencyRefSet() {
		return moduleDependencyRefSet;
	}
	
	public void setModuleDependencyRefSet(SnomedRefSet moduleDependencyRefSet) {
		this.moduleDependencyRefSet = moduleDependencyRefSet;
	}
}
