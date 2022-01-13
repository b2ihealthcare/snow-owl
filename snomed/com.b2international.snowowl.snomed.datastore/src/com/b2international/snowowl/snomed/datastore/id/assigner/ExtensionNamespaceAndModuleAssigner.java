/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Sets;

/**
 * Simple namespace-module assigner applicable for extension authoring.
 * <p>
 * Relationship <b>namespaces</b> match the source concept's namespace, unless
 * the source concept's module is not in the current code system's module list,
 * in which case the specified <code>defaultNamespace</code> value will be used.
 * <p>
 * Relationship and concrete domain <b>modules</b> match the source concept's
 * module, unless it is not in the current code system's module list, in which
 * case the specified <code>defaultModule</code> value is used.
 * 
 * @since 5.11.5
 */
@Component
public final class ExtensionNamespaceAndModuleAssigner implements SnomedNamespaceAndModuleAssigner {
	
	private final LongKeyLongMap relationshipModuleMap = PrimitiveMaps.newLongKeyLongOpenHashMap();
	private final LongKeyLongMap concreteDomainModuleMap = PrimitiveMaps.newLongKeyLongOpenHashMap();
	private final Set<String> extensionModuleIds = Sets.newHashSet();
	
	private String defaultNamespace;
	private String defaultModule;
	private BranchContext context;

	public ExtensionNamespaceAndModuleAssigner() {}
	
	@Override
	public void init(final String defaultNamespace, final String defaultModule, final BranchContext context) {
		this.defaultNamespace = defaultNamespace;
		this.defaultModule = defaultModule;
		this.context = context;

		relationshipModuleMap.clear();
		concreteDomainModuleMap.clear();
		extensionModuleIds.clear();
		
		initExtensionParentModules();
	}
	
	private void initExtensionParentModules() {
		final String path = context.path();
		final Map<String, CodeSystem> snomedCodeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.filterByToolingId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
			.build()
			.execute(context)
			.stream()
			/*
			 * The current branch should be either a working branch, or a descendant of the
			 * code system's working branch.
			 */
			.filter(cs -> path.equals(cs.getBranchPath()) || path.startsWith(cs.getBranchPath() + Branch.SEPARATOR))
			.collect(Collectors.toMap(cs -> cs.getShortName(), cs -> cs));
		
		final Optional<CodeSystem> currentCodeSystem = snomedCodeSystems.values()
			.stream()
			// Longest matching working branch path (or prefix) wins
			.max(Comparator.comparing(cs -> cs.getBranchPath().length()));

		// Collect module IDs based on code system metadata
		if (!currentCodeSystem.isEmpty()) {
			final Collection<String> moduleIds = (Collection<String>) currentCodeSystem.get()
				.getAdditionalProperties()
				.getOrDefault(SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of());
			
			extensionModuleIds.addAll(moduleIds);
		}
	}

	@Override
	public String getRelationshipNamespace(final String sourceConceptId) {
		final String sourceConceptNamespace = SnomedIdentifiers.getNamespace(sourceConceptId);
		final long sourceConceptIdAsLong = Long.parseLong(sourceConceptId);
		checkArgument(relationshipModuleMap.containsKey(sourceConceptIdAsLong), "The relationship module ID for '%s' was not collected.", sourceConceptId);
		final String moduleId = Long.toString(relationshipModuleMap.get(sourceConceptIdAsLong));
		
		if (!extensionModuleIds.contains(moduleId)) {
			return defaultNamespace;
		}
		
		return sourceConceptNamespace;
	}

	@Override
	public String getRelationshipModuleId(final String sourceConceptId) {
		final long sourceConceptIdAsLong = Long.parseLong(sourceConceptId);
		checkArgument(relationshipModuleMap.containsKey(sourceConceptIdAsLong), "The relationship module ID for '%s' was not collected.", sourceConceptId);
		final String moduleId = Long.toString(relationshipModuleMap.get(sourceConceptIdAsLong));
		
		if (!extensionModuleIds.contains(moduleId)) {
			return defaultModule;
		}
		
		return moduleId;
	}

	@Override
	public String getConcreteDomainModuleId(final String referencedConceptId) {
		final long referencedConceptIdAsLong = Long.parseLong(referencedConceptId);
		checkArgument(concreteDomainModuleMap.containsKey(referencedConceptIdAsLong), "The concrete domain member module ID for '%s' was not collected.", referencedConceptId);
		final String moduleId = Long.toString(concreteDomainModuleMap.get(referencedConceptIdAsLong));
		
		if (!extensionModuleIds.contains(moduleId)) {
			return defaultModule;
		}
		
		return moduleId;
	}

	@Override
	public void collectRelationshipModules(final Set<String> conceptIds) {
		relationshipModuleMap.clear();

		collectModules(conceptIds, c -> {
			final long conceptId = Long.parseLong(c.getId());
			final long moduleId = Long.parseLong(c.getModuleId());
			relationshipModuleMap.put(conceptId, moduleId);
		});
	}

	@Override
	public void collectConcreteDomainModules(final Set<String> conceptIds) {
		concreteDomainModuleMap.clear();

		collectModules(conceptIds, c -> {
			final long conceptId = Long.parseLong(c.getId());
			final long moduleId = Long.parseLong(c.getModuleId());
			concreteDomainModuleMap.put(conceptId, moduleId);
		});
	}
	
	private void collectModules(final Set<String> conceptIds, final Consumer<SnomedConcept> consumer) {
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
	
	@Override
	public String toString() {
		return String.format("%s[defaultNamespace: '%s', defaultModule: '%s']", getName(), defaultNamespace, defaultModule);
	}
}
