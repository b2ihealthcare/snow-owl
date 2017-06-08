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
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndModuleAssigner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Simple assigner that allocates the namespaces and modules for relationships and concrete domains
 * to match the source concept's namespace.
 */
public class SourceConceptNamespaceAndModuleAssigner implements NamespaceAndModuleAssigner {

	private Map<String, Iterator<String>> namespaceToRelationshipIdMap = newHashMap();
	private Map<String, Concept> conceptIdToRelationshipModuleMap = newHashMap();
	private Map<String, Concept> conceptIdToConcreteDomainModuleMap = newHashMap();

	@Override
	public String getRelationshipId(String sourceConceptId) {
		String sourceConceptNamespace = SnomedIdentifiers.create(sourceConceptId).getNamespace();
		return namespaceToRelationshipIdMap.get(sourceConceptNamespace).next();
	}

	@Override
	public Concept getRelationshipModule(String sourceConceptId) {
		return conceptIdToRelationshipModuleMap.get(sourceConceptId);
	}

	@Override
	public Concept getConcreteDomainModule(String sourceConceptId) {
		return conceptIdToConcreteDomainModuleMap.get(sourceConceptId);
	}

	@Override
	public void allocateRelationshipIdsAndModules(Multiset<String> conceptIds, final SnomedEditingContext editingContext) {
		Multiset<String> reservedIdsByNamespace = HashMultiset.create();
		for (Multiset.Entry<String> conceptIdWithCount : conceptIds.entrySet()) {
			String namespace = SnomedIdentifiers.create(conceptIdWithCount.getElement()).getNamespace();
			reservedIdsByNamespace.add(namespace, conceptIdWithCount.getCount());
		}
		
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		for (Multiset.Entry<String> namespaceWithCount : reservedIdsByNamespace.entrySet()) {
			Collection<String> reservedIds = identifierService.reserve(namespaceWithCount.getElement(), ComponentCategory.RELATIONSHIP, namespaceWithCount.getCount());
			namespaceToRelationshipIdMap.put(namespaceWithCount.getElement(), reservedIds.iterator());
		}
		
		SnomedConceptLookupService conceptLookupService = new SnomedConceptLookupService();
		for (String conceptId : conceptIds.elementSet()) {
			Concept concept = conceptLookupService.getComponent(conceptId, editingContext.getTransaction());
			conceptIdToRelationshipModuleMap.put(conceptId, concept.getModule());
		}
	}

	@Override
	public void allocateConcreteDomainModules(Set<String> conceptIds, final SnomedEditingContext editingContext) {
		SnomedConceptLookupService conceptLookupService = new SnomedConceptLookupService();
		for (String conceptId : conceptIds) {
			Concept concept = conceptLookupService.getComponent(conceptId, editingContext.getTransaction());
			conceptIdToConcreteDomainModuleMap.put(conceptId, concept.getModule());
		}
	}
}
