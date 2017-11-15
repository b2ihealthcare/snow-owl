/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.validation;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 6.0
 */
public final class SnomedEclValidationRuleEvaluator implements ValidationRuleEvaluator {

	@Override
	public List<ComponentIdentifier> eval(BranchContext context, ValidationRule rule) {
		checkArgument(type().equals(rule.getType()), "'%s' is not recognizable by this evaluator (accepts: %s)", rule, type());
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByEcl(rule.getImplementation())
				.setFields(SnomedConceptDocument.Fields.ID)
				.build()
				.execute(context)
				.stream()
				.map(concept -> ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.getId()))
				.collect(Collectors.toList());
	}

	@Override
	public String type() {
		return "snomed-ecl";
	}

}
