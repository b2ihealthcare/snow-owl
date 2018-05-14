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
package com.b2international.snowowl.snomed.datastore.request;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;

/**
 * @since 6.5
 */
public final class SnomedConstraintUpdateRequest implements Request<TransactionContext, Boolean> {

	@NotNull
	private SnomedConstraint constraint;
	
	SnomedConstraintUpdateRequest() {
	}
	
	void setConstraint(SnomedConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public Boolean execute(TransactionContext context) {
		final AttributeConstraint existingModel = context.lookup(constraint.getId(), AttributeConstraint.class);
		constraint.applyChangesTo(existingModel);
		return true;
	}
}
