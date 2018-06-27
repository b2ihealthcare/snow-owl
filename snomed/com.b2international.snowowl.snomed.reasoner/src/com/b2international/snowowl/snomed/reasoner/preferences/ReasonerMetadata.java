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
package com.b2international.snowowl.snomed.reasoner.preferences;

import java.io.Serializable;

import com.google.common.primitives.Ints;

/**
 * Encapsulates information about a contributing reasoner factory extension.
 * 
 */
public class ReasonerMetadata implements Comparable<ReasonerMetadata>, Serializable {

	private static final long serialVersionUID = 1L;

	private final int id; 
	private final String name;
	private final String version;
	private final String extensionId;
	
	/**
	 * @param name
	 * @param version
	 * @param extensionId
	 */
	public ReasonerMetadata(final int id, final String name, final String version, final String extensionId) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.extensionId = extensionId;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getExtensionId() {
		return extensionId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extensionId == null) ? 0 : extensionId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ReasonerMetadata))
			return false;
		final ReasonerMetadata other = (ReasonerMetadata) obj;
		if (extensionId == null) {
			if (other.extensionId != null)
				return false;
		} else if (!extensionId.equals(other.extensionId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append(" ");
		sb.append(name);
		sb.append(" [version: ");
		sb.append(version);
		sb.append("] (");
		sb.append(extensionId);
		sb.append(")");
		return sb.toString(); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final ReasonerMetadata o) {
		return Ints.compare(this.id, o.id);
	}
}