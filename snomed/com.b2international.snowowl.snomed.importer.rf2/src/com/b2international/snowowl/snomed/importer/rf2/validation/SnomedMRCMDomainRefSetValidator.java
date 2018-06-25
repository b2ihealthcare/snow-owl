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
public class SnomedMRCMDomainRefSetValidator extends SnomedRefSetValidator {

	private final List<String> defects = newArrayList();

	public SnomedMRCMDomainRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.MRCM_DOMAIN_REFSET, context, SnomedRf2Headers.MRCM_DOMAIN_HEADER);
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
		defects.clear();
	}

	@Override
	protected String getName() {
		return "MRCM Domain";
	}

	private void validateRow(final List<String> row) {

		// domainConstraint
		validateNotEmptyFieldValue(row.get(6), SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, row, defects);

		// parentDomain is optional

		// proximal primitive constraint
		validateNotEmptyFieldValue(row.get(8), SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, row, defects);

		// proximal primitive refinement is optional

		// domain template for precoordination
		validateNotEmptyFieldValue(row.get(10), SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, row, defects);

		// domain template for postcoordination
		validateNotEmptyFieldValue(row.get(11), SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, row, defects);

		// editorial guide reference is optional

	}

}
