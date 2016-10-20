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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A subclass of {@link QuickSearchElement} which relies on the parent provider to return terminology information. 
 */
public class FullQuickSearchElement extends QuickSearchElement {

	private static final long serialVersionUID = 5231144043150272991L;

	private final String terminologyComponentId;

	public FullQuickSearchElement(final String id, 
			final String imageId, 
			final String label, 
			final boolean approximate, 
			final String terminologyComponentId, 
			final int[][] matchRegions, 
			final String[] suffixes) {

		super(id, imageId, label, approximate, matchRegions, suffixes);
		this.terminologyComponentId = checkNotNull(terminologyComponentId, "Terminology component identifier may not be null.");
	}

	@Override
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}
}
