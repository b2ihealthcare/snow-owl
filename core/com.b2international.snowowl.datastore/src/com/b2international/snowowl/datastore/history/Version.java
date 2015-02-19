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
package com.b2international.snowowl.datastore.history;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Represents a version for the historical revision.
 */
@XStreamAlias("Version")
public final class Version implements IVersion<CDOID>, Serializable {
	
	private static final long serialVersionUID = 3283782033458915096L;

	@XStreamAlias("majorVersion")
	private int majorVersion;
	
	@XStreamAlias("minorVersion")
	private int minorVersion;
	
	private final Map<CDOID, Long> affectedObjectIds = newHashMap();

	public Version(int majorVersion) {
		this(majorVersion, 0);
	}
	
	public Version(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	@Override
	public int compareTo(IVersion<?> version) {
		int majorVersionDiff = majorVersion - version.getMajorVersion();
		return majorVersionDiff != 0 ? majorVersionDiff : (minorVersion - version.getMinorVersion());
	}

	@Override
	public int getMajorVersion() {
		return majorVersion;
	}
	
	@Override
	public int getMinorVersion() {
		return minorVersion;
	}
	
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	/**
	 * @param majorVersion the majorVersion to set
	 */
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	
	@Override
	public String toString() {
		return minorVersion == 0 ? String.valueOf(majorVersion) : majorVersion + "." + minorVersion;
	}

	public void addAffectedObjectId(CDOID id, final long timestamp) {
		if (affectedObjectIds.containsKey(id)) {
			final Long storeTimestamp = affectedObjectIds.get(id);
			//only update timestamp if smaller. store the smallest one with the ID
			if (timestamp < storeTimestamp) {
				affectedObjectIds.put(id, timestamp);
			}
		} else {
			affectedObjectIds.put(id, timestamp);
		}
	}
	
	@Override
	public Map<CDOID, Long> getAffectedObjectIds() {
		return affectedObjectIds;
	}
	
	public boolean representsMajorChange() {
		return (minorVersion == 0);
	}
	
	
}