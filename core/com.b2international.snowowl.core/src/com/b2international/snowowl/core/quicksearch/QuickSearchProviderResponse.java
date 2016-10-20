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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

/**
 * Implements the {@link IQuickSearchProviderResponse} interface.
 */
public class QuickSearchProviderResponse implements IQuickSearchProviderResponse {
	private final String providerName;
	private final int totalHitCount;
	private final List<QuickSearchElement> entries = newArrayList();

	public QuickSearchProviderResponse(String providerName, int totalHitCount) {
		this.providerName = providerName;
		this.totalHitCount = totalHitCount;
	}

	@Override
	public String getProviderName() {
		return providerName;
	}

	public int getTotalHitCount() {
		return totalHitCount;
	}

	@Override
	public List<QuickSearchElement> getEntries() {
		return entries;
	}
}
