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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SetSearchRequestEvaluator.OptionKey;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;

/**
 * @since 7.8
 */
public abstract class SnomedCollectionSearchRequestEvaluator<R, CR> {
	
	protected SnomedReferenceSetMembers fetchRefsetMembers(CodeSystemURI uri, BranchContext context, Options search) {
		
		final Integer limit = search.get(OptionKey.LIMIT, Integer.class);
		final String searchAfter = search.get(OptionKey.AFTER, String.class);
		final List<ExtendedLocale> locales = search.getList(OptionKey.LOCALES, ExtendedLocale.class);
		
		SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember();
		
		if (search.containsKey(OptionKey.SET)) {
			final Collection<String> refsetId = search.getCollection(OptionKey.SET, String.class);
			requestBuilder.filterByRefSet(refsetId);
		}
		
		return requestBuilder
			.filterByActive(true)
			.filterByRefSetType(getRefsetTypes())
			.setLocales(locales)
			.setExpand("referencedComponent(expand(fsn()))")
			.setLimit(limit)
			.setSearchAfter(searchAfter)
			.build()
			.execute(context);
	}
	
	protected abstract CR toCollectionResource(final SnomedReferenceSetMembers members, CodeSystemURI codeSystemURI);
	
	
	protected abstract Set<SnomedRefSetType> getRefsetTypes();
}
