/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the \"License\");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.ql.validation

import com.b2international.snowowl.snomed.ql.ql.Conjunction
import com.b2international.snowowl.snomed.ql.ql.Constraint
import com.b2international.snowowl.snomed.ql.ql.Disjunction
import com.b2international.snowowl.snomed.ql.ql.EclFilter
import com.b2international.snowowl.snomed.ql.ql.Exclusion
import com.b2international.snowowl.snomed.ql.ql.QlPackage
import com.b2international.snowowl.snomed.ql.ql.TermFilter
import org.eclipse.xtext.validation.Check

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class QLValidator extends AbstractQLValidator {
	
	static String AMBIGUOUS_MESSAGE = "Ambiguous binary operator, use parenthesis to disambiguate the meaning of the expression"
	static String AMBIGUOUS_CODE = "binaryoperator.ambiguous"


	@Check
	def checkAmbiguity(Disjunction it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.DISJUNCTION__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.DISJUNCTION__RIGHT, AMBIGUOUS_CODE)
		}
	}
	
	@Check
	def checkAmbiguity(Conjunction it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__RIGHT, AMBIGUOUS_CODE)
		}
	}
	
	@Check
	def checkAmbiguity(Exclusion it) {
		if (isAmbiguous(left)) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, AMBIGUOUS_CODE)
		} else if (isAmbiguous(right)) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__RIGHT, AMBIGUOUS_CODE)
		}
	}
	
	@Check
	def checkShortTermFilter(TermFilter it) {
		if (it.term.length < 3) {
			error("Term filter too short", QlPackage.Literals.TERM_FILTER__TERM)
		}
	}
	
	@Check
	def checkEmptyEclFilter(EclFilter it) {
		if (it.ecl.constraint === null) {
			error("Ecl expression not specified", QlPackage.Literals.ECL_FILTER__ECL)
		}
	}
	
	def private isAmbiguous(Constraint parent, Constraint child) {
		parent.class != child.class && 
			(child instanceof Disjunction 
				|| child instanceof Conjunction 
				|| child instanceof Exclusion)
	}

}
