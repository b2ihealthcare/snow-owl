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

import java.util.Arrays;

/**
 * Additional information extracted from a {@link QuickSearchElement}.
 * 
 */
public class QuickSearchElementInfo {
	private final int[][] matchRegions;
	private final String[] suffixes;

	public QuickSearchElementInfo(int[][] matchRegions, String[] suffixes) {
		this.matchRegions = matchRegions;
		this.suffixes = suffixes;
	}
	
	/**
	 * @return the match regions
	 */
	public int[][] getMatchRegions() {
		return matchRegions;
	}
	
	/**
	 * @return the suffixes
	 */
	public String[] getSuffixes() {
		return suffixes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(matchRegions);
		result = prime * result + Arrays.hashCode(suffixes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuickSearchElementInfo other = (QuickSearchElementInfo) obj;
		if (!Arrays.equals(matchRegions, other.matchRegions))
			return false;
		if (!Arrays.equals(suffixes, other.suffixes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "QuickSearchElementInfo [matchRegions=" + Arrays.toString(matchRegions) + ", suffixes="
				+ Arrays.toString(suffixes) + "]";
	}
	
}