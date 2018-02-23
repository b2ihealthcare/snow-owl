/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentSearchRequest.OptionKey;

/**
 * Abstract superclass for building request for SNOMED CT component searches.
 * Clients should not extend. 
 * @since 5.3
 */
public abstract class SnomedComponentSearchRequestBuilder<B extends SnomedComponentSearchRequestBuilder<B, R>, R> extends SnomedSearchRequestBuilder<B, R> {
	
	/**
	 * Filter matches by their active membership in the given reference set or ECL expression.
	 * @param referenceSetIdOrECL
	 * @return
	 */
	public final B isActiveMemberOf(String referenceSetIdOrECL) {
		return addOption(OptionKey.ACTIVE_MEMBER_OF, referenceSetIdOrECL);
	}
	
	/**
	 * Filter matches by their active membership in any of the given reference sets.
	 * @param referenceSetIds
	 * @return
	 */
	public final B isActiveMemberOf(Iterable<String> referenceSetIds) {
		return addOption(OptionKey.ACTIVE_MEMBER_OF, referenceSetIds);
	}
	
	/**
	 * Filter matches by their membership in the given reference set or ECL expression. Matches both active and inactive memberships.
	 * @param referenceSetIdOrECL
	 * @return
	 */
	public final B isMemberOf(String referenceSetIdOrECL) {
		return addOption(OptionKey.MEMBER_OF, referenceSetIdOrECL);
	}
	
	/**
	 * Filter matches by their membership in any of the given reference sets. Matches both active and inactive memberships.
	 * @param referenceSetIds
	 * @return
	 */
	public final B isMemberOf(Iterable<String> referenceSetIds) {
		return addOption(OptionKey.MEMBER_OF, referenceSetIds);
	}
	
	/**
	 * Filter matches by their namespace (specified in their SNOMED CT identifier).
	 * 
	 * @param namespaceId
	 *            - the namespace identifier as a string
	 * @return SnomedComponentSearchRequestBuilder
	 * @see Concepts
	 */
	public final B filterByNamespace(String namespaceId) {
		return addOption(OptionKey.NAMESPACE, namespaceId);
	}
	
	/**
	 * Filter matches by their namespace (specified in their SNOMED CT identifier).
	 * 
	 * @param namespaceIds 
	 *            - the namespace identifiers
	 * @return SnomedComponentSearchRequestBuilder
	 * @see Concepts
	 */
	public final B filterByNamespaces(Iterable<String> namespaceIds) {
		return addOption(OptionKey.NAMESPACE, namespaceIds);
	}

}
