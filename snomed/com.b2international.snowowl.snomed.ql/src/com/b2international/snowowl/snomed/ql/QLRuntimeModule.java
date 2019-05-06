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
package com.b2international.snowowl.snomed.ql;

import org.eclipse.xtext.conversion.IValueConverterService;

import com.b2international.snowowl.snomed.ecl.converter.EclValueConverterService;
import com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter;
import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.CaseSignificanceFilter;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.Filter;
import com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter;
import com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter;
import com.b2international.snowowl.snomed.ql.ql.ModuleFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedFilter;
import com.b2international.snowowl.snomed.ql.ql.PreferredInFilter;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;
import com.b2international.snowowl.snomed.ql.ql.TypeFilter;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class QLRuntimeModule extends AbstractQLRuntimeModule {

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return EclValueConverterService.class;
	}
	
	public static Domain getDomain(Filter constraint) {
		final Domain domain = getDomainInternal(constraint);
		return domain == null ? Domain.CONCEPT : domain;
	}
	
	private static Domain getDomainInternal(Filter constraint) {
		if (constraint instanceof NestedFilter) {
			return getDomain(((NestedFilter) constraint).getNested());
		} else if (constraint instanceof Conjunction) {
			Domain leftDomain = getDomain(((Conjunction) constraint).getLeft());
			Domain rightDomain = getDomain(((Conjunction) constraint).getRight());
			if (leftDomain != rightDomain) {
				throw new IllegalStateException("Constraint with inconsistent left and right domains.");
			}
			return leftDomain;
		} else if (constraint instanceof Disjunction) {
			Domain leftDomain = getDomain(((Disjunction) constraint).getLeft());
			Domain rightDomain = getDomain(((Disjunction) constraint).getRight());
			if (leftDomain != rightDomain) {
				throw new IllegalStateException("Constraint with inconsistent left and right domains.");
			}
			return leftDomain;
		} else if (constraint instanceof Exclusion) {
			Domain leftDomain = getDomain(((Exclusion) constraint).getLeft());
			Domain rightDomain = getDomain(((Exclusion) constraint).getRight());
			if (leftDomain != rightDomain) {
				throw new IllegalStateException("Constraint with inconsistent left and right domains.");
			}
			return leftDomain;
		} else if (constraint instanceof ActiveFilter) {
			return ((ActiveFilter) constraint).getDomain();
		} else if (constraint instanceof ModuleFilter) {
			return ((ModuleFilter) constraint).getDomain();
		} else if (constraint instanceof TypeFilter) {
			return Domain.DESCRIPTION;
		} else if (constraint instanceof TermFilter) {
			return Domain.DESCRIPTION;
		} else if (constraint instanceof PreferredInFilter) {
			return Domain.DESCRIPTION;
		} else if (constraint instanceof AcceptableInFilter) {
			return Domain.DESCRIPTION;
		} else if (constraint instanceof LanguageRefSetFilter) {
			return Domain.DESCRIPTION;
		} else if (constraint instanceof LanguageCodeFilter) {
			return Domain.DESCRIPTION;
		} else if (constraint instanceof CaseSignificanceFilter) {
			return Domain.DESCRIPTION;
		} else {
			throw new UnsupportedOperationException("Not implemented case: " + constraint);
		}
	}
	
}
