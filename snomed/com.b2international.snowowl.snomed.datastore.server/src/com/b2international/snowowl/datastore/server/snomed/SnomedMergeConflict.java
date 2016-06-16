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

import com.b2international.snowowl.datastore.server.cdo.GenericMergeConflict;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
public class SnomedMergeConflict extends GenericMergeConflict {

	private static final long serialVersionUID = 417129467147870910L;

	private String sourceType = null;
	private String targetType = null;
	
	private final Collection<String> changedSourceFeatures;
	private final Collection<String> changedTargetFeatures;
	
	public SnomedMergeConflict(@Nullable final String sourceId, @Nullable final String targetId, final String message) {
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
	
}
