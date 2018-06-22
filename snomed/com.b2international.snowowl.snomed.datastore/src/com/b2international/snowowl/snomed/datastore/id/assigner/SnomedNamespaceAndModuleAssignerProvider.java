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
package com.b2international.snowowl.snomed.datastore.id.assigner;

import javax.inject.Provider;

import com.b2international.commons.platform.Extensions;

/**
 * @since 6.3
 */
public enum SnomedNamespaceAndModuleAssignerProvider implements Provider<SnomedNamespaceAndModuleAssigner> {

	INSTANCE;

	private static final String NAMESPACE_ASSIGNER_EXTENSION = "com.b2international.snowowl.snomed.datastore.snomedNamespaceAndModuleAssigner";
	
	@Override
	public SnomedNamespaceAndModuleAssigner get() {
		return Extensions.getFirstPriorityExtension(NAMESPACE_ASSIGNER_EXTENSION, SnomedNamespaceAndModuleAssigner.class);
	}
	
}
