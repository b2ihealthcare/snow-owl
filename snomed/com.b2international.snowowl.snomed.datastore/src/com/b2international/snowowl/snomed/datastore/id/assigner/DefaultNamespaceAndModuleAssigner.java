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

import java.util.Set;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * Simple assigner that allocates the default namespace and module for relationships and concrete domains.
 * 
 * @since 5.11.5
 */
public final class DefaultNamespaceAndModuleAssigner implements SnomedNamespaceAndModuleAssigner {

	private String defaultNamespace;
	private String defaultModule;

	// Empty constructor required for executable extension-based initialization 
	public DefaultNamespaceAndModuleAssigner() { }
	
	public DefaultNamespaceAndModuleAssigner(final String defaultNamespace, final String defaultModule) {
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
		if (defaultNamespace == null) {
			defaultNamespace = context.service(SnomedCoreConfiguration.class).getDefaultNamespace();
		}

		initializeDefaultModule(context);
	}

	@Override
	public void collectConcreteDomainModules(final Set<String> conceptIds, final BranchContext context) {
		initializeDefaultModule(context);
	}

	private void initializeDefaultModule(final BranchContext context) {
		if (defaultModule == null) {
			defaultModule = context.service(SnomedCoreConfiguration.class).getDefaultModule();

			// verify that the default module concept exists
			SnomedRequests.prepareGetConcept(defaultModule).build().execute(context);
		}
	}
	
	@Override
	public void clear() {
		defaultModule = null;
		defaultNamespace = null;
	}

	@Override
	public String getConfigurationKey() {
		return "default";
	}
}
