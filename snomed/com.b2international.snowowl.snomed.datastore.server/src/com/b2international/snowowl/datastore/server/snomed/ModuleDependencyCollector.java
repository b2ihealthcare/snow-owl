/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 6.3
 */
public final class ModuleDependencyCollector {

	private final RevisionSearcher searcher;

	public ModuleDependencyCollector(RevisionSearcher searcher) {
		this.searcher = searcher;
	}
	
	public Map<String, String> getModuleDependencies(Iterable<Long> storageKeys) throws IOException {
		if (Iterables.isEmpty(storageKeys)) {
			return Collections.emptyMap();
		}

		final Map<String, String> moduleDependencies = newHashMap();
		
		final Multimap<String, String> conceptsByReferringModule = HashMultimap.create();
		collectConceptModuleDependencies(storageKeys, conceptsByReferringModule);
		collectDescriptionModuleDependencies(storageKeys, conceptsByReferringModule);
		collectRelationshipModuleDependencies(storageKeys, conceptsByReferringModule);
		collectMemberModuleDependencies(storageKeys, conceptsByReferringModule);
		
		// iterate over each module and get modules of all dependencies
		for (String module : conceptsByReferringModule.keySet()) {
			final Collection<String> dependencies = conceptsByReferringModule.get(module);
			Query<String[]> dependencyQuery = Query.select(String[].class)
					.from(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.MODULE_ID)
					.where(SnomedConceptDocument.Expressions.ids(dependencies))
					.limit(10000)
					.build();
			for (Hits<String[]> dependencyHits : searcher.scroll(dependencyQuery)) {
				for (String[] idAndModule : dependencyHits) {
					String targetModule = idAndModule[1];
					if (!module.equals(targetModule)) {
						moduleDependencies.put(module, targetModule);
					}
				}
			}
		}
		
		return moduleDependencies;
	}

	private void collectConceptModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> conceptsByReferringModule) throws IOException {
		for (SnomedConceptDocument doc : searcher.get(SnomedConceptDocument.class, storageKeys)) {
			conceptsByReferringModule.put(doc.getModuleId(), doc.isPrimitive() ? Concepts.PRIMITIVE : Concepts.FULLY_DEFINED);
		}
	}
	
	private void collectDescriptionModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> conceptsByReferringModule) throws IOException {
		for (SnomedDescriptionIndexEntry doc : searcher.get(SnomedDescriptionIndexEntry.class, storageKeys)) {
			conceptsByReferringModule.put(doc.getModuleId(), doc.getConceptId());
			conceptsByReferringModule.put(doc.getModuleId(), doc.getTypeId());
			conceptsByReferringModule.put(doc.getModuleId(), doc.getCaseSignificanceId());
		}
	}
	
	private void collectRelationshipModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> conceptsByReferringModule) throws IOException {
		for (SnomedRelationshipIndexEntry doc : searcher.get(SnomedRelationshipIndexEntry.class, storageKeys)) {
			conceptsByReferringModule.put(doc.getModuleId(), doc.getSourceId());
			conceptsByReferringModule.put(doc.getModuleId(), doc.getTypeId());
			conceptsByReferringModule.put(doc.getModuleId(), doc.getDestinationId());
			conceptsByReferringModule.put(doc.getModuleId(), doc.getModifierId());
			conceptsByReferringModule.put(doc.getModuleId(), doc.getCharacteristicTypeId());
		}
	}
	
	private void collectMemberModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> conceptsByReferringModule) throws IOException {
		for (SnomedRefSetMemberIndexEntry doc : searcher.get(SnomedRefSetMemberIndexEntry.class, storageKeys)) {
			conceptsByReferringModule.put(doc.getModuleId(), doc.getReferenceSetId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getAcceptabilityId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getCharacteristicTypeId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getCorrelationId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getDescriptionFormat());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getOperatorId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getValueId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getUnitId());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getTargetComponent());
			registerIfConcept(conceptsByReferringModule, doc.getModuleId(), doc.getMapCategoryId());
		}
	}

	private void registerIfConcept(Multimap<String, String> conceptsByReferringModule, String moduleId, String dependency) {
		if (SnomedIdentifiers.isConceptIdentifier(dependency)) {
			conceptsByReferringModule.put(moduleId, dependency);
		}
	}
	
}
