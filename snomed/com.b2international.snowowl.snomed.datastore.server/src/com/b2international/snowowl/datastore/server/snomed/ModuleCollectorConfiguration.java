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
package com.b2international.snowowl.datastore.server.snomed;

import java.util.Collection;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.xtext.util.Pair;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.Sets;

/**
 * POJO used for collecting SNOMED CT module dependencies.
 */
public class ModuleCollectorConfiguration {

	private CDOView view;
	private IBranchPath branchPath;
	private Collection<Pair<String, String>> members = Sets.newHashSet();
	private LongKeyLongMap conceptModuleMapping;
	private LongCollection unpublishedStorageKeys;

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
	
	public Collection<Pair<String, String>> getMembers() {
		return members;
	}
	
	public void setMembers(Collection<Pair<String, String>> members) {
		this.members = members;
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
	
}