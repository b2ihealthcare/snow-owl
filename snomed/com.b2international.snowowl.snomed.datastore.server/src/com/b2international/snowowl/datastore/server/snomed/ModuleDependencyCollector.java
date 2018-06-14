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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 6.3
 */
public final class ModuleDependencyCollector {

	private static final List<Class<? extends SnomedComponentDocument>> CORE_COMPONENT_TYPES = ImmutableList.of(
		SnomedConceptDocument.class,
		SnomedDescriptionIndexEntry.class, 
		SnomedRelationshipIndexEntry.class
	);
	private final RevisionSearcher searcher;

	public ModuleDependencyCollector(RevisionSearcher searcher) {
		this.searcher = searcher;
	}
	
	public Multimap<String, String> getModuleDependencies(Iterable<Long> storageKeys) throws IOException {
		if (Iterables.isEmpty(storageKeys)) {
			return ImmutableMultimap.of();
		}

		final Multimap<String, String> moduleDependencies = HashMultimap.create();

		final Multimap<String, String> componentIdsByReferringModule = HashMultimap.create();
		collectConceptModuleDependencies(storageKeys, componentIdsByReferringModule);
		collectDescriptionModuleDependencies(storageKeys, componentIdsByReferringModule);
		collectRelationshipModuleDependencies(storageKeys, componentIdsByReferringModule);
		collectMemberModuleDependencies(storageKeys, componentIdsByReferringModule);
		
		// iterate over each module and get modules of all components registered to componentsByReferringModule
		for (String module : ImmutableSet.copyOf(componentIdsByReferringModule.keySet())) {
			final Collection<String> dependencies = componentIdsByReferringModule.removeAll(module);
			for (Class<? extends SnomedComponentDocument> type : CORE_COMPONENT_TYPES) {
				Query<String[]> dependencyQuery = Query.select(String[].class)
						.from(type)
						.fields(SnomedComponentDocument.Fields.ID, SnomedComponentDocument.Fields.MODULE_ID)
						.where(SnomedComponentDocument.Expressions.ids(dependencies))
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
		}
		
		return moduleDependencies;
	}

	private void collectConceptModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> componentIdsByReferringModule) throws IOException {
		for (SnomedConceptDocument doc : searcher.search(queryByStorageKey(SnomedConceptDocument.class, storageKeys))) {
			componentIdsByReferringModule.put(doc.getModuleId(), doc.isPrimitive() ? Concepts.PRIMITIVE : Concepts.FULLY_DEFINED);
		}
	}
	
	private <T extends RevisionDocument> Query<T> queryByStorageKey(Class<T> type, Iterable<Long> storageKeys) {
		return Query.select(type).where(Expressions.matchAnyLong(RevisionDocument.Fields.STORAGE_KEY, storageKeys)).limit(Integer.MAX_VALUE).build();
	}

	private void collectDescriptionModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> componentIdsByReferringModule) throws IOException {
		for (SnomedDescriptionIndexEntry doc : searcher.search(queryByStorageKey(SnomedDescriptionIndexEntry.class, storageKeys))) {
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getConceptId());
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getTypeId());
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getCaseSignificanceId());
		}
	}
	
	private void collectRelationshipModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> componentIdsByReferringModule) throws IOException {
		for (SnomedRelationshipIndexEntry doc : searcher.search(queryByStorageKey(SnomedRelationshipIndexEntry.class, storageKeys))) {
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getSourceId());
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getTypeId());
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getDestinationId());
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getModifierId());
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getCharacteristicTypeId());
		}
	}
	
	private void collectMemberModuleDependencies(Iterable<Long> storageKeys, Multimap<String, String> componentIdsByReferringModule) throws IOException {
		for (SnomedRefSetMemberIndexEntry doc : searcher.search(queryByStorageKey(SnomedRefSetMemberIndexEntry.class, storageKeys))) {
			componentIdsByReferringModule.put(doc.getModuleId(), doc.getReferenceSetId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getReferencedComponentId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getAcceptabilityId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getCharacteristicTypeId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getCorrelationId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getDescriptionFormat());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getOperatorId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getValueId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getUnitId());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getTargetComponent());
			registerIfConcept(componentIdsByReferringModule, doc.getModuleId(), doc.getMapCategoryId());
		}
	}

	private void registerIfConcept(Multimap<String, String> componentIdsByReferringModule, String moduleId, String dependency) {
		if (SnomedIdentifiers.isValid(dependency)) {
			componentIdsByReferringModule.put(moduleId, dependency);
		}
	}
	
}
