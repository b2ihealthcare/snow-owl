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
package com.b2international.snowowl.api.impl.history.domain;

import com.b2international.snowowl.core.history.domain.IHistoryVersion;

/**
 *
 */
public class HistoryVersion implements IHistoryVersion {

	private int majorVersion;
	private int minorVersion;

	/**
	 * @param majorVersion
	 * @param minorVersion
	 */
	public HistoryVersion(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final IHistoryVersion version) {
		final int majorVersionDiff = getMajorVersion() - version.getMajorVersion();
		return majorVersionDiff != 0 ? majorVersionDiff : (getMinorVersion() - version.getMinorVersion());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IVersion#getMajorVersion()
	 */
	@Override
	public int getMajorVersion() {
		return majorVersion;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IVersion#getMinorVersion()
	 */
	@Override
	public int getMinorVersion() {
		return minorVersion;
	}
}