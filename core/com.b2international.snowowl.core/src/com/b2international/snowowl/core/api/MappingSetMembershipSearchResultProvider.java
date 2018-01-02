/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.google.common.collect.Sets;

/**
 * @since 3.0
 */
public enum MappingSetMembershipSearchResultProvider {

	INSTANCE;

	public Collection<IComponent<String>> getMappingSetMappings(final String codeSystemShortName, final String componentId) {
		return getUnmodifiableSet(codeSystemShortName, componentId, new MembershipSearchResultProviderStrategy<IMappingSetMembershipLookupService>() {
			@Override
			public Collection<IComponent<String>> getComponents(IMappingSetMembershipLookupService service, String terminologyComponentId, String componentId) {
				return service.getMappings(terminologyComponentId, componentId);
			}
		});
	}

	public Collection<IComponent<String>> getMappingSets(final String codeSystemShortName, final String componentId) {
		return getUnmodifiableSet(codeSystemShortName, componentId, new MembershipSearchResultProviderStrategy<IMappingSetMembershipLookupService>() {
			@Override
			public Collection<IComponent<String>> getComponents(IMappingSetMembershipLookupService service, String terminologyComponentId, String componentId) {
				return service.getMappingSets(terminologyComponentId, componentId);
			}
		});
	}

	private Collection<IComponent<String>> getUnmodifiableSet(final String terminologyComponentId, final String componentId, MembershipSearchResultProviderStrategy<IMappingSetMembershipLookupService> provider) {
		Set<IComponent<String>> result = Sets.newHashSet();
		Collection<IMappingSetMembershipLookupService> lookupServices = CoreTerminologyBroker.getInstance().getMappingSetMembershipLookupServices();
		for (IMappingSetMembershipLookupService service : lookupServices) {
			result.addAll(provider.getComponents(service, terminologyComponentId, componentId));
		}
		return Collections.unmodifiableSet(result);
	}

}