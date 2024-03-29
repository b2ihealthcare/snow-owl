/*
 * Copyright 2018-2020 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
final class SnomedOWLExpressionMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedOWLExpressionMemberUpdateDelegate(final SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(final SnomedRefSetMemberIndexEntry original, final SnomedRefSetMemberIndexEntry.Builder member, final TransactionContext context) {
		final String owlExpression = getProperty(SnomedRf2Headers.FIELD_OWL_EXPRESSION);

		if (!Strings.isNullOrEmpty(owlExpression) && !owlExpression.equals(original.getOwlExpression())) {
			SnomedOWLExpressionConverterResult result = context.service(SnomedOWLExpressionConverter.class).toSnomedOWLRelationships(original.getReferencedComponentId(), owlExpression);
				
			member
				.field(SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlExpression)
				.classAxiomRelationships(result.getClassAxiomRelationships())
				.gciAxiomRelationships(result.getGciAxiomRelationships());				
			
			return true;
		}

		return false;
	}

}
