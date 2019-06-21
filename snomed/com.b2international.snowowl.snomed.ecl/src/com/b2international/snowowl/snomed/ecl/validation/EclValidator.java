/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.validation;

import org.eclipse.xtext.validation.Check;

import com.b2international.snowowl.snomed.ecl.Ecl;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class EclValidator extends AbstractEclValidator {
	
	private static final String AMBIGUOUS_MESSAGE = "Ambiguous binary operator, use parenthesis to disambiguate the meaning of the expression";
	private static final String AMBIGUOUS_CODE = "binaryoperator.ambiguous";
	
	private static final String CARDINALITY_RANGE_ERROR_MESSAGE = "Cardinality minimum value should not be greater than maximum value";
	private static final String CARDINALITY_RANGE_ERROR_CODE = "cardinality.range.error";

	@Override
	public boolean isLanguageSpecific() {
		return false;
	}

	@Check
	public void checkCardinality(Cardinality it) {
		if (it.getMax() != Ecl.MAX_CARDINALITY && it.getMin() > it.getMax()) {
			error(CARDINALITY_RANGE_ERROR_MESSAGE, it, EclPackage.Literals.CARDINALITY__MIN, CARDINALITY_RANGE_ERROR_CODE);
		}
	}
	
	@Check
	public void checkAmbiguity(AndExpressionConstraint it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__RIGHT, AMBIGUOUS_CODE);
		}
	}

	@Check
	public void checkAmbiguity(OrExpressionConstraint it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__RIGHT, AMBIGUOUS_CODE);
		}
	}

	@Check
	public void checkAmbiguity(ExclusionExpressionConstraint it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT, AMBIGUOUS_CODE);
		}
	}

	@Check
	public void checkAmbiguity(OrRefinement it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_REFINEMENT__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.OR_REFINEMENT__RIGHT, AMBIGUOUS_CODE);
		}
	}

	@Check
	public void checkAmbiguity(AndRefinement it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_REFINEMENT__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, EclPackage.Literals.AND_REFINEMENT__RIGHT, AMBIGUOUS_CODE);
		}
	}

	private boolean isAmbiguous(ExpressionConstraint parent, ExpressionConstraint child) {
		return parent.getClass() != child.getClass() && (child instanceof AndExpressionConstraint || child instanceof OrExpressionConstraint || child instanceof ExclusionExpressionConstraint);
	}

	private boolean isAmbiguous(Refinement parent, Refinement child) {
		return parent.getClass() != child.getClass() && (child instanceof AndRefinement || child instanceof OrRefinement);
	}
	
}
