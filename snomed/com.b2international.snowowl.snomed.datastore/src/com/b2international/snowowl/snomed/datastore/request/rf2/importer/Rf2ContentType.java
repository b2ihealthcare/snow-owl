/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;
import com.google.common.base.Strings;

/**
 * Represents the content type found in RF2 files.
 * 
 * @param <T>
 */
public interface Rf2ContentType<T extends SnomedComponent> {

	default void register(String[] values, Rf2EffectiveTimeSlice slice, ImportDefectBuilder defectBuilder) {
		
		final String containerId = getContainerId(values);
		slice.register(containerId, this, values, defectBuilder);
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
	
	default void validate(ImportDefectBuilder defectBuilder, String[] values) {
		final String isActive = values[2];
		final String moduleId = values[3];
		
		defectBuilder
			.whenNotEqual(values.length, getHeaderColumns().length)
			.error(Rf2ValidationDefects.INCORRECT_COLUMN_NUMBER.getLabel());
		
		defectBuilder
			.whenBlank(isActive)
			.error(Rf2ValidationDefects.MISSING_ACTIVE_FLAG.getLabel());
		
		validateConceptIds(defectBuilder, moduleId);
		validateByContentType(defectBuilder, values);
	}
	
	default void validateConceptIds(ImportDefectBuilder defectBuilder, String... idsToValidate) {
		for (String id : idsToValidate) {
			validateByComponentCategory(defectBuilder, id, ComponentCategory.CONCEPT);
		}
	}
	
	default void validateByComponentCategory(ImportDefectBuilder defectBuilder, String id, ComponentCategory expectedCategory) {
		validateId(defectBuilder, id);
		
		final ComponentCategory componentCategory[] = new ComponentCategory[1];
		defectBuilder
			.whenThrows(() -> { componentCategory[0] = SnomedIdentifiers.getComponentCategory(id); })
			.error("%s %s", id, Rf2ValidationDefects.INVALID_ID);
		
		defectBuilder
			.whenNotEqual(componentCategory[0], expectedCategory)
			.error(Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.getLabel());
	}
	
	default void validateId(ImportDefectBuilder defectBuilder, String id) {
		defectBuilder
			.whenThrows(() -> SnomedIdentifiers.validate(id))
			.error("%s %s", id, Rf2ValidationDefects.INVALID_ID);
	}
	
	void validateByContentType(ImportDefectBuilder defectBuilder, String[] values);
	
	LongSet getDependencies(String[] values);
	
	String getType();

	String getContainerId(String[] values);

	void resolve(T component, String[] values);

	T create();

	String[] getHeaderColumns();
	
}
