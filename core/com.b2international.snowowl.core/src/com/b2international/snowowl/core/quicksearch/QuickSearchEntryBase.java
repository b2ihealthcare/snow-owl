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
 */
public class QuickSearchEntryBase {

	protected final QuickSearchElement element;
	
	protected int providerCount;
	
	protected boolean drawProviderText;
	
	protected boolean drawBottomSeparator;
	
	protected boolean drawApproximate;

	public QuickSearchEntryBase(final QuickSearchElement element) {
		this.element = element;
	}

	public void setProviderCount(final int count) {
		this.providerCount = count;
	}

	public QuickSearchElement getElement() {
		return element;
	}

	public String[] getElementSuffixes() {
		return element.getSuffixes();
	}
	
	public int[][] getElementMatchRegions() {
		return element.getMatchRegions();
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
