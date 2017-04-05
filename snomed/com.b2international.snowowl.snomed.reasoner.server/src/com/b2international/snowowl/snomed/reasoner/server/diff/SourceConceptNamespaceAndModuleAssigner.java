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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Simple assigner that allocates the namespaces and modules for relationships and concrete domains
 * to match the source concept's namespace.
 *
 */
public class SourceConceptNamespaceAndModuleAssigner implements NamespaceAndMolduleAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceConceptNamespaceAndModuleAssigner.class);

	private Map<String, Concept> conceptIdToRelationshipModuleMap = Maps.newHashMap();
	
	private Map<String, Concept> conceptIdToConcreteDomainModuleMap = Maps.newHashMap();

	private Concept relationshipDefaultModuleConcept;

	private Concept cdDefaultModuleConcept;

	@Override
	public String getRelationshipNamespace(String sourceConceptId, final IBranchPath branchPath) {
		return SnomedIdentifiers.create(sourceConceptId).getNamespace();
	}

	@Override
	public Concept getRelationshipModule(String sourceConceptId, final IBranchPath branchPath) {

		if (!conceptIdToRelationshipModuleMap.containsKey(sourceConceptId)) {
			LOGGER.warn("Could not find the conceptId '{}' in the allocated module map.", sourceConceptId);
			return relationshipDefaultModuleConcept;
		}
		return conceptIdToRelationshipModuleMap.get(sourceConceptId);
	}

	@Override
	public Concept getConcreateDomainModule(String sourceConceptId, IBranchPath branchPath) {
		
		if (!conceptIdToConcreteDomainModuleMap.containsKey(sourceConceptId)) {
			LOGGER.warn("Could not find the conceptId '{}' in the allocated module map.", sourceConceptId);
			return cdDefaultModuleConcept;
		}
		return conceptIdToConcreteDomainModuleMap.get(sourceConceptId);
	}

	@Override
	public void allocateRelationshipNamespacesAndModules(Multimap<String, StatementFragment> conceptIdToPropertiesMap,
			final SnomedEditingContext editingContext) {

		//default module is the fall back
		relationshipDefaultModuleConcept = editingContext.getDefaultModuleConcept();

		SnomedConceptLookupService conceptLookupService = new SnomedConceptLookupService();

		// find the SDD_Ext concepts first as it is the fastest
		Set<String> conceptIds = conceptIdToPropertiesMap.keySet();
		for (String conceptId : conceptIds) {

			Concept concept = conceptLookupService.getComponent(conceptId, editingContext.getTransaction());
			conceptIdToRelationshipModuleMap.put(conceptId, concept.getModule());
		}
	}

	@Override
	public void allocateConcreateDomainModules(Multimap<String, StatementFragment> conceptIdToConcreteDomainMap, final SnomedEditingContext editingContext) {
		
		//default module is the fall back
		cdDefaultModuleConcept = editingContext.getDefaultModuleConcept();

		SnomedConceptLookupService conceptLookupService = new SnomedConceptLookupService();
		
		// find the SDD_Ext concepts first as it is the fastest
		Set<String> conceptIds = conceptIdToConcreteDomainMap.keySet();
		for (String conceptId : conceptIds) {

			Concept concept = conceptLookupService.getComponent(conceptId, editingContext.getTransaction());
			conceptIdToConcreteDomainModuleMap.put(conceptId, concept.getModule());
		}
	}

}
