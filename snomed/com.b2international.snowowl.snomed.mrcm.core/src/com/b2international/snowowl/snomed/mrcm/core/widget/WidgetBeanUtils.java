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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import java.util.Set;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.collect.ImmutableSet;

/**
 * Utility methods and constants related to widget beans.
 * 
 */
final public class WidgetBeanUtils {

	/** Relationships of this type should never appear in non-zero groups. */
	public static final Set<String> NEVER_GROUPED_RELATIONSHIP_TYPE_IDS = ImmutableSet.<String>builder()
			.add(Concepts.IS_A)
			.add(Concepts.PART_OF)
			.add(Concepts.LATERALITY)
			.add(Concepts.HAS_ACTIVE_INGREDIENT)
			.add(Concepts.HAS_DOSE_FORM)
			.add(Concepts.HAS_RELEASE_CHARACTERISTIC)
			.add(Concepts.HAS_PRODUCT_HIERARCHY_LEVEL)
			.build();
	
	private WidgetBeanUtils() {}
}