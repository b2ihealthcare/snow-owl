/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.validation;

import static com.google.common.collect.Sets.newHashSet;

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.base.Strings;

/**
 *
 */
public class SnomedOWLAxiomRefSetValidator extends SnomedRefSetValidator {

	private final Set<String> owlExpressionIsEmpty = newHashSet();

	public SnomedOWLAxiomRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.OWL_AXIOM_REFSET, context, SnomedRf2Headers.OWL_AXIOM_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		validateOWLExpression(row);
	}

	@Override
	protected void doValidate(final String effectiveTime, final IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.OWL_EXPRESSION_IS_EMPTY, owlExpressionIsEmpty);
		owlExpressionIsEmpty.clear();
	}

	@Override
	protected String getName() {
		return "owl axiom";
	}

	private void validateOWLExpression(final List<String> row) {
		final String owlExpression = row.get(6);
		if (Strings.isNullOrEmpty(owlExpression)) {
			final String uuid = row.get(0);
			final String effectiveTime = row.get(1);
			final String safeEffectiveTime = Strings.isNullOrEmpty(effectiveTime) ? EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL
					: EffectiveTimes.format(effectiveTime);
			owlExpressionIsEmpty.add(String.format(
					"OWL Axiom reference set member's '%s' OWL expression property is empty in effective time '%s'", uuid, safeEffectiveTime));
		}
	}
}
