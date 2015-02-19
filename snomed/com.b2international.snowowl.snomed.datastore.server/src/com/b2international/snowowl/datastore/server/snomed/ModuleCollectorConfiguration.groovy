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
package com.b2international.snowowl.datastore.server.snomed

import static com.google.common.collect.HashMultimap.create

import org.eclipse.emf.cdo.view.CDOView

import bak.pcj.LongCollection
import bak.pcj.map.LongKeyLongMap

import com.b2international.snowowl.core.api.IBranchPath
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet
import com.google.common.collect.Multimap

/**
 * POJO used for collecting SNOMED&nbsp;CT module dependencies.
 */
class ModuleCollectorConfiguration {

	CDOView view
	IBranchPath branchPath
	Collection<SnomedModuleDependencyRefSetMember> members = [] as Set
	Multimap<Long, Long> moduleMapping = create()
	LongKeyLongMap conceptModuleMapping
	LongCollection unpublishedStorageKeys
	Collection<SnomedModuleDependencyRefSetMemberFragment> existingModules = [] as Set
	SnomedRefSet moduleDependencyRefSet
	
}
