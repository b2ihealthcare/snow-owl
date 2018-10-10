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
import java.util.List;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;
import com.google.common.base.Strings;

/**
 * Represents the content type found in RF2 files.
 * 
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
	
	default void validate(Rf2ValidationIssueReporter reporter, String[] values) {
		final String isActive = values[2];
		final String moduleId = values[3];
		
		if (values.length != getHeaderColumns().length) {
			reporter.error(Rf2ValidationDefects.INCORRECT_COLUMN_NUMBER.getLabel());
		}
		
		if (Strings.isNullOrEmpty(isActive)) {
			reporter.error(Rf2ValidationDefects.MISSING_ACTIVE_FLAG.getLabel());
		}
		
		validateConceptIds(reporter, moduleId);
		validateByContentType(reporter, values);
	}
	
	default void validateConceptIds(Rf2ValidationIssueReporter reporter, String...idsToValidate) {
		final List<String> ids = Arrays.asList(idsToValidate);
		for (String id : ids) {
			try {
				validateByComponentCategory(id, reporter, ComponentCategory.CONCEPT);
			} catch (IllegalArgumentException e) {
				reporter.error("%s %s", id, Rf2ValidationDefects.INVALID_ID);
				// ignore exception
			}
		}
	}
	
	default void validateByComponentCategory(String id, Rf2ValidationIssueReporter reporter, ComponentCategory expectedCategory) {
		validateId(id, reporter);
		final ComponentCategory componentCategory = SnomedIdentifiers.getComponentCategory(id);
		if (componentCategory != expectedCategory) {
			reporter.error(Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.getLabel());
		}
	}
	
	default void validateId(String id, Rf2ValidationIssueReporter reporter) {
		try {
			SnomedIdentifiers.validate(id);
		} catch (IllegalArgumentException e) {
			reporter.error("%s %s", id, Rf2ValidationDefects.INVALID_ID);
		}
	}
	
	LongSet getDependencies(String[] values);
	
	String getType();

	String getContainerId(String[] values);

	void resolve(T component, String[] values);
	
	void validateByContentType(Rf2ValidationIssueReporter reporter, String[] values);

	T create();

	String[] getHeaderColumns();
	
}
