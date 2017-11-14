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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConcreteDomainConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedDescriptionConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipConstraint;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;

/**
 * @since 5.7
 */
final class SnomedConstraintConverter extends BaseResourceConverter<SnomedConstraintDocument, SnomedConstraint, SnomedConstraints> {

	protected SnomedConstraintConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedConstraints createCollectionResource(List<SnomedConstraint> results, String scrollId, Object[] searchAfter, int limit, int total) {
		return new SnomedConstraints(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected SnomedConstraint toResource(SnomedConstraintDocument entry) {
		final SnomedConstraint constraint;
		switch (entry.getPredicateType()) {
		case DATATYPE:
			final SnomedConcreteDomainConstraint cConstraint = new SnomedConcreteDomainConstraint();
			cConstraint.setCharacteristicTypeExpresion(entry.getCharacteristicTypeExpression());
			cConstraint.setTypeExpression(entry.getDataTypeName());
			cConstraint.setValueType(entry.getDataType());
			constraint = cConstraint;
			break;
		case DESCRIPTION:
			SnomedDescriptionConstraint dConstraint = new SnomedDescriptionConstraint();
			dConstraint.setTypeId(entry.getDescriptionTypeId());
			constraint = dConstraint;
			break;
		case RELATIONSHIP:
			SnomedRelationshipConstraint rConstraint = new SnomedRelationshipConstraint();
			rConstraint.setCharacteristicTypeExpression(entry.getCharacteristicTypeExpression());
			rConstraint.setType(entry.getRelationshipTypeExpression());
			rConstraint.setDestinationExpression(entry.getRelationshipValueExpression());
			rConstraint.setGroupRule(entry.getGroupRule());
			constraint = rConstraint;
			break;
		default: throw new UnsupportedOperationException("Unsupported MRCM constraint: " + entry.getPredicateType());
		}
		constraint.setId(entry.getId());
		constraint.setStorageKey(entry.getStorageKey());
		constraint.setDomain(entry.getDomain());
		constraint.setMinCardinality(entry.getMinCardinality());
		constraint.setMaxCardinality(entry.getMaxCardinality());
		constraint.setRefSetIds(entry.getRefSetIds());
		constraint.setSelfIds(entry.getSelfIds());
		constraint.setDescendantIds(entry.getDescendantIds());
		return constraint;
	}

}
