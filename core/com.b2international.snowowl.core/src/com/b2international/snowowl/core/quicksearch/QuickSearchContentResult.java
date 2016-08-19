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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Captures the results of a quick search filtering operation sent from the server. 
 */
public class QuickSearchContentResult implements Serializable {
	
	private static final List<QuickSearchElement> EMPTY_LIST = ImmutableList.of();

	private static final long serialVersionUID = -5073119508848593926L;
	
	private final int totalHitCount;
	private final List<QuickSearchElement> elements;
	
	public QuickSearchContentResult() {
		this(0, EMPTY_LIST);
	}

	public QuickSearchContentResult(final int totalHitCount, final List<? extends QuickSearchElement> elements) {
		checkArgument(totalHitCount >= 0, "Total hit count may not be negative.");
		checkNotNull(elements, "Element list may not be null.");
		
		this.totalHitCount = totalHitCount;
		this.elements = ImmutableList.copyOf(elements);
	}

	public int getTotalHitCount() {
		return totalHitCount;
	}
	
	public List<QuickSearchElement> getElements() {
		return elements;
	}
}
