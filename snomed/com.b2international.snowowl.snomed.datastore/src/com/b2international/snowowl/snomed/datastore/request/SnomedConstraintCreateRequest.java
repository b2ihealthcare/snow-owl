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

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;

/**
 * @since 6.5
 */
public final class SnomedConstraintCreateRequest implements Request<TransactionContext, String> {

	@NotNull
	private SnomedConstraint constraint;
	
	SnomedConstraintCreateRequest() {
	}
	
	void setConstraint(SnomedConstraint constraint) {
		this.constraint = constraint;
	}

	@Override
	public String execute(TransactionContext context) {
		try {
			context.lookup(constraint.getId(), SnomedConstraintDocument.class);
			throw new AlreadyExistsException("Attribute constraint", constraint.getId());
		} catch (NotFoundException e) {
			// ignore
		}
		
		final SnomedConstraintDocument.Builder newModel = SnomedConstraintDocument.builder();
		constraint.applyChangesTo(newModel);
		SnomedConstraintDocument model = newModel.build();
		context.add(model);
		return model.getId();
	}
}
