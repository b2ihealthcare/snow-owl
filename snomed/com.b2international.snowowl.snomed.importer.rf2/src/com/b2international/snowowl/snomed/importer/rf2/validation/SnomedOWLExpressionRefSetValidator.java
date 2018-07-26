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
package com.b2international.snowowl.snomed.importer.rf2.validation;

import static com.google.common.collect.Lists.newArrayList;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;

/**
 * @since 6.5
 */
public class SnomedOWLExpressionRefSetValidator extends SnomedRefSetValidator {

	private List<String> defects = newArrayList();

	public SnomedOWLExpressionRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.OWL_EXPRESSION_REFSET, context, SnomedRf2Headers.OWL_EXPRESSION_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		validateRow(row);
	}

	@Override
	protected void doValidate(final String effectiveTime, final IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		if (!defects.isEmpty()) {
			addDefect(DefectType.EMPTY_REFSET_MEMBER_FIELD, defects);
		}
	}

	@Override
	protected String getName() {
		return "OWL Expression";
	}

	private void validateRow(final List<String> row) {
		validateNotEmptyFieldValue(row.get(6), SnomedRf2Headers.FIELD_OWL_EXPRESSION, row, defects);
	}
	
	@Override
	protected void clearCaches() {
		super.clearCaches();
		defects = newArrayList();
	}
}
