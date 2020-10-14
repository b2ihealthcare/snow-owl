/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.plugin.Component;

/**
 * Simple assigner that allocates the default namespace and module for relationships and concrete domains.
 * 
 * @since 5.11.5
 */
@Component
public final class DefaultNamespaceAndModuleAssigner implements SnomedNamespaceAndModuleAssigner {

	private String defaultNamespace;
	private String defaultModule;

	public DefaultNamespaceAndModuleAssigner() {
	}
	
	@Override
	public void init(final String defaultNamespace, final String defaultModule) {
		this.defaultNamespace = defaultNamespace;
		this.defaultModule = defaultModule;
	}
	
	@Override
	public String getRelationshipNamespace(final String sourceConceptId) {
		return defaultNamespace;
	}

	@Override
	public String getRelationshipModuleId(final String sourceConceptId) {
		return defaultModule;
	}

	@Override
	public String getConcreteDomainModuleId(final String referencedConceptId) {
		return defaultModule;
	}

	@Override
	public void collectRelationshipNamespacesAndModules(final Set<String> conceptIds, final BranchContext context) {
	}

	@Override
	public void collectConcreteDomainModules(final Set<String> conceptIds, final BranchContext context) {
	}

	@Override
	public void clear() {
	}

	@Override
	public String getName() {
		return "default";
	}
	
	@Override
	public String toString() {
		return String.format("%s[defaultNamespace: '%s', defaultModule: '%s']", getName(), defaultNamespace, defaultModule);
	}
	
}
