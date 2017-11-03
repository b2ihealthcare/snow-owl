/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.util.Arrays;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;

/**
 * @param <T>
 */
interface Rf2ContentType<T extends SnomedComponent> {

	default void register(String[] values, EffectiveTimeSlice slice) {
		final String containerId = getContainerId(values);
		slice.register(containerId, getType(), values);
	}

	default T resolve(String[] values) {
		final T component = create();
		component.setId(values[0]);
		component.setEffectiveTime(EffectiveTimes.parse(values[1], DateFormats.SHORT));
		component.setActive("1".equals(values[2]));
		component.setModuleId(values[3]);
		resolve(component, values);
		return component;
	}

	String getType();

	String getContainerId(String[] values);

	void resolve(T component, String[] values);

	T create();

	default boolean canResolve(String[] header) {
		return Arrays.equals(getHeaderColumns(), header);
	}

	String[] getHeaderColumns();

}