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
package com.b2international.snowowl.snomed.datastore.snor;

import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.google.common.base.Predicate;

/**
 * Predicate implementation for filtering {@link ConstraintBase constraints} applicable for concept validation.
 * The current implementation checks whether:
 * <ul><li>the constraint form is either {@link ConstraintForm#ALL_FORMS} or {@link ConstraintForm#DISTRIBUTION_FORM}</li>
 * <li>the constraint strength is either {@link ConstraintStrength#MANDATORY_CM}, {@link ConstraintStrength#RECOMMENDED_CM}
 * or {@link ConstraintStrength#ADVISORY_CM}.</ul>
 * 
 */
public final class ConstraintFormIsApplicableForValidationPredicate implements Predicate<ConceptModelComponent> {
	@Override public boolean apply(ConceptModelComponent input) {
		if (input instanceof AttributeConstraint) {
			AttributeConstraint attributeConstraint = (AttributeConstraint) input;
			boolean isFormValid = ConstraintForm.ALL_FORMS.equals(attributeConstraint.getForm()) 
					|| ConstraintForm.DISTRIBUTION_FORM.equals(attributeConstraint.getForm());
			boolean isStrengthValid = ConstraintStrength.MANDATORY_CM.equals(attributeConstraint.getStrength())
					|| ConstraintStrength.ADVISORY_CM.equals(attributeConstraint.getStrength())
					|| ConstraintStrength.RECOMMENDED_CM.equals(attributeConstraint.getStrength());
			return isStrengthValid && isFormValid;
		}
		throw new IllegalArgumentException("Unexpected input: " + input);
	}
}