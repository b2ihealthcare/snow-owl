/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.diff;

import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;

/**
 * Simple assigner that allocates the default namespace and module for relationships and concrete domains.
 */
public class DefaultNamespaceAndModuleAssigner implements NamespaceAndMolduleAssigner {

	private Concept defaultRelationshipModuleConcept;

	private Concept defaultConcreteDomainModuleConcept;

	@Override
	public String getRelationshipNamespace(String sourceConceptId, final IBranchPath branchPath) {
		return SnomedEditingContext.getDefaultNamespace();
	}

	@Override
	public Concept getRelationshipModule(String sourceConceptId, final IBranchPath branchPath) {
		return defaultRelationshipModuleConcept;
	}

	@Override
	public Concept getConcreteDomainModule(String sourceConceptId, IBranchPath branchPath) {
		return defaultConcreteDomainModuleConcept;
	}

	@Override
	public void allocateRelationshipNamespacesAndModules(Set<String> conceptIds, final SnomedEditingContext editingContext) {
		
		defaultRelationshipModuleConcept = editingContext.getDefaultModuleConcept();
	}

	@Override
	public void allocateConcreteDomainModules(Set<String> conceptIds, final SnomedEditingContext editingContext) {
		
		defaultConcreteDomainModuleConcept = editingContext.getDefaultModuleConcept();
	}

}
