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
package com.b2international.snowowl.snomed.core.rest.services;

import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 */
public abstract class SnomedComponentOrdering {

	private static final Ordering<? extends SnomedComponent> ID_ORDERING = Ordering.natural().onResultOf(new Function<SnomedComponent, String>() {
		@Override
		public String apply(final SnomedComponent input) {
			return input.getId();
		}
	});
	
	@SuppressWarnings("unchecked")
	public static <T extends SnomedComponent> Ordering<T> id() {
		return (Ordering<T>) ID_ORDERING;
	}

	private SnomedComponentOrdering() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}