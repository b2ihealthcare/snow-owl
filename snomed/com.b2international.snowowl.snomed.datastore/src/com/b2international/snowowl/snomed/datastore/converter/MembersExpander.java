/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.6
 */
class MembersExpander {
	
	private final BranchContext context;
	private final Options expand;
	private final List<ExtendedLocale> locales;

	public MembersExpander(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		this.context = context;
		this.expand = expand;
		this.locales = locales;
	}
	
	void expand(List<? extends SnomedCoreComponent> results, Set<String> componentIds) {
		if (expand.containsKey("members")) {
			final Options membersOptions = expand.get("members", Options.class);
			// TODO support limit, offset, filtering, selection
			final SnomedReferenceSetMembers matchingMembers = SnomedRequests
				.prepareSearchMember()
				.all()
				.filterByReferencedComponent(componentIds)
				.setLocales(locales)
				.setExpand(membersOptions.get("expand", Options.class))
				.build()
				.execute(context);
			final Multimap<String, SnomedReferenceSetMember> membersByReferencedComponentId = Multimaps.index(matchingMembers, new Function<SnomedReferenceSetMember, String>() {
				@Override
				public String apply(SnomedReferenceSetMember input) {
					return input.getReferencedComponent().getId();
				}
			});
			for (SnomedCoreComponent component : results) {
				final Collection<SnomedReferenceSetMember> members = membersByReferencedComponentId.get(component.getId());
				((SnomedCoreComponent) component).setMembers(new SnomedReferenceSetMembers(ImmutableList.copyOf(members), null, members.size(), members.size()));
			}
		}
	}

}
