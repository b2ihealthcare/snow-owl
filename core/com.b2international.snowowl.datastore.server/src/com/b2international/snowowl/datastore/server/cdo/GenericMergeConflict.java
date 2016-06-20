/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.cdo;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.merge.MergeConflict;
import com.google.common.base.Objects;

/**
 * @since 4.7
 */
public class GenericMergeConflict implements MergeConflict, Serializable {

	private static final long serialVersionUID = 5238168089442966388L;

	private String targetId = null;
	private String sourceId = null;
	private final String message;
	
	public GenericMergeConflict(@Nullable final String sourceId, @Nullable final String targetId, final String message) {
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.message = message;
	}
	
	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}
	
	/**
	 * @return the targetId
	 */
	public String getTargetId() {
		return targetId;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("message", message).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result + ((targetId == null) ? 0 : targetId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenericMergeConflict other = (GenericMergeConflict) obj;
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (sourceId == null) {
			if (other.sourceId != null) {
				return false;
			}
		} else if (!sourceId.equals(other.sourceId)) {
			return false;
		}
		if (targetId == null) {
			if (other.targetId != null) {
				return false;
			}
		} else if (!targetId.equals(other.targetId)) {
			return false;
		}
		return true;
	}
	
}
