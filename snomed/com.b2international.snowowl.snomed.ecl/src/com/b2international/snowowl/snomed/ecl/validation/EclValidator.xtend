/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.validation

import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement
import com.b2international.snowowl.snomed.ecl.ecl.EclPackage
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement
import com.b2international.snowowl.snomed.ecl.ecl.Refinement
import org.eclipse.xtext.validation.Check

/**
 * @since 5.4
 */
class EclValidator extends AbstractEclValidator {

	static String AMBIGUOUS_MESSAGE = "Ambiguous binary operator, use parenthesis to disambiguate the meaning of the expression"
	static String AMBIGUOUS_CODE = "binaryoperator.ambiguous"

	override isLanguageSpecific() {
		false
	}

	@Check
	def checkAmbiguity(AndExpressionConstraint it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__RIGHT, AMBIGUOUS_CODE)
		}
	}

	@Check
	def checkAmbiguity(OrExpressionConstraint it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__RIGHT, AMBIGUOUS_CODE)
		}
	}

	@Check
	def checkAmbiguity(ExclusionExpressionConstraint it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT, AMBIGUOUS_CODE)
		}
	}

	@Check
	def checkAmbiguity(OrRefinement it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_REFINEMENT__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_REFINEMENT__RIGHT, AMBIGUOUS_CODE)
		}
	}

	@Check
	def checkAmbiguity(AndRefinement it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_REFINEMENT__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_REFINEMENT__RIGHT, AMBIGUOUS_CODE)
		}
	}

	def private isAmbiguous(ExpressionConstraint parent, ExpressionConstraint child) {
		parent.class != child.class && (child instanceof AndExpressionConstraint || child instanceof OrExpressionConstraint || child instanceof ExclusionExpressionConstraint)
	}

	def private isAmbiguous(Refinement parent, Refinement child) {
		parent.class != child.class && (child instanceof AndRefinement || child instanceof OrRefinement)
	}

}