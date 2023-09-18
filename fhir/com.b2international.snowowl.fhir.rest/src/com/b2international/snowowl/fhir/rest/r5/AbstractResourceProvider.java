/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest.r5;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;

/**
 * @since 9.0
 */
public abstract class AbstractResourceProvider<R extends IBaseResource> implements IResourceProvider {

	// Standard headers
	protected static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

	// Application-specific headers
	protected static final String HEADER_X_OWNER = "X-Owner";
	protected static final String HEADER_X_OWNER_PROFILE_NAME = "X-Owner-Profile-Name";
	protected static final String HEADER_X_BUNDLE_ID = "X-Bundle-Id";
	protected static final String HEADER_X_EFFECTIVE_DATE = "X-Effective-Date";

	private final Class<R> resourceType;

	@Autowired
	protected Provider<IEventBus> bus;

	public AbstractResourceProvider(final Class<R> resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public Class<R> getResourceType() {
		return resourceType;
	}

	protected Set<String> getUniqueOrListTokens(final List<TokenOrListParam> orListParams) {
		if (orListParams == null) {
			return null;
		}
		
		return orListParams.stream()
			.flatMap(tolp -> tolp.getValuesAsQueryTokens().stream())
			.map(TokenParam::getValue)
			.collect(Collectors.toSet());
	}

	protected String asString(final DateParam dateParam) {
		return (dateParam != null && !dateParam.isEmpty()) ? dateParam.getValueAsString() : null;
	}

	protected String asString(final StringParam stringParam) {
		return (stringParam != null && !stringParam.isEmpty()) ? stringParam.getValue() : null; 
	}

	protected String getSortField(final SortSpec sortSpec) {
		final String paramName = sortSpec.getParamName();
		
		if (SortOrderEnum.DESC.equals(sortSpec.getOrder())) {
			return "-" + paramName;
		} else {
			return paramName;
		}
	}

	protected Set<String> asSet(final StringOrListParam stringOrList) {
		if (stringOrList == null) {
			return null;
		}

		return stringOrList.getValuesAsQueryTokens()
			.stream()
			.map(sp -> sp.getValueNotNull())
			.collect(Collectors.toSet());
	}

	protected Set<String> asSet(final UriOrListParam uriOrList) {
		if (uriOrList == null) {
			return null;
		}

		return uriOrList.getValuesAsQueryTokens()
			.stream()
			.map(sp -> sp.getValueNotNull())
			.collect(Collectors.toSet());
	}

	protected Set<String> intersectionOf(final UriOrListParam uriOrList1, final UriOrListParam uriOrList2) {
		final Set<String> set1 = asSet(uriOrList1);
		final Set<String> set2 = asSet(uriOrList2);

		if (set2 == null) {
			return set1;
		} else if (set1 == null) {
			return set2;
		} else {
			return Sets.intersection(set1, set2);
		}
	}
	
	protected com.b2international.snowowl.fhir.core.model.dt.Coding asCoding(final Coding coding) {
		return com.b2international.snowowl.fhir.core.model.dt.Coding.of(coding.getSystem(), coding.getCode());
	}
	
	protected boolean isForceDelete(final RequestDetails requestDetails) {
		final Map<String, String[]> parameters = requestDetails.getParameters();
		final String[] forceValues = parameters.get("force");
		
		// It is not a case of forced deletion when there is no mention of the parameter
		if (forceValues == null) {
			return false;
		}
		
		// Support "?force" form without any value
		if (forceValues.length < 1) {
			return true;
		}
		
		// Otherwise check first occurrence
		return Boolean.parseBoolean(forceValues[0]);
	}
}
