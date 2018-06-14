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
package com.b2international.snowowl.snomed.reasoner.model;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * A SNOMED&nbsp;CT concept ID collection which holds constants in primitive long form.
 */
public abstract class LongConcepts {

	private static final long HAS_DOSE_FORM_ID = Long.parseLong(Concepts.HAS_DOSE_FORM);
	private static final long LATERALITY_ID = Long.parseLong(Concepts.LATERALITY);
	private static final long PART_OF_ID = Long.parseLong(Concepts.PART_OF);

	public static final long NOT_APPLICABLE_ID = -1L;
	public static final long IS_A_ID = Long.parseLong(Concepts.IS_A);
	public static final long CONCEPT_MODEL_ATTRIBUTE_ID = Long.parseLong(Concepts.CONCEPT_MODEL_ATTRIBUTE);
	public static final long HAS_ACTIVE_INGREDIENT_ID = Long.parseLong(Concepts.HAS_ACTIVE_INGREDIENT);
	public static final long EXISTENTIAL_RESTRICTION_MODIFIER_ID = Long.parseLong(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
	public static final long UNIVERSAL_RESTRICTION_MODIFIER_ID = Long.parseLong(Concepts.UNIVERSAL_RESTRICTION_MODIFIER);
	public static final long[] NEVER_GROUPED_ROLE_IDS = new long[] { PART_OF_ID, LATERALITY_ID, HAS_DOSE_FORM_ID, HAS_ACTIVE_INGREDIENT_ID };

	private LongConcepts() {
		// Prevent instantiation
	}
}