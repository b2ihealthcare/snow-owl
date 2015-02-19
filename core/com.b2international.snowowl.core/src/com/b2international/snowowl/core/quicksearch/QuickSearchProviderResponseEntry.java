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

/**
 * Implements the {@link IQuickSearchProviderResponseEntry} interface.
 * 
 */
public class QuickSearchProviderResponseEntry implements IQuickSearchProviderResponseEntry {
	private final String id;
	private final String label;
	private boolean approximate;
	private String imageUrl;
	private final List<IQuickSearchMatchRegion> matchRegions = Lists.newArrayList();

	public QuickSearchProviderResponseEntry(String id, String label, boolean approximate) {
		this.id = id;
		this.label = label;
		this.approximate = approximate;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public List<IQuickSearchMatchRegion> getMatchRegions() {
		return matchRegions;
	}

	@Override
	public boolean isApproximate() {
		return approximate;
	}
	
	@Override
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}