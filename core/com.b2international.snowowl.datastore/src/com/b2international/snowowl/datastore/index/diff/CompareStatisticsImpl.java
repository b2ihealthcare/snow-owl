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
package com.b2international.snowowl.datastore.index.diff;

import com.b2international.commons.Triple;

/**
 * Compare statistics implementation.
 *
 */
public class CompareStatisticsImpl implements CompareStatistics {

	private static final long serialVersionUID = 7709572557998353885L;

	private final int newComponentCount;
	private final int changedComponentCount;
	private final int detachedComponentCount;
	
	public CompareStatisticsImpl(final int newComponentCount, final int changedComponentCount, final int detachedComponentCount) {
		this.newComponentCount = newComponentCount;
		this.changedComponentCount = changedComponentCount;
		this.detachedComponentCount = detachedComponentCount;
	}

	@Override
	public int getNewComponentCount() {
		return newComponentCount;
	}

	@Override
	public int getChangedComponentCount() {
		return changedComponentCount;
	}

	@Override
	public int getDetachedComponentCount() {
		return detachedComponentCount;
	}

	@Override
	public Triple<String, String, String> toFormattedString() {
		return Triple.of(
				"New component" + (1 == newComponentCount ? "" : "s") + ": " + newComponentCount, 
				"Modified component" + (1 == changedComponentCount ? "" : "s") + ": " + changedComponentCount, 
				"Deleted component" + (1 == detachedComponentCount ? "" : "s") + ": " + detachedComponentCount);
	}

	
	
}