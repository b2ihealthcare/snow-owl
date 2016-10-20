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
package com.b2international.snowowl.core.quicksearch;

import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Implements the {@link IQuickSearchResponse} interface.
 * 
 */
@XStreamAlias("QuickSearchResponse")
public class QuickSearchResponse implements IQuickSearchResponse {
	private final List<IQuickSearchProviderResponse> providerResponses = Lists.newArrayList();
	private final String suggestedSuffix;
	private final int totalExactMatchCount;
	private final int totalApproximateMatchCount;

	public QuickSearchResponse(String suggestedSuffix, int totalExactMatchCount, int totalApproximateMatchCount) {
		this.suggestedSuffix = suggestedSuffix;
		this.totalExactMatchCount = totalExactMatchCount;
		this.totalApproximateMatchCount = totalApproximateMatchCount;
	}

	@Override
	public List<IQuickSearchProviderResponse> getProviderResponses() {
		return providerResponses;
	}

	@Override
	public String getSuggestedSuffix() {
		return suggestedSuffix;
	}

	@Override
	public int getTotalExactMatchCount() {
		return totalExactMatchCount;
	}

	@Override
	public int getTotalApproximateMatchCount() {
		return totalApproximateMatchCount;
	}
}