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
package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.snomed.api.domain.ISnomedConceptInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescriptionInput;
import com.b2international.snowowl.snomed.api.domain.IdGenerationStrategy;
import com.google.common.collect.ImmutableList;

/**
 */
public class SnomedConceptInput extends AbstractSnomedComponentInput implements ISnomedConceptInput {

	private List<ISnomedDescriptionInput> descriptions = Collections.emptyList();
	private String parentId;
	private IdGenerationStrategy isAIdGenerationStrategy;

	@Override
	public List<ISnomedDescriptionInput> getDescriptions() {
		return descriptions;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public IdGenerationStrategy getIsAIdGenerationStrategy() {
		return isAIdGenerationStrategy;
	}

	public void setParentId(final String parentId) {
		this.parentId = parentId;
	}

	public void setIsAIdGenerationStrategy(final IdGenerationStrategy isAIdGenerationStrategy) {
		this.isAIdGenerationStrategy = isAIdGenerationStrategy;
	}

	public void setDescriptions(final List<? extends ISnomedDescriptionInput> descriptions) {
		this.descriptions = ImmutableList.copyOf(descriptions);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedConceptInput [getIdGenerationStrategy()=");
		builder.append(getIdGenerationStrategy());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getCodeSystemShortName()=");
		builder.append(getCodeSystemShortName());
		builder.append(", getCodeSystemVersionId()=");
		builder.append(getCodeSystemVersionId());
		builder.append(", getTaskId()=");
		builder.append(getTaskId());
		builder.append(", getParentId()=");
		builder.append(getParentId());
		builder.append(", getIsAIdGenerationStrategy()=");
		builder.append(getIsAIdGenerationStrategy());
		builder.append(", getDescriptions()=");
		builder.append(getDescriptions());
		builder.append("]");
		return builder.toString();
	}
}