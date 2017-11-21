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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndModuleAssigner;
import com.google.common.collect.Multiset;

/**
 * Simple assigner that allocates the default namespace and module for relationships and concrete domains.
 * 
 * @since 5.11.5
 */
public class DefaultNamespaceAndModuleAssigner implements NamespaceAndModuleAssigner {

	private Iterator<String> relationshipIds;
	private Concept defaultRelationshipModuleConcept;
	private Concept defaultConcreteDomainModuleConcept;

	@Override
	public String getRelationshipId(String sourceConceptId) {
		return relationshipIds.next();
	}

	@Override
	public Concept getRelationshipModule(String sourceConceptId) {
		return defaultRelationshipModuleConcept;
	}

	@Override
	public Concept getConcreteDomainModule(String sourceConceptId) {
		return defaultConcreteDomainModuleConcept;
	}

	@Override
	public void allocateRelationshipIdsAndModules(Multiset<String> conceptIds, final SnomedEditingContext editingContext) {
		if (conceptIds.isEmpty()) return;
		
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		String defaultNamespace = editingContext.getDefaultNamespace();
		Collection<String> reservedIds = identifierService.reserve(defaultNamespace, ComponentCategory.RELATIONSHIP, conceptIds.size());

		relationshipIds = reservedIds.iterator();
		defaultRelationshipModuleConcept = editingContext.getDefaultModuleConcept();
	}

	@Override
	public void allocateConcreteDomainModules(Set<String> conceptIds, final SnomedEditingContext editingContext) {
		defaultConcreteDomainModuleConcept = editingContext.getDefaultModuleConcept();
	}
}
