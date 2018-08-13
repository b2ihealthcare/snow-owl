/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import java.util.Arrays;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.AbstractRf2RowValidator;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;
import com.google.common.base.Strings;

/**
 * @param <T>
 */
public interface Rf2ContentType<T extends SnomedComponent> {

	default void register(String[] values, Rf2EffectiveTimeSlice slice, Rf2ValidationIssueReporter reporter) {
		final String containerId = getContainerId(values);
		slice.register(containerId, this, values, reporter);
		slice.registerDependencies(getDependentComponentId(values), getDependencies(values));
	}

	default long getDependentComponentId(String[] values) {
		return Long.parseLong(values[0]);
	}

	default T resolve(String[] values) {
		final T component = create();
		final String componentId = values[0];
		final String effectiveTime = values[1];
		final boolean isActive = BooleanUtils.valueOf(values[2]);
		final String moduleId = values[3];
		
		component.setId(componentId);
		if (!Strings.isNullOrEmpty(effectiveTime)) {
			component.setEffectiveTime(EffectiveTimes.parse(values[1], DateFormats.SHORT));
		}
		component.setActive(isActive);
		component.setModuleId(moduleId);
		resolve(component, values);
		return component;
	}

	default boolean canResolve(String[] header) {
		return Arrays.equals(getHeaderColumns(), header);
	}
	
	LongSet getDependencies(String[] values);
	
	String getType();

	String getContainerId(String[] values);

	void resolve(T component, String[] values);

	T create();

	String[] getHeaderColumns();
	
	AbstractRf2RowValidator getValidator(Rf2ValidationIssueReporter reporter, String[] values);
	
}