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
package com.b2international.snowowl.snomed.validation.detail;

import java.util.Collection;

import com.b2international.commons.options.Options;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.validation.issue.ValidationDetailExtension;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;

/**
 * @since 6.4
 */
public class SnomedValidationDetailExtension implements ValidationDetailExtension {

	@Override
	public void prepareQuery(ExpressionBuilder queryBuilder, Options options) {
		if (options.containsKey(SnomedRf2Headers.FIELD_ACTIVE)) {
			final Boolean isActive = options.get(SnomedRf2Headers.FIELD_ACTIVE, Boolean.class);
			queryBuilder.filter(Expressions.match(SnomedRf2Headers.FIELD_ACTIVE, isActive));
		}
		
		if (options.containsKey(SnomedRf2Headers.FIELD_MODULE_ID)) {
			final Collection<String> moduleIds = options.getCollection(SnomedRf2Headers.FIELD_MODULE_ID, String.class);
			if (moduleIds.isEmpty()) {
				return;
			}
			queryBuilder.filter(Expressions.matchAny(SnomedRf2Headers.FIELD_MODULE_ID, moduleIds));
		}
		
	}

}