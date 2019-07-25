/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.assigner;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;
import java.util.function.Consumer;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Sets;

/**
 * Simple assigner that allocates the namespaces and modules for relationships
 * and concrete domains to match the source concept's namespace.
 * 
 * @since 5.11.5
 */
public final class ExtensionNamespaceAndModuleAssigner implements SnomedNamespaceAndModuleAssigner {
	
	private final LongKeyLongMap relationshipModuleMap = PrimitiveMaps.newLongKeyLongOpenHashMap();
	private final LongKeyLongMap concreteDomainModuleMap = PrimitiveMaps.newLongKeyLongOpenHashMap();
	private String defaultNamespace;
	private String defaultModuleId;
	private Set<String> internationalModuleIds = Sets.newHashSet();
	
	// Empty constructor required for executable extension-based initialization 
	public ExtensionNamespaceAndModuleAssigner() { }
	
	@Override
	public String getRelationshipNamespace(final String sourceConceptId) {
		String namespace = SnomedIdentifiers.getNamespace(sourceConceptId);
		return namespace.isEmpty() ? defaultNamespace : namespace;
	}

	@Override
	public String getRelationshipModuleId(final String sourceConceptId) {
		final long sourceConceptIdAsLong = Long.parseLong(sourceConceptId);
		checkArgument(relationshipModuleMap.containsKey(sourceConceptIdAsLong), "The relationship module ID for '%s' was not collected.", sourceConceptId);
		final String moduleId = Long.toString(relationshipModuleMap.get(sourceConceptIdAsLong));
		
		if (internationalModuleIds.contains(moduleId)) {
			return defaultModuleId;
		}
		return moduleId;
	}

	@Override
	public String getConcreteDomainModuleId(final String referencedConceptId) {
		final long referencedConceptIdAsLong = Long.parseLong(referencedConceptId);
		checkArgument(concreteDomainModuleMap.containsKey(referencedConceptIdAsLong), "The concrete domain member module ID for '%s' was not collected.", referencedConceptId);
		final String moduleId = Long.toString(concreteDomainModuleMap.get(referencedConceptIdAsLong));
		
		if (internationalModuleIds.contains(moduleId)) {
			return defaultModuleId;
		}
		return moduleId;
	}

	@Override
	public void collectRelationshipNamespacesAndModules(final Set<String> conceptIds, final BranchContext context) {
		relationshipModuleMap.clear();

		collectModules(conceptIds, context, c -> {
			final long conceptId = Long.parseLong(c.getId());
			final long moduleId = Long.parseLong(c.getModuleId());
			relationshipModuleMap.put(conceptId, moduleId);
		});
		
		defaultNamespace = context.service(SnomedCoreConfiguration.class).getDefaultNamespace();
		defaultModuleId = context.service(SnomedCoreConfiguration.class).getDefaultModule();
		initializeInternationalModules(context);
	}

	@Override
	public void collectConcreteDomainModules(final Set<String> conceptIds, final BranchContext context) {
		concreteDomainModuleMap.clear();

		collectModules(conceptIds, context, c -> {
			final long conceptId = Long.parseLong(c.getId());
			final long moduleId = Long.parseLong(c.getModuleId());
			concreteDomainModuleMap.put(conceptId, moduleId);
		});
		
		defaultModuleId = context.service(SnomedCoreConfiguration.class).getDefaultModule();
		initializeInternationalModules(context);
	}
	
	private void initializeInternationalModules(BranchContext context) {
		if (internationalModuleIds.isEmpty()) {
			SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.filterByEcl(String.format("<<%s", SnomedConstants.Concepts.IHTSDO_MAINTAINED_MODULE))
				.build()
				.execute(context)
				.forEach(concept -> internationalModuleIds.add(concept.getId()));
		}
	}
	
	private void collectModules(final Set<String> conceptIds, final BranchContext context, final Consumer<SnomedConcept> consumer) {
		SnomedRequests.prepareSearchConcept()
			.setLimit(conceptIds.size())
			.filterByIds(conceptIds)
			.setFields(SnomedComponentDocument.Fields.ID, SnomedComponentDocument.Fields.MODULE_ID)
			.build()
			.execute(context)
			.forEach(consumer);
	}
	
	@Override
	public void clear() {
		relationshipModuleMap.clear();
		concreteDomainModuleMap.clear();
	}

	@Override
	public String getName() {
		return "extension";
	}
}
