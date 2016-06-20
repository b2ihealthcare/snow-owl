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
package com.b2international.snowowl.datastore.server.snomed;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflict;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
public class SnomedCDOMergeConflict extends GenericCDOMergeConflict {

	private static final long serialVersionUID = 417129467147870910L;

	private String sourceType = null;
	private String targetType = null;
	
	private final Collection<String> changedSourceFeatures;
	private final Collection<String> changedTargetFeatures;
	
	public SnomedCDOMergeConflict(@Nullable final String sourceId, @Nullable final String targetId, final String message) {
		super(sourceId, targetId, message);
		changedSourceFeatures = Strings.isNullOrEmpty(sourceId) ? Collections.<String>emptyList() : Lists.<String>newArrayList();
		changedTargetFeatures = Strings.isNullOrEmpty(targetId) ? Collections.<String>emptyList() : Lists.<String>newArrayList();
	}

	public String getSourceType() {
		return sourceType;
	}

	public Collection<String> getChangedSourceFeatures() {
		return changedSourceFeatures;
	}

	public String getTargetType() {
		return targetType;
	}

	public Collection<String> getChangedTargetFeatures() {
		return changedTargetFeatures;
	}

	public void setSourceType(final String sourceClass) {
		this.sourceType = sourceClass;
	}

	public void setTargetType(final String targetClass) {
		this.targetType = targetClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((changedSourceFeatures == null) ? 0 : changedSourceFeatures.hashCode());
		result = prime * result + ((changedTargetFeatures == null) ? 0 : changedTargetFeatures.hashCode());
		result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
		result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SnomedCDOMergeConflict other = (SnomedCDOMergeConflict) obj;
		if (changedSourceFeatures == null) {
			if (other.changedSourceFeatures != null) {
				return false;
			}
		} else if (!changedSourceFeatures.equals(other.changedSourceFeatures)) {
			return false;
		}
		if (changedTargetFeatures == null) {
			if (other.changedTargetFeatures != null) {
				return false;
			}
		} else if (!changedTargetFeatures.equals(other.changedTargetFeatures)) {
			return false;
		}
		if (sourceType == null) {
			if (other.sourceType != null) {
				return false;
			}
		} else if (!sourceType.equals(other.sourceType)) {
			return false;
		}
		if (targetType == null) {
			if (other.targetType != null) {
				return false;
			}
		} else if (!targetType.equals(other.targetType)) {
			return false;
		}
		return true;
	}
	
}
