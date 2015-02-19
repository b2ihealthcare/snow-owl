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
package com.b2international.snowowl.snomed.importer.rf2.csv;

import java.util.Date;

/**
 * Represents a module dependency reference set release file row.
 * Provides storage for the following CSV fields:
 * <ul>
 * <li>{@code sourceEffectiveTime}
 * <li>{@code targetEffectiveTime}
 * </ul>
 * 
 * @since Snow&nbsp;Owl 2.9
 */
public class ModuleDependencyRefSetRow extends RefSetRow {

	public static final String PROP_SOURCE_EFFECTIVE_TIME = "sourceEffectiveTime";
	public static final String PROP_TARGET_EFFECTIVE_TIME = "targetEffectiveTime";

	private Date sourceEffectiveTime;
	private Date targetEffectiveTime;

	public Date getSourceEffectiveTime() {
		return sourceEffectiveTime;
	}

	public void setSourceEffectiveTime(Date sourceEffectiveTime) {
		this.sourceEffectiveTime = sourceEffectiveTime;
	}

	public Date getTargetEffectiveTime() {
		return targetEffectiveTime;
	}

	public void setTargetEffectiveTime(Date targetEffectiveTime) {
		this.targetEffectiveTime = targetEffectiveTime;
	}

	@Override
	public String toString() {
		return String.format(
				"ModuleDependencyRefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, referencedComponentId=%s, sourceEffectiveTime=%s, targetEffectiveTime=%s]",
				getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(), getReferencedComponentId(), getSourceEffectiveTime(), getTargetEffectiveTime());
	}
}