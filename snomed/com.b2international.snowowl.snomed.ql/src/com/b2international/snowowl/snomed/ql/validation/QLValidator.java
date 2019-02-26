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
package com.b2international.snowowl.snomed.ql.validation;

import java.util.Locale;
import java.util.stream.Stream;

import org.eclipse.xtext.validation.Check;

import com.b2international.snowowl.snomed.ql.QLRuntimeModule;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.DomainQuery;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.Filter;
import com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class QLValidator extends AbstractQLValidator {

	public static final String AMBIGUOUS_MESSAGE = "Ambiguous binary operator, use parenthesis to disambiguate the meaning of the expression";
	public static final String AMBIGUOUS_CODE = "binaryoperator.ambiguous";
	
	public static final String DOMAIN_INCONSISTENCY_MESSAGE = "Inconsistent domains on left and right side of a binary operator, specify the domain (Concept, Description) the disambiguate the meaning of the expression";
	public static final String DOMAIN_INCONSISTENCY_CODE = "binaryoperator.inconsistentdomain";
	
	public static final String LANGUAGE_CODE_NONEXISITING_MESSAGE = "Non-existing ISO-639 Language Code";
	public static final String LANGUAGE_CODE_NONEXISITING_CODE = "languagecode.nonexisting";
	

	@Check
	public void checkSubQuery(DomainQuery it) {
		if (it.getEcl() == null && it.getFilter() != null) {
			error("ECL expression is required for domain refinement", QlPackage.Literals.DOMAIN_QUERY__ECL);
		}
	}
	
	@Check
	public void checkLanguageCodeFilter(LanguageCodeFilter it) {
		boolean existingLanguageCode = Stream.of(Locale.getISOLanguages())
			.filter(code -> code.equals(it.getLanguageCode()))
			.findFirst()
			.isPresent();
		if (!existingLanguageCode) {
			error(LANGUAGE_CODE_NONEXISITING_MESSAGE, it, QlPackage.Literals.LANGUAGE_CODE_FILTER__LANGUAGE_CODE, LANGUAGE_CODE_NONEXISITING_CODE);
		}
	}

	@Check
	public void checkDisjunction(Disjunction it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.DISJUNCTION__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.DISJUNCTION__RIGHT, AMBIGUOUS_CODE);
		}
		
		Domain leftDomain = QLRuntimeModule.getDomain(it.getLeft());
		Domain rightDomain = QLRuntimeModule.getDomain(it.getRight());
		
		if (leftDomain != rightDomain) {
			error(DOMAIN_INCONSISTENCY_MESSAGE, it, QlPackage.Literals.DISJUNCTION__LEFT, DOMAIN_INCONSISTENCY_CODE);
		}
		
	}
	
	@Check
	public void checkConjunction(Conjunction it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__RIGHT, AMBIGUOUS_CODE);
		}
		
		Domain leftDomain = QLRuntimeModule.getDomain(it.getLeft());
		Domain rightDomain = QLRuntimeModule.getDomain(it.getRight());
		
		if (leftDomain != rightDomain) {
			error(DOMAIN_INCONSISTENCY_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, DOMAIN_INCONSISTENCY_CODE);
		}
	}
	
	@Check
	public void checkExclusion(Exclusion it) {
		if (isAmbiguous(it, it.getLeft())) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__LEFT, AMBIGUOUS_CODE);
		} else if (isAmbiguous(it, it.getRight())) {
			error(AMBIGUOUS_MESSAGE, it, QlPackage.Literals.CONJUNCTION__RIGHT, AMBIGUOUS_CODE);
		}
		
		Domain leftDomain = QLRuntimeModule.getDomain(it.getLeft());
		Domain rightDomain = QLRuntimeModule.getDomain(it.getRight());
		
		if (leftDomain != rightDomain) {
			error(DOMAIN_INCONSISTENCY_MESSAGE, it, QlPackage.Literals.EXCLUSION__LEFT, DOMAIN_INCONSISTENCY_CODE);
		}
	}
	
	@Check
	public void checkShortTermFilter(TermFilter it) {
		int MIN_TERM_LENGTH = 2;
		if (it.getTerm().length() < MIN_TERM_LENGTH) {
			error(String.format("At least %d characters are required for term filter", MIN_TERM_LENGTH), QlPackage.Literals.TERM_FILTER__TERM);
		}
	}
	
	private boolean isAmbiguous(Filter parent, Filter child) {
		return parent.getClass() != child.getClass() && 
			(child instanceof Disjunction 
				|| child instanceof Conjunction 
				|| child instanceof Exclusion);
	}
	
}
