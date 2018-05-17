/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import java.util.Date;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public abstract class SnomedPredicate extends SnomedConceptModelComponent {

	@Override
	public abstract ConceptModelPredicate createModel();

	@Override
	public abstract ConceptModelPredicate applyChangesTo(ConceptModelComponent existingModel);

	@Override
	public abstract SnomedPredicate deepCopy(Date date, String userName);

	static String getCharacteristicTypeExpression(final String characteristicTypeId) {
		return Strings.isNullOrEmpty(characteristicTypeId) 
				? "<" + Concepts.CHARACTERISTIC_TYPE 
				: "<<" + characteristicTypeId;
	}
}
