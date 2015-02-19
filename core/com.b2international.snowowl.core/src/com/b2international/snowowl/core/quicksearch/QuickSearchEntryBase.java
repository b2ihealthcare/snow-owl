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

/**
 * Wraps a {@link QuickSearchElement}, extending it with information required for rendering the element. 
 *
 */
public class QuickSearchEntryBase {

	protected static final int[][] EMPTY_REGIONS = new int[0][0];
	
	protected static final String[] EMPTY_SUFFIXES = new String[0];
	
	protected final QuickSearchElement element;
	
	protected final int[][] elementMatchRegions;
	
	protected final String[] elementSuffixes;
	
	protected int providerCount;
	
	protected boolean drawProviderText;
	
	protected boolean drawBottomSeparator;
	
	protected boolean drawApproximate;

	public QuickSearchEntryBase(final QuickSearchElement element, final int[][] elementMatchRegions, final String[] elementSuffixes) {
		this.element = element;
		this.elementMatchRegions = elementMatchRegions;
		this.elementSuffixes = elementSuffixes;
	}
	
	public QuickSearchEntryBase(final QuickSearchElement element) {
		this(element, EMPTY_REGIONS, EMPTY_SUFFIXES);
	}

	public void setProviderCount(final int count) {
		this.providerCount = count;
	}

	public QuickSearchElement getElement() {
		return element;
	}

	public String[] getElementSuffixes() {
		return elementSuffixes;
	}
	
	public int[][] getElementMatchRegions() {
		return elementMatchRegions;
	}

	public void setDrawProviderText() {
		this.drawProviderText = true;
	}

	public void setDrawBottomSeparator() {
		this.drawBottomSeparator = true;
	}

	public void setDrawApproximate() {
		this.drawApproximate = true;
	}
}