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
 * The valuse set membership search result provider singleton.
 * 
 */
public enum ValueSetMembershipSearchResultProvider {

	/**
	 * The search result provider instance.
	 */
	INSTANCE;

	/**
	 * Returns with a collection of value set members and their globally unique
	 * identifier referencing to a terminology independent component identified
	 * by the specified <b>terminologyComponentId</b> and the
	 * <b>componentId</b>.
	 * 
	 * @param codeSystemShortName
	 *            the code system short name for the component.
	 * @param componentId
	 *            the identifier of the component.
	 * @return a collection of value set members with their globally unique
	 *         storage key referencing to the component.
	 */
	public Collection<IComponent<String>> getValueSetMembers(final String codeSystemShortName, final String componentId) {
		return getUnmodifiableSet(codeSystemShortName, componentId, new MembershipSearchResultProviderStrategy<IValueSetMembershipLookupService>() {
			@Override
			public Collection<IComponent<String>> getComponents(IValueSetMembershipLookupService service, String codeSystemShortName, String componentId) {
				return service.getMembers(codeSystemShortName, componentId);
			}
		});
	}

	/**
	 * Returns with a collection of value sets and their globally unique
	 * identifier referencing to a terminology independent component identified
	 * by the specified <b>terminologyComponentId</b> and the
	 * <b>componentId</b>.
	 * 
	 * @param codeSystemShortName
	 *            the code system short name for the component.
	 * @param componentId
	 *            the identifier of the component.
	 * @return a collection of value sets with their globally unique
	 *         storage key referencing to the component.
	 */
	public Collection<IComponent<String>> getValueSets(final String codeSystemShortName, final String componentId) {
		return getUnmodifiableSet(codeSystemShortName, componentId, new MembershipSearchResultProviderStrategy<IValueSetMembershipLookupService>() {
			@Override
			public Collection<IComponent<String>> getComponents(IValueSetMembershipLookupService service, String codeSystemShortName, String componentId) {
				return service.getValueSets(codeSystemShortName, componentId);
			}
		});
	}

	private Collection<IComponent<String>> getUnmodifiableSet(final String codeSystemShortName, final String componentId, MembershipSearchResultProviderStrategy<IValueSetMembershipLookupService> provider) {
		final Set<IComponent<String>> result = Sets.newHashSet();
		final Collection<IValueSetMembershipLookupService> lookupServices = CoreTerminologyBroker.getInstance().getValueSetMembershipLookupServices();
		for (final IValueSetMembershipLookupService service : lookupServices) {
			result.addAll(provider.getComponents(service, codeSystemShortName, componentId));
		}
		return Collections.unmodifiableSet(result);
	}

}