/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;

import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.SetMember;
import com.b2international.snowowl.core.domain.SetMembers;
import com.b2international.snowowl.core.request.SetMemberSearchRequestEvaluator;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;

/**
 * @since 7.7
 */
public class SnomedRefSetMemberSearchRequestEvaluator implements SetMemberSearchRequestEvaluator {

	@Override
	public SetMembers evaluate(CodeSystemURI uri, BranchContext context, Options search) {
		
		if (!search.containsKey(OptionKey.SET)) {
			return SetMemberSearchRequestEvaluator.NOOP.evaluate(uri, context, search);
		}
		
		final String refsetId = search.get(OptionKey.SET, String.class);
		final Integer limit = search.get(OptionKey.LIMIT, Integer.class);
		final String searchAfter = search.get(OptionKey.AFTER, String.class);
		
		SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
			.isActiveMemberOf(refsetId)
			.setExpand("preferredDescriptions()")
			.setLimit(limit)
			.setSearchAfter(searchAfter)
			.build()
			.execute(context);
		
		return new SetMembers(concepts.stream().map(c -> toMember(c, uri)).collect(Collectors.toList()),
				concepts.getSearchAfter(),
				concepts.getLimit(),
				concepts.getTotal()
		);
		
	}

	private SetMember toMember(SnomedConcept concept, CodeSystemURI uri) {
		return new SetMember(REFSET_MEMBER_NUMBER, 
				uri.getCodeSystem(),
				concept.getId(),
				concept.getPreferredDescriptions().first().get().getTerm(),
				concept.getIconId());
	}
	
}
